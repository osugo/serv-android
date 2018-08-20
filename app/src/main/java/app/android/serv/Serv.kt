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
import timber.log.Timber

/**
 * Created by kombo on 28/03/2018.
 */
class Serv : MultiDexApplication() {

    companion object {
        lateinit var instance: Serv
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        Realm.init(this)
        Realm.setDefaultConfiguration(RealmUtil.realmConfig)

        initLoggingTools()

        AndroidNetworking.initialize(applicationContext)
    }

    private fun initLoggingTools(){
        if (BuildConfig.DEBUG) {
            Fabric.with(this, Answers())

            Timber.uprootAll()
            Timber.plant(Timber.DebugTree())
        } else
            Fabric.with(this, Crashlytics(), Answers())
    }
}