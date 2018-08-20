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

    @get:GET("services")
    val getServices: Observable<RealmList<Service>>

    @POST("oauth/token")
    fun refreshToken(@Body requestCredentials: RequestCredentials): Call<UserCredentials>

    @get:GET("property_types")
    val getPropertyTypes: Observable<RealmList<PropertyType>>

    @get:GET("properties")
    val getProperties: Observable<ArrayList<Property>>

    @get:GET("experts")
    val getExperts: Observable<ArrayList<Expert>>

    @POST("properties")
    fun createProperty(@Body property: Property): Observable<Property>

    @PATCH("properties")
    fun updateProperty(@Body property: Property): Observable<Property>

    @POST("requests")
    fun makeRequest(@Body request: Request): Observable<Request>

}