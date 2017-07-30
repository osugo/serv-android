package app.property.management.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import app.property.management.R

/**
 * Created by kombo on 30/07/2017.
 */

class InfoDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(getString(R.string.request_confirmation))
        builder.setPositiveButton(R.string.ok) { _, _ ->
            dismissAllowingStateLoss()
        }

        return builder.create()
    }
}