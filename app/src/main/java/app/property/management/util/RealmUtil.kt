package app.property.management.util

import app.property.management.R
import io.realm.RealmConfiguration

/**
 * Created by kombo on 21/08/2017.
 */
object RealmUtil {

    public fun getRealmConfig(): RealmConfiguration {
        return RealmConfiguration.Builder()
                .name("House")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build()
    }
}