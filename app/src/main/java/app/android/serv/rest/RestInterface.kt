package app.android.serv.rest

import app.android.serv.model.ClientCredentials
import app.android.serv.model.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by kombo on 28/03/2018.
 */
interface RestInterface {

    @POST("google/signin")
    fun signInWithGoogle(@Body clientCredentials: ClientCredentials): Observable<User>
}