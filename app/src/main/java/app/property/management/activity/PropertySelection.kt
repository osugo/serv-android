package app.property.management.activity

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.Toast
import app.property.management.R
import app.property.management.adapter.PlaceAutocompleteAdapter
import app.property.management.model.Property
import app.property.management.model.User
import app.property.management.util.RealmUtil
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.places.PlaceBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.realm.Realm
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.property_selection.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by kombo on 08/08/2017.
 */

/**
 * Let's users input their property details.
 */
class PropertySelection : AppCompatActivity(), View.OnClickListener, RadioGroup.OnCheckedChangeListener, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener {

    private var typeOfProperty: String? = null
    private lateinit var nameOfProperty: String
    private lateinit var locationOfProperty: String

    private lateinit var realm: Realm
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var adapter: PlaceAutocompleteAdapter

    companion object {
        val TAG: String = PropertySelection::class.java.simpleName
        val BOUNDS_GREATER_SYDNEY = LatLngBounds(LatLng(-34.041458, 150.790100), LatLng(-33.682247, 151.383362));
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build()

        setContentView(R.layout.property_selection)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        proceed.setOnClickListener(this)
        propertyType.setOnCheckedChangeListener(this)

        location.onItemClickListener = this

        adapter = PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS_GREATER_SYDNEY, null)
        location.setAdapter(adapter)

        Glide.with(this).load(R.drawable.apart_two).into(background)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = adapter.getItem(position)
        val placeId = item.placeId
        val primaryText = item.getPrimaryText(null)

        Log.i(TAG, "Autocomplete item selected: " + primaryText)

        /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
        val placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
        placeResult.setResultCallback(updatePlaceDetailsCallback)

        Toast.makeText(applicationContext, "Clicked: " + primaryText, Toast.LENGTH_SHORT).show()
        Log.i(TAG, "Called getPlaceById to get Place details for " + placeId!!)
    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private val updatePlaceDetailsCallback = ResultCallback<PlaceBuffer> { places ->
        if (!places.status.isSuccess) {
            Log.e(TAG, "Place query did not complete. Error: " + places.status.toString())
            places.release()
            return@ResultCallback
        }
        
        val place = places.get(0)

        location.setText(place.address)

        Log.e(TAG, place.address.toString())

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

        Log.i(TAG, "Place details received: " + place.name)

        places.release()
    }

//    private fun formatPlaceDetails(res: Resources, name: CharSequence, id: String,
//                                   address: CharSequence, phoneNumber: CharSequence, websiteUri: Uri): Spanned {
//        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri))
//        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri))
//
//    }

    override fun onCheckedChanged(radioGroup: RadioGroup, checkedId: Int) {
        typeOfProperty = getPropertyTypeFromId(checkedId)
    }

    override fun onClick(view: View?) {
        nameOfProperty = name.text.toString()
        locationOfProperty = location.text.toString()

        if (typeOfProperty.isNullOrEmpty())
            typeOfProperty = getPropertyTypeFromId(propertyType.checkedRadioButtonId)

        if (nameOfProperty.isBlank()) {
            toast("Please enter the name of the property")
            return
        }

        if (locationOfProperty.isBlank()) {
            toast("Please enter the location")
            return
        }

        if (nameOfProperty.isNotBlank() && locationOfProperty.isNotBlank() && !typeOfProperty.isNullOrBlank()) {
            try {
                realm.executeTransaction {
                    val property = Property(nameOfProperty, locationOfProperty, typeOfProperty)
                    realm.copyToRealmOrUpdate(property)
                }
            } catch (e: RealmException) {
                Log.e(TAG, e.message, e)
            } finally {
                startActivity(Intent(this, Properties::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                finish()
            }
        }
    }

    private fun getPropertyTypeFromId(id: Int): String {
        var type = ""

        when (id) {
            R.id.commercial -> {
                type = getString(R.string.commercial)
            }
            R.id.apartment -> {
                type = getString(R.string.apartment)
            }
            R.id.estate -> {
                type = getString(R.string.estate)
            }
        }

        return type
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.errorCode)

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this, "Could not connect to Google API Client: Error " + connectionResult.errorCode, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed();
            true
        }
        else -> false
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }

}
