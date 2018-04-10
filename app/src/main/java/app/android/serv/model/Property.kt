package app.android.serv.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Property(

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("created_at")
        var createdAt: String? = null,

        @field:SerializedName("updated_at")
        var updatedAt: String? = null,

        @field:SerializedName("property_type_id")
        var propertyTypeId: String? = null,

        @field:SerializedName("longitude")
        var longitude: String? = null,

        @field:SerializedName("latitude")
        var latitude: String? = null,

        @PrimaryKey
        @field:SerializedName("id")
        var id: String? = null,

        @field:SerializedName("property_type_name")
        var propertyTypeName: String? = null
) : RealmObject()