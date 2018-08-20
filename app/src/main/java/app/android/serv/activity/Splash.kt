package app.android.serv.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import app.android.serv.R
import app.android.serv.util.Commons
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.splash.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor

/**
 * Created by kombo on 11/01/2018.
 */
class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        Glide.with(this).load(R.drawable.splash_image).into(background)

        Handler().postDelayed({ initialize() }, 3000)
    }

    private fun initialize() {
        if (Commons.user != null)
            startActivity(intentFor<DetailsActivity>().clearTop())
        else
            startActivity(intentFor<SignIn>().clearTop())

        finish()
    }
}