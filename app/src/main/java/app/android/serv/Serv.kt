package app.android.serv

import android.support.multidex.MultiDexApplication
import app.android.serv.util.RealmUtil
import com.androidnetworking.AndroidNetworking
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import io.fabric.sdk.android.Fabric
import io.realm.Realm

/**
 * Created by kombo on 28/03/2018.
 */
class Serv : MultiDexApplication() {

    companion object {
        lateinit var INSTANCE: Serv
            private set
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        Realm.init(this)
        Realm.setDefaultConfiguration(RealmUtil.realmConfig)

        if (BuildConfig.DEBUG)
            Fabric.with(this, Answers())
        else
            Fabric.with(this, Crashlytics(), Answers())

        AndroidNetworking.initialize(applicationContext)
    }
}