package app.android.serv.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import app.android.serv.R
import app.android.serv.event.ErrorEvent
import app.android.serv.model.ClientCredentials
import app.android.serv.rest.ErrorHandler
import app.android.serv.rest.RestClient
import app.android.serv.rest.RestInterface
import app.android.serv.util.NetworkHelper
import app.android.serv.util.PrefUtils
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.sign_in.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 28/03/2018.
 */
class SignIn : BaseActivity(), GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private val disposable = CompositeDisposable()
    private var googleApiClient: GoogleApiClient? = null
    private var locationPermissionGranted: Boolean = false

    private val restInterface by lazy {
        RestClient.headerLessClient.create(RestInterface::class.java)
    }

    private val googleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .requestProfile()
                .build()
    }

    companion object {
        private val TAG: String = SignIn::class.java.simpleName
        private const val GOOGLE_LOGIN = 1
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 23
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!isFinishing) {
            hideProgressDialog()

            alert(errorEvent.message) {
                yesButton {
                    it.dismiss()
                }
            }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        Glide.with(this).load(R.drawable.splash_image).into(background)

        getLocationPermission()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build()

        google.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.google -> {
                if (NetworkHelper.isOnline(this)) {
                    showProgressDialog()
                    startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), GOOGLE_LOGIN)
                } else
                    snackbar(parentLayout, getString(R.string.network_unavailable))
            }
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                GOOGLE_LOGIN -> {
                    handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.localizedMessage, e)
        }
    }

    override fun onStart() {
        super.onStart()
        val operationalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient)

        if (operationalPendingResult.isDone)
            handleSignInResult(operationalPendingResult.get())
        else
            operationalPendingResult.setResultCallback {
                hideProgressDialog()
                handleSignInResult(it)
            }
    }

    private fun handleSignInResult(googleSignInResult: GoogleSignInResult) {
        if (googleSignInResult.isSuccess) {
            googleSignInResult.signInAccount?.let {
                getUser(it.idToken)
            }
        } else {
            Log.e(TAG, "${googleSignInResult.status.statusCode}")
        }
    }

    private fun getUser(idToken: String?) {
        idToken?.let {
            showProgressDialog()
            disposable.add(
                    restInterface.signInWithGoogle(ClientCredentials(getString(R.string.serv_client_id), it))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                hideProgressDialog()
                                PrefUtils.putString(PrefUtils.USER, Gson().toJson(it))

                                startActivity(intentFor<ServiceChooser>().clearTop())
                            }) {
                                ErrorHandler.showError(it)
                            }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        hideProgressDialog()
        disposable.clear()
    }
}