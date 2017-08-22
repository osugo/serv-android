package app.property.management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 17/08/2017.
 */

open class Property(
        @PrimaryKey var id: Int = 1,
        var name: String? = null,
        var location: String? = null) : RealmObject() {
}