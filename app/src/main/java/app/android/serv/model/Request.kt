package app.android.serv.model

import com.google.gson.annotations.SerializedName

data class Request(

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("property_id")
        val propertyId: String? = null,

        @field:SerializedName("user_id")
        val userId: String? = null,

        @SerializedName("date")
        val date: String? = null,

        @SerializedName("time")
        val time: String? = null,

        @field:SerializedName("service_id")
        val serviceId: String? = null,

        @field:SerializedName("service_name")
        val serviceName: String? = null,

        @field:SerializedName("user_name")
        val userName: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("property_name")
        val propertyName: String? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("start_date")
        val startDate: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
)