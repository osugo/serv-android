package app.android.serv.rest

import app.android.serv.model.User
import app.android.serv.util.Commons
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
        val ragUser = refreshToken()

        return if (ragUser != null) {
            response.request().newBuilder().header("Authorization", "Bearer " + ragUser.accessToken).build()
        } else {
//            EventBus.getDefault().post(LogoutEvent("Token refresh failed. Logging out."))
            null
        }
    }

    private fun refreshToken(): User? {
        if (Commons.user?.refreshToken != null) {
//            val apiInterface = ApiClient.getClient().create(ApiInterface::class.java)
//            val call = apiInterface.refreshTokens(ClientRequest(Songa.getContext().getString(com.songa.music.R.string.client_id),
//                    com.songa.music.Songa.getContext().getString(R.string.client_secret),
//                    com.songa.music.Songa.getContext().getString(com.songa.music.R.string.grant_type), Songa.getRAGUser()?.refreshToken))
//
//            var ragUser: RAGUser? = null
//
//            try {
//                ragUser = call.execute().body()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            return if (ragUser != null) {
//                val gson = Gson()
//                val user = gson.toJson(ragUser)
//                PrefUtils.putString(Constants.USER, user)
//
//                ragUser
//            } else {
//                null
//            }
            return  null
        } else
            return null
    }
}