package app.android.serv.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import app.android.serv.Constants
import app.android.serv.R
import app.android.serv.adapter.ServicesAdapter
import app.android.serv.event.ErrorEvent
import app.android.serv.model.Expert
import app.android.serv.model.Property
import app.android.serv.model.PropertyType
import app.android.serv.model.Service
import app.android.serv.rest.ErrorHandler
import app.android.serv.rest.RestClient
import app.android.serv.rest.RestInterface
import app.android.serv.util.NetworkHelper
import app.android.serv.util.RealmUtil
import app.android.serv.viewmodel.ExpertsViewModel
import app.android.serv.viewmodel.ServicesViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.*
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_properties.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import timber.log.Timber

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

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
//    private lateinit var adapter: PlaceAutocompleteAdapter

    private var name: String? = null
    private var propertyLocation: LatLng? = null
    private var serviceId: String? = null
    private var dialog: ProgressDialog? = null
    private var propertyTypes: RealmList<PropertyType>? = null

    private val autocomplteteFilter = AutocompleteFilter.Builder().setTypeFilter(Place.TYPE_COUNTRY).setCountry("KE").build()

    private val disposable = CompositeDisposable()

    private val realm by lazy {
        Realm.getInstance(RealmUtil.realmConfig)
    }

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    private var servicesAdapter: ServicesAdapter? = null

    private val icons = intArrayOf(
            R.drawable.light_bulb,
            R.drawable.elevator,
            R.drawable.plumbing,
            R.drawable.fumigator,
            R.drawable.air_conditioner,
            R.drawable.house_inspection,
            R.drawable.handyman,
            R.drawable.landscaping
    )

    companion object {
        val TAG: String = MapActivity::class.java.simpleName
        // Keys for storing activity state.
        const val KEY_CAMERA_POSITION = "camera_position"
        const val KEY_LOCATION = "location"
    }

//    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        val item = adapter.getItem(position)
//        val placeId = item.placeId
//        val primaryText = item.getPrimaryText(null)
//
//        Log.i(TAG, "Autocomplete item selected: $primaryText")
//
//        /*
//             Issue a request to the Places Geo Data API to retrieve a Place object with additional
//             details about the place.
//              */
//        val placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
//        placeResult.setResultCallback(updatePlaceDetailsCallback)
//
//        Log.i(TAG, "Called getPlaceById to get Place details for " + placeId!!)
//    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        //do nothing, connection failed
        Timber.e("onConnectionFailed: ConnectionResult.getErrorCode() = %s", connectionResult.errorCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build()

        setContentView(R.layout.activity_properties)

        serviceId = intent.getStringExtra(Constants.SERVICE_ID)

//        setSupportActionBar(toolbar)

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this)

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

//        location.onItemClickListener = this

//        adapter = PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS_GREATER_SYDNEY, null)
//        location.setAdapter(adapter)

        //add a 500ms delay to prevent the activity from freezing up before the map loads
        Handler().postDelayed({
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }, 500)

        done.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done))
        done.setOnClickListener {
            showPropertyTypes()
        }

        setUpAutoCompleteFragment()

        initServices()
    }

    private fun setUpAutoCompleteFragment() {
        val autoCompleteFragment = fragmentManager.findFragmentById(R.id.places_autocomplete_fragment) as PlaceAutocompleteFragment
        autoCompleteFragment.setFilter(autocomplteteFilter)
        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place?) {
                hideKeyboard()

                place?.let {
                    Timber.i(it.address.toString())

                    showMarker(it.latLng, it.name.toString(), it.address.toString())

                    Timber.i( "Place details received: ${it.name}")
                }

            }

            override fun onError(status: Status?) {
                toast("An error occurred $status")
            }
        })
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

        try {
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

            if(!success)
                Timber.e("Style parsing failed")
        } catch (e: Resources.NotFoundException){
            Timber.e("Can't find style ${e.localizedMessage}")
        }

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
        outState!!.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
        outState.putParcelable(KEY_LOCATION, mLastKnownLocation)
        super.onSaveInstanceState(outState)
    }

