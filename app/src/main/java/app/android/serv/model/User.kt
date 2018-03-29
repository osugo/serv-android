package app.android.serv.model

import com.google.gson.annotations.SerializedName

data class User(

        @SerializedName("google_id")
        val googleId: String? = null,

        @SerializedName("roles")
        val roles: List<String?>? = null,

        @SerializedName("last_name")
        val lastName: String? = null,

        @SerializedName("created_at")
        val createdAt: String? = null,

        @SerializedName("token_type")
        val tokenType: String? = null,

        @SerializedName("picture")
        val picture: String? = null,

        @SerializedName("access_token")
        val accessToken: String? = null,

        @SerializedName("refresh_token")
        val refreshToken: String? = null,

        @SerializedName("updated_at")
        val updatedAt: String? = null,

        @SerializedName("phone_number")
        val phoneNumber: String? = null,

        @SerializedName("id")
        val id: String? = null,

        @SerializedName("first_name")
        val firstName: String? = null,

        @SerializedName("expires_in")
        val expiresIn: Int? = null,

        @SerializedName("email")
        val email: String? = null
)