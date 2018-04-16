package app.android.serv

import android.support.multidex.MultiDexApplication
import app.android.serv.model.User
import app.android.serv.util.RealmUtil
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import io.fabric.sdk.android.Fabric
import io.realm.Realm

/**
 * Created by kombo on 28/03/2018.
 */
class Serv: MultiDexApplication() {

    companion object {
        lateinit var INSTANCE: Serv
    }

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        Realm.init(this)
        Realm.setDefaultConfiguration(RealmUtil.getRealmConfig())

        Fabric.with(this, Crashlytics(), Answers())
//        Fabric.with(this, Answers())
    }
}