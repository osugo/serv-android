package app.android.serv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 09/08/2018.
 */
data class Expert (
        @SerializedName("id")
        var id: String? = null,

        @SerializedName("first_name")
        var firstName: String? = null,

        @SerializedName("last_name")
        var lastName: String? = null,

        @SerializedName("phone_number")
        var phoneNumber: String? = null,

        @SerializedName("picture")
        var picture: String? = null,

        @SerializedName("address")
        var address: AddressDetails? = null,

        @SerializedName("services")
        var service: ArrayList<Service>? = null
)