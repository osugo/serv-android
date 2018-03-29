package app.android.serv.activity

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.indeterminateProgressDialog

/**
 * Created by kombo on 28/03/2018.
 */
open class BaseActivity : AppCompatActivity() {

    protected var dialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (!isFinishing)
            dialog = indeterminateProgressDialog("Please wait...")
    }

    fun hideProgressDialog() {
        dialog?.dismiss()
    }
}