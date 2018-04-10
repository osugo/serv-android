package app.android.serv.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by kombo on 24/08/2017.
 */
open class OfferedService(
        @PrimaryKey var title: String? = null,
        var image: String? = null,
        var icon: Int? = null) : RealmObject()