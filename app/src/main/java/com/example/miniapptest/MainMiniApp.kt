package com.example.miniapptest
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.miniapptest.helper.*
import com.rakuten.tech.mobile.miniapp.MiniApp
import com.rakuten.tech.mobile.miniapp.MiniAppDisplay
import com.rakuten.tech.mobile.miniapp.MiniAppSdkException
import com.rakuten.tech.mobile.miniapp.file.MiniAppFileDownloaderDefault
import com.rakuten.tech.mobile.miniapp.js.MiniAppMessageBridge
import com.rakuten.tech.mobile.miniapp.file.MiniAppCameraPermissionDispatcher
import com.rakuten.tech.mobile.miniapp.file.MiniAppFileChooserDefault
import com.rakuten.tech.mobile.miniapp.js.NativeEventType
import com.rakuten.tech.mobile.miniapp.navigator.ExternalResultHandler
import com.rakuten.tech.mobile.miniapp.permission.MiniAppDevicePermissionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MINI_APP_EXTERNAL_WEBVIEW_REQUEST_CODE = 100
private const val MINI_APP_FILE_CHOOSING_REQUEST_CODE = 10101
private const val MINI_APP_FILE_DOWNLOAD_REQUEST_CODE = 10102

class MainMiniApp : Activity(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main
    private var miniappPermissionCallback: (isGranted: Boolean) -> Unit = {}
    private var miniappCameraPermissionCallback: (isGranted: Boolean) -> Unit = {}
    private val miniAppFileDownloader = MiniAppFileDownloaderDefault(
        activity = this,
        requestCode = MINI_APP_FILE_DOWNLOAD_REQUEST_CODE
    )
    private val miniAppCameraPermissionDispatcher = object : MiniAppCameraPermissionDispatcher {
        override fun getCameraPermission(permissionCallback: (isGranted: Boolean) -> Unit) {
            if (ContextCompat.checkSelfPermission(
                    this@MainMiniApp,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permissionCallback(true)
            } else {
                permissionCallback(false)
            }
        }

        override fun requestCameraPermission(
            miniAppPermissionType: MiniAppDevicePermissionType,
            permissionRequestCallback: (isGranted: Boolean) -> Unit
        ) {
            miniappCameraPermissionCallback = permissionRequestCallback
            ActivityCompat.requestPermissions(
                this@MainMiniApp,
                AppDevicePermission.getDevicePermissionRequest(miniAppPermissionType),
                AppDevicePermission.getDeviceRequestCode(miniAppPermissionType)
            )
        }
    }
    private val miniAppFileChooser = MiniAppFileChooserDefault(
        requestCode = MINI_APP_FILE_CHOOSING_REQUEST_CODE,
        miniAppCameraPermissionDispatcher = miniAppCameraPermissionDispatcher

    )


    private lateinit var sampleWebViewExternalResultHandler: ExternalResultHandler
    private lateinit var miniAppDisplay: MiniAppDisplay
    private lateinit var createMiniAppBridge: MiniAppMessageBridge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(applicationContext, "Connecting to React Rakuten Mini App", Toast.LENGTH_SHORT).show()


        createMiniAppBridge = createMessageBridge(this){ onDevicePermissionResultCallback ->
            miniappPermissionCallback = onDevicePermissionResultCallback
        }

     //   val chatWindow = ChatWindow(this@MainMiniApp)
        val chatBridgeDispatcher = getChatBridgeDispatcher()

        val userInfoBridgeDispatcher = getUserInfoBridgeDispatcher()

        createMiniAppBridge.setChatBridgeDispatcher(chatBridgeDispatcher)
        createMiniAppBridge.setUserInfoBridgeDispatcher(userInfoBridgeDispatcher)
        createMiniAppBridge.setMiniAppFileDownloader(miniAppFileDownloader)
        createMiniAppBridge.allowScreenOrientation(true)
        //   createMiniAppBridge.setAdMobDisplayer(AdMobDisplayer(this@MainMiniApp))
        createMiniAppBridge.setMiniAppCloseListener { withConfirmationAlert ->
            if (withConfirmationAlert) checkCloseAlert() else finish()

        }
       /* CoroutineScope(Dispatchers.IO).launch {
            try {
                val miniAppInfo = MiniApp.instance().fetchInfo("abcd1234")


            } catch(e: MiniAppSdkException) {
                Log.e("MiniApp", "There was an error retrieving the list", e)
            }
        }*/
        launch {


            //for fetching mini app using url
            try {

                 miniAppDisplay = withContext(Dispatchers.IO) {
                       MiniApp.instance().createWithUrl(
                           appUrl = "https://ica.edu.np/Dems/API/miniapp/",
                           miniAppMessageBridge = createMiniAppBridge,
                           miniAppNavigator = null,
                           miniAppFileChooser = miniAppFileChooser,
                           queryParams = "try"
                       )}


                  val miniAppView = miniAppDisplay.getMiniAppView(this@MainMiniApp)
                  setContentView(miniAppView)

            } catch (e: MiniAppSdkException) {

                Log.e("MiniApp", "There was an error retrieving the list", e)
            }


        }



    }

    override fun onDestroy() {
        super.onDestroy()
        miniAppDisplay.destroyView()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handlePermissionResult(requestCode, grantResults)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Activity.RESULT_OK != resultCode) {
            miniAppFileChooser.onCancel()
            miniAppFileDownloader.onCancel()
        }

        when {
            requestCode == MINI_APP_EXTERNAL_WEBVIEW_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                data?.let { intent ->
                    val isClosedByBackPressed =
                        intent.getBooleanExtra("isClosedByBackPressed", false)
                    createMiniAppBridge.dispatchNativeEvent(
                        NativeEventType.EXTERNAL_WEBVIEW_CLOSE,
                        "External webview closed"
                    )
                    if (!isClosedByBackPressed) {
                        sampleWebViewExternalResultHandler.emitResult(intent)
                    }

                    finish()
                }
            }
            requestCode == MINI_APP_FILE_CHOOSING_REQUEST_CODE && resultCode == Activity.RESULT_OK -> {
                miniAppFileChooser.onReceivedFiles(data)
            }
            requestCode == MINI_APP_FILE_DOWNLOAD_REQUEST_CODE -> {
                data?.data?.let { destinationUri ->
                    miniAppFileDownloader.onReceivedResult(destinationUri)
                }
            }
        }
    }

    fun handlePermissionResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        val isGranted = !grantResults.contains(PackageManager.PERMISSION_DENIED)
        when (requestCode) {
            AppDevicePermission.ReqCode.CAMERA -> miniappCameraPermissionCallback.invoke(isGranted)
            else -> miniappPermissionCallback.invoke(isGranted)
        }
    }

    private fun checkCloseAlert() {
        try {
            val closeAlertInfo = createMiniAppBridge.miniAppShouldClose()
            if (closeAlertInfo?.shouldDisplay!!) {
                val dialogClickListener =
                    DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                finish()
                            }
                        }
                    }

                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainMiniApp)
                builder.setTitle(closeAlertInfo.title)
                    .setMessage(closeAlertInfo.description)
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
            } else finish()
        } catch (e: NullPointerException) {
            finish()
        }
    }

}