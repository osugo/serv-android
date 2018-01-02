package app.property.management.activity

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import app.property.management.R
import app.property.management.adapter.PlaceAutocompleteAdapter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.property_selection.*
import kotlinx.android.synthetic.main.toolbar.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener {

    private lateinit var map: GoogleMap

    private lateinit var mGeoDataClient: GeoDataClient
    private lateinit var mPlaceDetectionClient: PlaceDetectionClient

    // The entry point to the Fused Location Provider.
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val mDefaultLocation = LatLng(-33.8523341, 151.2106085)
    private val DEFAULT_ZOOM = 15
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var mLocationPermissionGranted: Boolean = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var mLastKnownLocation: Location? = null
    private var marker: Marker? = null

    // Used for selecting the current place.
    private val M_MAX_ENTRIES = 5
    private var mLikelyPlaceNames: Array<String>? = null
    private var mLikelyPlaceAddresses: Array<String>? = null
    private var mLikelyPlaceAttributions: Array<String>? = null
    private var mLikelyPlaceLatLngs: Array<LatLng>? = null

    // Obtain a client for use with Places API
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var adapter: PlaceAutocompleteAdapter

    companion object {
        val TAG: String = MapActivity::class.java.simpleName
        val SELECTED_SERVICE = "title"
        val PROPERTY = "name"
        // Keys for storing activity state.
        val KEY_CAMERA_POSITION = "camera_position"
        val KEY_LOCATION = "location"
        val BOUNDS_GREATER_SYDNEY = LatLngBounds(LatLng(-34.041458, 150.790100), LatLng(-33.682247, 151.383362));
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = adapter.getItem(position)
        val placeId = item.placeId
        val primaryText = item.getPrimaryText(null)

        Log.i(PropertySelection.TAG, "Autocomplete item selected: " + primaryText)

        /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
        val placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
        placeResult.setResultCallback(updatePlaceDetailsCallback)

        Toast.makeText(applicationContext, "Clicked: " + primaryText, Toast.LENGTH_SHORT).show()
        Log.i(PropertySelection.TAG, "Called getPlaceById to get Place details for " + placeId!!)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        //do nothing, connection failed
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.errorCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build()

        setContentView(R.layout.activity_properties)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null)

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        location.onItemClickListener = this

        adapter = PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS_GREATER_SYDNEY, null)
        location.setAdapter(adapter)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        map.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

            override// Return null here, so that getInfoContents() is called next.
            fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(R.layout.custom_info_components, findViewById<FrameLayout>(R.id.map), false)

                val title = infoWindow.findViewById(R.id.title) as TextView
                title.text = marker.title

                val snippet = infoWindow.findViewById(R.id.snippet) as TextView
                snippet.text = marker.snippet

                return infoWindow
            }
        })

        map.setOnMapLongClickListener { latLng -> showMarker(latLng!!, latLng.latitude.toString() + ", " + latLng.longitude.toString(), "") }

        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        if (map != null) {
            outState!!.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation)
            super.onSaveInstanceState(outState)
        }
    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private val updatePlaceDetailsCallback = ResultCallback<PlaceBuffer> { places ->
        if (!places.status.isSuccess) {
            Log.e(PropertySelection.TAG, "Place query did not complete. Error: " + places.status.toString())
            places.release()
            return@ResultCallback
        }

        val place = places.get(0)

        location.setText(place.name)

        Log.e(PropertySelection.TAG, place.address.toString())

        showMarker(place.latLng, place.name.toString(), place.address.toString())

//        // Format details of the place for display and show it in a TextView.
//        mPlaceDetailsText.setText(formatPlaceDetails(resources, place.name,
//                place.id, place.address, place.phoneNumber,
//                place.websiteUri))
//
//        // Display the third party attributions if set.
//        val thirdPartyAttribution = places.attributions
//        if (thirdPartyAttribution == null) {
//            mPlaceDetailsAttribution.setVisibility(View.GONE)
//        } else {
//            mPlaceDetailsAttribution.setVisibility(View.VISIBLE)
//            mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()))
//        }

        Log.i(PropertySelection.TAG, "Place details received: " + place.name)

        places.release()
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.result
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(mLastKnownLocation!!.latitude,
                                        mLastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM.toFloat()))
                        map.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
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
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.pick_place -> {
                showCurrentPlace()
                true
            }
            else -> false
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            val placeResult = mPlaceDetectionClient.getCurrentPlace(null)
            placeResult.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val likelyPlaces = task.result

                    // Set the count, handling cases where less than 5 entries are returned.
                    val count: Int
                    count = if (likelyPlaces.count < M_MAX_ENTRIES) {
                        likelyPlaces.count
                    } else {
                        M_MAX_ENTRIES
                    }

                    var i = 0
                    mLikelyPlaceNames = Array(count, { "" })
                    mLikelyPlaceAddresses = Array(count, { "" })
                    mLikelyPlaceAttributions = Array(count, { "" })
                    mLikelyPlaceLatLngs = Array(count, { LatLng(-33.8523341, 151.2106085) })

                    for (placeLikelihood in likelyPlaces) {
                        // Build a list of likely places to show the user.
                        mLikelyPlaceNames!![i] = placeLikelihood.place.name as String
                        mLikelyPlaceAddresses!![i] = placeLikelihood.place.address as String
//                        mLikelyPlaceAttributions!![i] = placeLikelihood.place
//                                .attributions as String
                        mLikelyPlaceLatLngs!![i] = placeLikelihood.place.latLng

                        i++
                        if (i > count - 1) {
                            break
                        }
                    }

                    // Release the place likelihood buffer, to avoid memory leaks.
                    likelyPlaces.release()

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    openPlacesDialog()

                } else {
                    Log.e(TAG, "Exception: %s", task.exception)
                }
            }
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            marker = map.addMarker(MarkerOptions()
                    .title("Default Location")
                    .position(mDefaultLocation)
                    .snippet("No location found because location permission is disabled"))

            // Prompt the user for permission.
            getLocationPermission()
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private fun openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        val listener = DialogInterface.OnClickListener { _, which ->
            // The "which" argument contains the position of the selected item.
            val markerLatLng = mLikelyPlaceLatLngs!![which]
            var markerSnippet = mLikelyPlaceAddresses!![which]
            markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions!![which]

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            marker = map.addMarker(MarkerOptions()
                    .title(mLikelyPlaceNames!![which])
                    .position(markerLatLng)
                    .snippet(markerSnippet))

            // Position the map's camera at the location of the marker.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng, DEFAULT_ZOOM.toFloat()))
        }

        // Display the dialog.
        AlertDialog.Builder(this)
                .setTitle("Pick a place")
                .setItems(mLikelyPlaceNames, listener)
                .show()
    }

    /**
     * Displays a marker on location selected from search results
     */
    private fun showMarker(latLng: LatLng, title: String, address: String) {
        // Add a marker for the selected place, with an info window
        // showing information about that place.
        marker = map.addMarker(MarkerOptions()
                .title(title)
                .position(latLng)
                .snippet(address))

        // Position the map's camera at the location of the marker.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat()))
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map.isMyLocationEnabled = false
                map.uiSettings.isMyLocationButtonEnabled = false
                mLastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }
}
