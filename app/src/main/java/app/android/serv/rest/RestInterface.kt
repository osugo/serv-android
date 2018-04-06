package app.android.serv.rest

import app.android.serv.model.*
import io.reactivex.Observable
import io.realm.RealmList
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by kombo on 28/03/2018.
 */
interface RestInterface {

    @POST("google/signin")
    fun signInWithGoogle(@Body clientCredentials: ClientCredentials): Observable<User>

    @GET("services")
    fun getServices(): Observable<ArrayList<Service>>

    @POST("oauth/token")
    fun refreshToken(@Body requestCredentials: RequestCredentials): Call<UserCredentials>

    @GET("property_types")
    fun getPropertyTypes(): Observable<RealmList<PropertyType>>

    @POST("properties")
    fun createProperty(@Body property: Property): Observable<Property>

    @PATCH("properties")
    fun updateProperty(@Body property: Property): Observable<Property>
}