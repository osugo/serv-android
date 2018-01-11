package app.property.management.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import app.property.management.R
import app.property.management.model.User
import app.property.management.util.RealmUtil
import com.bumptech.glide.Glide
import io.realm.Realm
import kotlinx.android.synthetic.main.splash.*

/**
 * Created by kombo on 11/01/2018.
 */
class Splash: AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        Glide.with(this).load(R.drawable.splash_image).into(background)

        Handler().postDelayed({initialize()}, 500)
    }

    private fun initialize() {
        if(realm.where(User::class.java).findAll().isNotEmpty()) {
            startActivity(Intent(this, ServiceChooser::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        } else {
            startActivity(Intent(this, Login::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }

        finish()
    }
}