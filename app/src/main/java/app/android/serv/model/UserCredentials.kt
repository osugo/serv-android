package app.android.serv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by kombo on 06/04/2018.
 */
data class UserCredentials(
        @SerializedName("access_token")
        var accessToken: String? = null,
        @SerializedName("token_type")
        var tokenType: String? = null,
        @SerializedName("expires_in")
        var expiry: Long? = null,
        @SerializedName("refresh_token")
        var refreshToken: String? = null,
        @SerializedName("created_at")
        var createdAt: Long? = null
)