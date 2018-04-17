package app.android.serv.rest

import android.util.Log
import app.android.serv.R
import app.android.serv.Serv
import app.android.serv.model.RequestCredentials
import app.android.serv.model.UserCredentials
import app.android.serv.util.Commons
import app.android.serv.util.PrefUtils
import com.google.gson.Gson
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

/**
 * Created by kombo on 28/03/2018.
 */
class TokenAuthenticator : Authenticator {

    @Synchronized
    @Throws(IOException::class)
    override fun authenticate(route: Route, response: Response): Request? {
        val credentials = refreshToken()

        return if (credentials != null) {
            response.request().newBuilder().header("Authorization", "Bearer " + credentials.accessToken).build()
        } else {
//            EventBus.getDefault().post(LogoutEvent("Token refresh failed. Logging out."))
            null
        }
    }

    private fun refreshToken(): UserCredentials? {
        val token = if (Commons.credentials == null) Commons.user?.refreshToken else Commons.credentials!!.refreshToken

        if (token != null) {

//            val request = AndroidNetworking.post("http://serv.mtandao.space/oauth/token")
//                    .addJSONObjectBody()
            Log.e("Token", token)
            val restInterface = RestClient.headerLessClient.create(RestInterface::class.java)

            val call = restInterface.refreshToken(
                    RequestCredentials(Serv.INSTANCE.getString(R.string.client_id), Serv.INSTANCE.getString(R.string.client_secret), token, "refresh_token")
            )

            Log.e("Refresh", "here")

            var userCredentials: UserCredentials? = null

            try {
                userCredentials = call.execute().body()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            Log.e("Credentials", if (userCredentials == null) "Is null" else "Is not null")

            return if (userCredentials != null) {
                Log.e("Credentials", "Is not null")
                PrefUtils.putString(PrefUtils.CREDENTIALS, Gson().toJson(userCredentials))
                userCredentials
            } else {
                Log.e("Credentials", "Is null")
                null
            }
        } else {
            Log.e("Refresh", "Token is null")
            return null
        }
    }
}