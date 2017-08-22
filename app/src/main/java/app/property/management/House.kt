package app.property.management

import android.app.Application
import app.property.management.util.RealmUtil
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
    }
}