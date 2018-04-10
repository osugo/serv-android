package app.android.serv.rest

import app.android.serv.model.*
import io.reactivex.Observable
import io.realm.RealmList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

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

    @GET("properties")
    fun getProperties(): Observable<ArrayList<Property>>

    @POST("properties")
    fun createProperty(@Body property: Property): Observable<Property>

    @PATCH("properties")
    fun updateProperty(@Body property: Property): Observable<Property>

    @POST("requests")
    fun makeRequest(@Body request: Request): Observable<Request>

}