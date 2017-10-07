package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import app.property.management.R
import app.property.management.adapter.ServiceChooserAdapter
import app.property.management.model.OfferedService
import io.realm.RealmList
import kotlinx.android.synthetic.main.service_selection_layout.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 07/10/2017.
 */
class ServiceChooser : AppCompatActivity() {

    private fun getServices(): RealmList<OfferedService> {
        val services: RealmList<OfferedService> = RealmList()
        services.add(OfferedService("Electrical Services", null))
        services.add(OfferedService("Lift Services", null))
        services.add(OfferedService("Plumbing Services", null))
        services.add(OfferedService("Fumigation Services", null))
        services.add(OfferedService("AC Maintenance Services", null))
        services.add(OfferedService("Property Inspection Services", null))
        services.add(OfferedService("Handyman Services", null))
        services.add(OfferedService("Ground Maintenance Services", null))

        return services
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_selection_layout)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        background.setActualImageResource(R.drawable.apart_three)

        categories.layoutManager = LinearLayoutManager(this)

        val adapter = ServiceChooserAdapter(this, getServices())
        categories.adapter = adapter
    }
}