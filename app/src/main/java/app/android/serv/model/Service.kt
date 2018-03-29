package app.android.serv.model

import com.google.gson.annotations.SerializedName

data class Service(

        @SerializedName("updated_at")
        val updatedAt: String? = null,

        @SerializedName("name")
        val name: String? = null,

        @SerializedName("created_at")
        val createdAt: String? = null,

        @SerializedName("id")
        val id: String? = null,

        var icon: Int? = null
)