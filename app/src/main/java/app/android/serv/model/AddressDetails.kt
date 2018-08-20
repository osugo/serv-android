package app.android.serv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 09/08/2018.
 */
data class AddressDetails(
        @SerializedName("id")
        var id: String? = null,

        @SerializedName("latitude")
        var latitude: String? = null,

        @SerializedName("longitude")
        var longitude: String? = null,

        @SerializedName("addressable_id")
        var addressableId: String? = null
)