package app.property.management

import android.app.Application
import app.property.management.util.RealmUtil
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by kombo on 21/08/2017.
 */

class House : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Realm.setDefaultConfiguration(RealmUtil.getRealmConfig())

//        Fabric.with(this, Crashlytics(), Answers())
        Fabric.with(this, Answers())
    }
}