package app.android.serv

import android.support.multidex.MultiDexApplication
import app.android.serv.util.RealmUtil
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