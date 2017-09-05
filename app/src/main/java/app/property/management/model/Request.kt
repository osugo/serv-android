package app.property.management.model

import io.realm.RealmObject

/**
 * Created by kombo on 24/08/2017.
 */
open class Request(
        private var service: OfferedService? = null,
        private var description: String? = null,
        private var time: String? = null) : RealmObject()