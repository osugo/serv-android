package app.property.management.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import app.property.management.R
import app.property.management.model.OfferedService
import app.property.management.model.Property
import app.property.management.model.User
import app.property.management.util.RealmUtil
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.Completable
import io.realm.Realm
import io.realm.RealmList
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.register.*
import org.jetbrains.anko.toast
import java.security.MessageDigest
import java.util.*
import kotlin.properties.Delegates

class Login : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private var realm: Realm by Delegates.notNull()
    private var googleApiClient: GoogleApiClient? = null
    private var progressDialog: ProgressDialog? = null
    private lateinit var callbackManager: CallbackManager

    companion object {
        val TAG: String = "Login"
        val RC_SIGN_IN = 9001
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.google, R.id.googleLogin -> signIn()
            R.id.facebook, R.id.facebookLogin -> {
                LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

                    override fun onError(error: FacebookException?) {
                        toast("An error occurred during login: " + error?.message)
                        hideProgressDialog()
                    }

                    override fun onCancel() {
                        toast("Facebook login cancelled")
                        hideProgressDialog()
                    }

                    override fun onSuccess(result: LoginResult?) {
                        val graphRequest: GraphRequest = GraphRequest.newMeRequest(result?.accessToken) { `object`, _ ->
                            val userId = `object`?.getString("id")
                            val firstName = `object`?.getString("first_name")
                            val lastName = `object`?.getString("last_name")
                            val email = `object`?.getString("email")
                            val profilePicture = "https://graph.facebook.com/$userId/picture?width=500&height=500"

                            val name = firstName + " " + lastName

                            createUser(userId!!, name, email!!, profilePicture)
                        }

                        val parameters = Bundle()
                        parameters.putString("fields", "id, first_name, last_name, email")
                        graphRequest.parameters = parameters
                        graphRequest.executeAsync()
                    }
                })

                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email"))
            }
            R.id.register -> registerUser(name.text.toString(), email.text.toString(), password.text.toString())
            R.id.login -> loginUser(userEmail.text.toString(), userPassword.text.toString())
            R.id.signUpText -> {
                login_layout.visibility = View.GONE
                registration_layout.visibility = View.VISIBLE
            }
            R.id.login_layout -> {
                registration_layout.visibility = View.GONE
                login_layout.visibility = View.VISIBLE
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        if (realm.where(User::class.java).findAll().isNotEmpty()) { //user is already logged in, proceed to app
            launchIntent()
        } else {
            val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
            googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build()

            callbackManager = CallbackManager.Factory.create()

            google.setSize(SignInButton.SIZE_STANDARD)
            google.setOnClickListener(this)
            googleLogin.setSize(SignInButton.SIZE_STANDARD)
            googleLogin.setOnClickListener(this)
            facebook.setOnClickListener(this)
            facebookLogin.setOnClickListener(this)

            signUpText.setOnClickListener(this)
            loginText.setOnClickListener(this)
        }
    }

    override fun onStart() {
        super.onStart()

        val opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient)
        if (opr.isDone) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in")
            val result = opr.get()
            handleSignInResult(result)
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog()
            opr.setResultCallback { googleSignInResult ->
                hideProgressDialog()
                handleSignInResult(googleSignInResult)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        hideProgressDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                Log.e(TAG, "onActivityResult")

                hideProgressDialog()

                val googleSignInResult: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                handleSignInResult(googleSignInResult)
            }
            else -> callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Use retrieved details to populate data on home page
     */
    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount

            val id: String? = acct?.id
            val name = acct?.displayName
            val email = acct?.email
            val picture = acct?.photoUrl

            Log.e("User", "$name is now logged in")

            createUser(id!!, name!!, email!!, picture.toString())
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private fun createUser(id: String, name: String, email: String, picture: String) {
        Completable.fromAction {
            try {
                realm.executeTransaction { r ->
                    val user = User(id, name, email, picture)
                    r.copyToRealmOrUpdate(user)
                }
            } catch (e: RealmException) {
                Log.e(TAG, e.message, e)
            }
        }.subscribe({
            addServices()
        }, { throwable ->
            Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
            Log.e(TAG, throwable.message, throwable)
        }
        )
    }

    private fun registerUser(username: String, useremail: String, userpass: String) {
        if (isEmpty(username)) {
            showSnackbarMessage("Please enter your name")
            return
        }

        if (isEmpty(useremail)) {
            showSnackbarMessage("Please provide your email address")
            return
        }

        if (isEmpty(userpass)) {
            showSnackbarMessage("Please create a password")
            return
        }

        if (!isEmpty(username) && !isEmpty(useremail) && !isEmpty(userpass)) {
            Completable.fromAction {
                try {
                    realm.executeTransaction { r ->

                        // convert the name to a SHA-256 string and use it as the ID to ensure consistency and availability of an id
                        val messageDigest = MessageDigest.getInstance("SHA-256")
                        messageDigest.update(username.toByte())
                        val id = String(messageDigest.digest())

                        val user = User(id, username, useremail, null, userpass, null)
                        r.copyToRealmOrUpdate(user)
                    }
                } catch (e: RealmException) {
                    Log.e(TAG, e.message, e)
                }
            }.subscribe({
                addServices()
            }, { throwable ->
                Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, throwable.message, throwable)
            }
            )
        }
    }

    private fun loginUser(useremail: String, userpass: String) {
        if (isEmpty(useremail)) {
            showSnackbarMessage("Please provide your email address")
            return
        }

        if (isEmpty(userpass)) {
            showSnackbarMessage("Please create a password")
            return
        }

        if (!isEmpty(useremail) && !isEmpty(userpass)) {
            val results = realm.where(User::class.java).equalTo("email", useremail).equalTo("password", userpass).findAll()

            if (results.isNotEmpty()) {
                addServices()
            } else {
                showSnackbarMessage("Sorry, user does not exist. Please check credentials")
            }
        }
    }

    private fun isEmpty(text: String): Boolean {
        return text.isNotBlank()
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun signIn() {
        showProgressDialog()

        val signInIntent: Intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStop() {
        super.onStop()

        progressDialog?.dismiss()
    }

    private fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog?.setMessage(getString(R.string.loading))
            progressDialog?.isIndeterminate = true
        }

        progressDialog?.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing)
            progressDialog?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

    private fun addServices() {
        val services: RealmList<OfferedService> = RealmList()
        services.add(OfferedService("Electrical Services", null, R.drawable.light_bulb))
        services.add(OfferedService("Lift Services", null, R.drawable.elevator))
        services.add(OfferedService("Plumbing Services", null, R.drawable.plumbing))
        services.add(OfferedService("Fumigation Services", null, R.drawable.fumigator))
        services.add(OfferedService("AC Maintenance Services", null, R.drawable.air_conditioner))
        services.add(OfferedService("Property Inspection Services", null, R.drawable.house_inspection))
        services.add(OfferedService("Handyman Services", null, R.drawable.handyman))
        services.add(OfferedService("Ground Maintenance Services", null, R.drawable.landscaping))

        Completable.fromAction({
            try {
                realm.executeTransaction { r -> r.copyToRealmOrUpdate(services) }
            } catch (ex: RealmException) {
                Log.e(TAG, ex.message, ex)
            }
        }).subscribe({
            launchIntent()
        })
    }

    private fun launchIntent() {
        if (realm.where(Property::class.java).findAll().isNotEmpty()) {
            startActivity(Intent(this, Properties::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        } else
            startActivity(Intent(this, ServiceChooser::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

        finish()
    }
}
