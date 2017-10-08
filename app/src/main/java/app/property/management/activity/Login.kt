package app.property.management.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import app.property.management.R
import app.property.management.model.OfferedService
import app.property.management.model.User
import app.property.management.util.RealmUtil
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.properties.Delegates

class Login : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.sign_in_button -> signIn()
        }
    }

    private var realm: Realm by Delegates.notNull()

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private var googleApiClient: GoogleApiClient? = null
    private var progressDialog: ProgressDialog? = null

    companion object {
        val TAG: String = "Login"
        val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build()

        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener(this)

        background.setColorFilter(Color.parseColor("#50000000"), PorterDuff.Mode.ADD)

        Glide.with(this).load(R.drawable.apart_one).centerCrop().error(android.R.color.darker_gray).into(background)
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

            Completable.fromAction {
                try {
                    realm.executeTransaction { r ->
                        val user = User(id, name, email, picture.toString())
                        r.copyToRealmOrUpdate(user)
                    }
                } catch (e: RealmException) {
                    Log.e(TAG, e.message, e)
                }
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        addServices()
                    }, {
                        throwable ->
                        Toast.makeText(this, throwable.message, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, throwable.message, throwable)
                    }
                    )
        } else {
            // Signed out, show unauthenticated UI.
        }
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
        services.add(OfferedService("Electrical Services", null))
        services.add(OfferedService("Lift Services", null))
        services.add(OfferedService("Plumbing Services", null))
        services.add(OfferedService("Fumigation Services", null))
        services.add(OfferedService("AC Maintenance Services", null))
        services.add(OfferedService("Property Inspection Services", null))
        services.add(OfferedService("Handyman Services", null))
        services.add(OfferedService("Ground Maintenance Services", null))

        try {
            realm.executeTransaction { r -> r.copyToRealmOrUpdate(services) }
        } catch (ex: RealmException) {
            Log.e(TAG, ex.message, ex)
        } finally {
            startActivity(Intent(this, PropertySelection::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
}
