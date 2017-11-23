package app.property.management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 24/08/2017.
 */
open class Request(
        @PrimaryKey
        var timestamp: Long? = null,
        var service: OfferedService? = null,
        var property: Property? = null,
        var description: String? = null,
        var date: String? = null,
        var time: String? = null) : RealmObject()