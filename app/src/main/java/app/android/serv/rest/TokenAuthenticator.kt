package app.android.serv.rest

import app.android.serv.R
import app.android.serv.Serv
import app.android.serv.model.RequestCredentials
import app.android.serv.model.UserCredentials
import app.android.serv.util.Commons
import app.android.serv.util.PrefUtils
import com.androidnetworking.AndroidNetworking
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
            val request = AndroidNetworking.post("http://serv.mtandao.space/oauth/token")
                    .addBodyParameter(RequestCredentials(Serv.instance.getString(R.string.client_id), Serv.instance.getString(R.string.client_secret), token, "refresh_token"))
                    .build()

            val response = request.executeForObject(UserCredentials::class.java)

            var userCredentials: UserCredentials? = null

            if (response.isSuccess)
                userCredentials = response.result as UserCredentials

            return if (userCredentials != null) {
                PrefUtils.putString(PrefUtils.CREDENTIALS, Gson().toJson(userCredentials))
                userCredentials
            } else {
                null
            }
        } else {
            return null
        }
    }
}