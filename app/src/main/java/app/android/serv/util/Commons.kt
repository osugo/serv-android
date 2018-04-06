package app.android.serv.util

import app.android.serv.model.User
import app.android.serv.model.UserCredentials
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by kombo on 28/03/2018.
 */
object Commons {

    val user: User?
        get() {
            val user = PrefUtils.getString(PrefUtils.USER, "")

            val gson = Gson()
            val type = object : TypeToken<User>() {}.type

            return gson.fromJson<User>(user, type)
        }

    val credentials: UserCredentials?
        get() {
            val userCredentials = PrefUtils.getString(PrefUtils.CREDENTIALS, "")

            val gson = Gson()
            val type = object : TypeToken<UserCredentials>() {}.type

            return gson.fromJson<UserCredentials>(userCredentials, type)
        }
}