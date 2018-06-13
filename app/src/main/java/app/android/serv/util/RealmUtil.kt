package app.android.serv.util

import io.realm.RealmConfiguration

/**
 * Created by kombo on 21/08/2017.
 */
object RealmUtil {

    val realmConfig: RealmConfiguration
        get() = RealmConfiguration.Builder()
                .name("House")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build()
}