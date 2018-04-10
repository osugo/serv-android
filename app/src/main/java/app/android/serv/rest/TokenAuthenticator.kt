package app.android.serv.rest

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
            val restInterface = RestClient.client.create(RestInterface::class.java)

            val call = restInterface.refreshToken(
                    RequestCredentials(Serv.INSTANCE.getString(R.string.client_id), Serv.INSTANCE.getString(R.string.client_secret), Commons.credentials!!.refreshToken)
            )

            var userCredentials: UserCredentials? = null

            try {
                userCredentials = call.execute().body()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return if (userCredentials != null) {
                PrefUtils.putString(PrefUtils.CREDENTIALS, Gson().toJson(userCredentials))
                userCredentials
            } else
                null
        } else
            return null
    }
}