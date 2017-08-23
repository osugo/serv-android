package app.property.management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 17/08/2017.
 */

open class Property(
        @PrimaryKey var id: String?,
        var name: String? = null,
        var location: String? = null,
        var propertyType: String? = null
        ) : RealmObject()