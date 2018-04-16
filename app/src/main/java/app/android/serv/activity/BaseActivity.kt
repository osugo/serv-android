package app.android.serv.activity

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.indeterminateProgressDialog

/**
 * Created by kombo on 28/03/2018.
 */
abstract class BaseActivity : AppCompatActivity() {

    private var dialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (!isFinishing)
            dialog = indeterminateProgressDialog("Please wait...")
    }

    fun hideProgressDialog() {
        dialog?.dismiss()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}