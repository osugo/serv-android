package app.property.management.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 21/08/2017.
 */
open class User(
        @PrimaryKey var id: String? = null,
        var name: String? = null,
        var email: String? = null,
        var phone: String? = null,
        var password: String? = null,
        var photo: String? = null) : RealmObject()