//    /**w
//     * Callback for results from a Places Geo Data API query that shows the first place result in
//     * the details view on screen.
//     */
//    private val updatePlaceDetailsCallback = ResultCallback<PlaceBuffer> { places ->
//        if (!places.status.isSuccess) {
//            Log.e(TAG, "Place query did not complete. Error: " + places.status.toString())
//            places.release()
//            return@ResultCallback
//        }
//
//        val place = places.get(0)
//
////        location.setText(place.name)
////        location.dismissDropDown()
//
//        hideKeyboard()
//
//        Log.e(TAG, place.address.toString())
//
//        showMarker(place.latLng, place.name.toString(), place.address.toString())
//
//        Log.i(TAG, "Place details received: " + place.name)
//
//        places.release()
//    }

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

                        mLastKnownLocation?.let {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), DEFAULT_ZOOM.toFloat()))
                        }
                    } else {
                        Timber.d( "Exception: %s", task.exception)
                        Timber.d( "Current location is null. Using defaults.")
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM.toFloat()))
                        map.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
           Timber.e("Exception: %s", e.message)
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
//            R.id.pick_place -> {
//                showCurrentPlace()
//                true
//            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.done -> {
                showPropertyTypes()
                true
            }
            else -> false
        }
    }

    /**
     * Show the list of property types to select from
     */
    private fun showPropertyTypes() {
        if (!name.isNullOrEmpty()) {
            val results = realm.where(PropertyType::class.java).findAll()

            if (results.isNotEmpty()) {
                val list = RealmList<PropertyType>()
                list.addAll(results)

                propertyTypes = list

                showTypes(results.map { it.name!! })
            } else if (NetworkHelper.isOnline(this)) {
                if (!isFinishing) {
                    showProgressDialog()

                    disposable.add(
                            restInterface.getPropertyTypes
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        hideProgressDialog()
                                        propertyTypes = it

                                        savePropertyTypesToRealm(it)
                                        showTypes(it.map { it.name!! })
                                    }) {
                                        hideProgressDialog()
                                        ErrorHandler.showError(it)
                                    }
                    )
                }
            } else {
                snackbar(parentLayout, getString(R.string.network_unavailable))
            }
        } else {
            snackbar(parentLayout, "Please select a property to proceed")
        }
    }

    private fun savePropertyTypesToRealm(propertyTypes: RealmList<PropertyType>) {
        Realm.getInstance(RealmUtil.realmConfig).use {
            it.executeTransaction {
                it.copyToRealmOrUpdate(propertyTypes)
            }
        }
    }

    private fun showTypes(types: List<String>) {
        selector("Property Type", types) { _, i ->
            createProperty(getPropertyTypeId(types[i])!!)
        }
    }

    private fun createProperty(propertyTypeId: String) {
        if (NetworkHelper.isOnline(this)) {
            if (!isFinishing && propertyLocation != null) {
                showProgressDialog()

                disposable.add(
                        restInterface.createProperty(
                                Property(name.toString(), null, null, propertyTypeId,
                                        propertyLocation!!.longitude.toString(), propertyLocation!!.latitude.toString(), null, null))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ property ->
                                    Realm.getInstance(RealmUtil.realmConfig).use {
                                        it.executeTransaction {
                                            it.copyToRealmOrUpdate(property)
                                        }
                                    }

                                    hideProgressDialog()

                                    startActivity(intentFor<Details>(Constants.PROPERTY_ID to property.id, Constants.SERVICE_ID to serviceId))
                                }) {
                                    hideProgressDialog()
                                    ErrorHandler.showError(it)
                                }
                )
            }
        } else {
            snackbar(parentLayout, getString(R.string.network_unavailable))
        }
    }

    private fun getPropertyTypeId(name: String): String? {
        var id: String? = null

        Realm.getInstance(RealmUtil.realmConfig).use {
            val result = it.where(PropertyType::class.java).equalTo("name", name).findFirst()

            if (result != null)
                id = it.copyFromRealm(result).id
        }

        return id
    }

    private fun showProgressDialog() {
        dialog = indeterminateProgressDialog("Please wait...")
    }

    private fun hideProgressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    @SuppressLint("RestrictedApi")
    private fun showCurrentPlace() {
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
                    mLikelyPlaceNames = Array(count) { "" }
                    mLikelyPlaceAddresses = Array(count) { "" }
                    mLikelyPlaceAttributions = Array(count) { "" }
                    mLikelyPlaceLatLngs = Array(count) { LatLng(-33.8523341, 151.2106085) }

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

        name = title
        propertyLocation = latLng
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateLocationUI() {
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
            Timber.e("Exception: %s", e.message)
        }
    }

    private fun initServices(){
        servicesAdapter = ServicesAdapter(this)
        servicesRecycler.adapter = servicesAdapter

        getServices()

        servicesRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        getExperts()
    }

    private fun getServices(){
        if (NetworkHelper.isOnline(this)) {
            val servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
            servicesViewModel.getServices().observe(this, Observer {
                it?.let {
//                    hideProgressDialog()
                    showServices(it)
                }
            })
        } else {
//            hideProgressDialog()
            snackbar(parentLayout, getString(R.string.network_unavailable))
        }
    }

    private fun getExperts(){
        if(NetworkHelper.isOnline(this)) {
            val expertsViewModel = ViewModelProviders.of(this).get(ExpertsViewModel::class.java)
            expertsViewModel.getExperts().observe(this, Observer {
                it?.let {
                    showExpertMarkers(it)
                }
            })
        }
    }

    private fun showExpertMarkers(experts: ArrayList<Expert>){
        val iconFactory = IconGenerator(this)
        iconFactory.setColor(ContextCompat.getColor(this, R.color.app_theme))

        experts.forEach {
            map.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(it.service!!.first().name)))
                    .position(LatLng(it.address!!.latitude!!.toDouble(), it.address!!.longitude!!.toDouble()))
                    .anchor(iconFactory.anchorU, iconFactory.anchorV))
        }
    }

    private fun showServices(services: RealmList<Service>){
        if (services.isNotEmpty()) {
            services.forEach {
                when (it.name) {
                    "Electrical" -> it.icon = icons[0]
                    "Lift Maintenance" -> it.icon = icons[1]
                    "Plumbing" -> it.icon = icons[2]
                    "Fumigation" -> it.icon = icons[3]
                    "AC Maintenance" -> it.icon = icons[4]
                    "Property Inspection" -> it.icon = icons[5]
                    "Handyman Services" -> it.icon = icons[6]
                    "Ground Maintenance" -> it.icon = icons[7]
                }
            }

            saveServicesToRealm(services)

            servicesAdapter?.setData(services)
            servicesAdapter?.notifyDataSetChanged()
        }
    }

    private fun saveServicesToRealm(services: RealmList<Service>) {
        realm.executeTransaction {
            it.copyToRealmOrUpdate(services)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        hideProgressDialog()

        if (!isFinishing)
            alert(errorEvent.message) {
                yesButton {
                    it.dismiss()
                }
            }.show()
    }

    private fun hideKeyboard() {
        val view = currentFocus

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
        realm.close()
    }
}
