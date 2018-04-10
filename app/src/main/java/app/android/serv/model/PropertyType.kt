package app.android.serv.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PropertyType(

        @field:SerializedName("updated_at")
        var updatedAt: String? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @PrimaryKey
        @field:SerializedName("id")
        var id: String? = null
) : RealmObject()