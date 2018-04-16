package app.android.serv.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import app.android.serv.R

/**
 * Created by kombo on 30/07/2017.
 */

class InfoDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setMessage(getString(R.string.request_confirmation))
        builder.setPositiveButton(R.string.ok) { _, _ ->
            dismissAllowingStateLoss()
        }

        return builder.create()
    }
}