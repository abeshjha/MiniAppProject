import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import com.example.miniapptest.R

fun Context.showToastMessage(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, text, duration).show()
}

val Context?.isAvailable: Boolean
    get() {
        if (this == null) {
            return false
        } else if (this !is Application) {
            return this is Activity && (!this.isDestroyed && !this.isFinishing)
        }
        return true
    }

fun showAlertDialog(
    activity: Activity,
    title: String = "Alert",
    content: String,
    negativeButton: String = "Close"
) {
    // prepare an EditText where the content can be copied by long press
    val editText = EditText(activity)
    editText.setText(content)
    editText.setTextIsSelectable(true)
    editText.inputType = InputType.TYPE_NULL
    editText.isSingleLine = false
    editText.background = null
    val container = FrameLayout(activity)
    val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.topMargin = activity.resources.getDimensionPixelSize(R.dimen.medium_16)
    params.bottomMargin = activity.resources.getDimensionPixelSize(R.dimen.medium_16)
    params.leftMargin = activity.resources.getDimensionPixelSize(R.dimen.medium_16)
    params.rightMargin = activity.resources.getDimensionPixelSize(R.dimen.medium_16)
    editText.layoutParams = params
    container.addView(editText)

    // show content using alert dialog
    val alertDialog = AlertDialog.Builder(activity)
    alertDialog.setTitle(title)
    alertDialog.setView(container)
    alertDialog.setNegativeButton(negativeButton) { dialog, _ ->
        dialog.dismiss()
    }
    alertDialog.create().show()
}