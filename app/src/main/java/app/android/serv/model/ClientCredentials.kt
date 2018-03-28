package app.android.serv.model

import com.google.gson.annotations.SerializedName

data class ClientCredentials(
        @SerializedName("client_id")
        val clientId: String? = null,
        @SerializedName("token")
        val token: String? = null
)
