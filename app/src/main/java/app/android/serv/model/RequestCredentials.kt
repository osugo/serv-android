package app.android.serv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 06/04/2018.
 */
class RequestCredentials(
        @SerializedName("client_id")
        var clientId: String? = null,
        @SerializedName("client_secret")
        var clientSecret: String? = null,
        @SerializedName("refresh_token")
        val refreshToken: String? = null,
        @SerializedName("grant_type")
        val grantType: String = "refresh_token"
)