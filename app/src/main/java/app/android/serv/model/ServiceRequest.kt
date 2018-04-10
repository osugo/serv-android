package app.android.serv.model

import com.google.gson.annotations.SerializedName

data class ServiceRequest(

        @field:SerializedName("date")
        val date: String? = null,

        @field:SerializedName("user_id")
        val userId: String? = null,

        @field:SerializedName("service_id")
        val serviceId: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("time")
        val time: String? = null,

        @field:SerializedName("property_id")
        val propertyId: String? = null
)