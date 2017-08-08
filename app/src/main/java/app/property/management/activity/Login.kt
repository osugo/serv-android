package app.property.management.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import app.property.management.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private var googleApiClient: GoogleApiClient? = null
    private var progressDialog: ProgressDialog? = null

    companion object {
        val TAG: String = "Login"
        val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        googleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build()

        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener { onClickSignInButton }
    }

    private val onClickSignInButton = View.OnClickListener {
        signIn()
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
                val googleSignInResult: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                handleSignInResult(googleSignInResult)
            }
        }
    }

    /**
     * Use retrieved details to populate data on home page
     */
    private fun handleSignInResult(result: GoogleSignInResult){
        Log.d(TAG, "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount

            val name = acct?.displayName
            val email = acct?.email
            val picture = acct?.photoUrl

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    private fun signIn(){
        val signInIntent : Intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onStop(){
        super.onStop()

        progressDialog?.dismiss()
    }

    private fun showProgressDialog(){
        if(progressDialog == null){
            progressDialog = ProgressDialog(this)
            progressDialog?.setMessage(getString(R.string.loading))
            progressDialog?.isIndeterminate = true
        }

        progressDialog?.show()
    }

    private fun hideProgressDialog(){
        if(progressDialog != null && progressDialog!!.isShowing)
            progressDialog?.hide()
    }

}
