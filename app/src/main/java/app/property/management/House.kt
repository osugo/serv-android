package app.property.management

import android.content.Context
import android.support.multidex.MultiDexApplication
import app.property.management.util.RealmUtil
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import io.fabric.sdk.android.Fabric
import io.realm.Realm

/**
 * Created by kombo on 21/08/2017.
 */

class House : MultiDexApplication() {

    public lateinit var INSTANCE: Context

    override fun onCreate() {
        super.onCreate()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        INSTANCE = this

        Realm.init(this)
        Realm.setDefaultConfiguration(RealmUtil.getRealmConfig())

//        Fabric.with(this, Crashlytics(), Answers())
        Fabric.with(this, Answers())
    }

    public fun getContext(): Context = INSTANCE
}