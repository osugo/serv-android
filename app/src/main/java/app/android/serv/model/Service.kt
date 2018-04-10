package app.android.serv.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Service(

        @SerializedName("updated_at")
        var updatedAt: String? = null,

        @SerializedName("name")
        var name: String? = null,

        @SerializedName("created_at")
        var createdAt: String? = null,

        @PrimaryKey
        @SerializedName("id")
        var id: String? = null,

        var icon: Int? = null
): RealmObject()