package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import app.property.management.R
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places

/**
 * Created by kombo on 08/10/2017.
 */
class PropertySelector : AppCompatActivity() {

    lateinit var geoDataClient: GeoDataClient
    lateinit var placesDetectionClient: PlaceDetectionClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.property_selector)

        geoDataClient = Places.getGeoDataClient(this, null)
        placesDetectionClient = Places.getPlaceDetectionClient(this, null)
    }
}

