package app.property.management.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 24/08/2017.
 */
open class Request(@PrimaryKey private var id: Int = 1, private var services: RealmList<OfferedService>) : RealmObject()