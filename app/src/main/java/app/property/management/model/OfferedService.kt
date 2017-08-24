package app.property.management.model

import io.realm.RealmObject

/**
 * Created by kombo on 24/08/2017.
 */
open class OfferedService(var service: String? = null, var image: String? = null) : RealmObject()