package com.example.miniapptest.helper

import android.app.Activity
import androidx.core.app.ActivityCompat
import com.rakuten.tech.mobile.miniapp.errors.MiniAppAccessTokenError
import com.rakuten.tech.mobile.miniapp.errors.MiniAppPointsError
import com.rakuten.tech.mobile.miniapp.js.MessageToContact
import com.rakuten.tech.mobile.miniapp.js.MiniAppMessageBridge
import com.rakuten.tech.mobile.miniapp.js.chat.ChatBridgeDispatcher
import com.rakuten.tech.mobile.miniapp.js.userinfo.Contact
import com.rakuten.tech.mobile.miniapp.js.userinfo.Points
import com.rakuten.tech.mobile.miniapp.js.userinfo.TokenData
import com.rakuten.tech.mobile.miniapp.js.userinfo.UserInfoBridgeDispatcher
import com.rakuten.tech.mobile.miniapp.permission.AccessTokenScope
import com.rakuten.tech.mobile.miniapp.permission.MiniAppDevicePermissionType
import isAvailable
import showToastMessage

//----------- mini app message bridge------------
fun createMessageBridge(
    activity: Activity,
    onDevicePermissionResultCallback: ((Boolean) -> Unit) -> Unit,
) = object : MiniAppMessageBridge() {

    override fun getUniqueId(
        onSuccess: (uniqueId: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val appSettings = AppSettings(activity,)
        val errorMsg = appSettings.uniqueIdError
        if (errorMsg.isNotEmpty()) onError(errorMsg)
        else onSuccess(appSettings.uniqueId)
    }

    override fun getMessagingUniqueId(
        onSuccess: (uniqueId: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val appSettings = AppSettings(activity,)
        val errorMsg = appSettings.messagingUniqueIdError
        if (errorMsg.isNotEmpty()) onError(errorMsg)
        else onSuccess("TEST-MESSAGE_UNIQUE-ID-01234")
    }

    override fun getMauid(
        onSuccess: (mauid: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val appSettings = AppSettings(activity,)
        val errorMsg = appSettings.mauIdError
        if (errorMsg.isNotEmpty()) onError(errorMsg)
        else onSuccess("TEST-MAUID-01234-56789")
    }

    override fun sendJsonToHostApp(
        jsonStr: String,
        onSuccess: (String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        jsonStr.let {
            val message: String
            if (it.isNotBlank()) {
                message = it
                onSuccess(message)
            } else {
                message =
                    "error sending json to the host app"
                onError(message)
            }
            if (activity.isAvailable) {
                activity.showToastMessage(message)
            }
        }
    }

    override fun requestDevicePermission(
        miniAppPermissionType: MiniAppDevicePermissionType,
        callback: (isGranted: Boolean) -> Unit
    ) {


        onDevicePermissionResultCallback.invoke(callback)
        ActivityCompat.requestPermissions(
            activity,
            AppDevicePermission.getDevicePermissionRequest(miniAppPermissionType),
            AppDevicePermission.getDeviceRequestCode(miniAppPermissionType)
        )

    }



}

//---------------- mini app user info bridge dispatcher-------
fun getUserInfoBridgeDispatcher() = object : UserInfoBridgeDispatcher {

    override fun getUserName(
        onSuccess: (userName: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val name = AppSettings.instance.profileName
        if (name.isNotEmpty()) onSuccess(name)
        else onError("User name is not found.")
    }

    override fun getProfilePhoto(
        onSuccess: (profilePhoto: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val photo = AppSettings.instance.profilePictureUrlBase64
        if (photo.isNotEmpty()) onSuccess(photo)
        else onError("Profile photo is not found.")
    }

    /*override fun getAccessToken(
        miniAppId: String,
        accessTokenScope: AccessTokenScope,
        onSuccess: (tokenData: TokenData) -> Unit,
        onError: (tokenError: MiniAppAccessTokenError) -> Unit
    ) {
        if (AppSettings.instance.accessTokenError != null) {
            onError(AppSettings.instance.accessTokenError!!)
        } else {
            onSuccess(AppSettings.instance.tokenData)
        }
    }

    override fun getContacts(
        onSuccess: (contacts: ArrayList<Contact>) -> Unit,
        onError: (message: String) -> Unit
    ) {
        if (AppSettings.instance.isContactsSaved)
            onSuccess(AppSettings.instance.contacts)
        else
            onError("There is no contact found in HostApp.")
    }

    override fun getPoints(
        onSuccess: (points: Points) -> Unit,
        onError: (pointsError: MiniAppPointsError) -> Unit
    ) {
        val points = AppSettings.instance.points
        if (points != null) onSuccess(points)
        else onError(MiniAppPointsError.custom("There is no points found in HostApp."))
    }*/
}

//---------------- mini app user contact-------

fun getChatBridgeDispatcher() =
    object : ChatBridgeDispatcher {

        override fun sendMessageToContact(
            message: MessageToContact,
            onSuccess: (contactId: String?) -> Unit,
            onError: (message: String) -> Unit
        ) {
           // chatWindow.openSingleContactSelection(message, onSuccess, onError)
        }

        override fun sendMessageToContactId(
            contactId: String,
            message: MessageToContact,
            onSuccess: (contactId: String?) -> Unit,
            onError: (message: String) -> Unit
        ) {
           // chatWindow.openSpecificContactIdSelection(contactId, message, onSuccess, onError)
        }

        override fun sendMessageToMultipleContacts(
            message: MessageToContact,
            onSuccess: (contactIds: List<String>?) -> Unit,
            onError: (message: String) -> Unit
        ) {
           // chatWindow.openMultipleContactSelections(message, onSuccess, onError)
        }
    }
