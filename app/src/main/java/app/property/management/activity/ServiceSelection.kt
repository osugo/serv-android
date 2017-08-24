package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import app.property.management.R
import app.property.management.adapter.ServicesAdapter
import app.property.management.adapter.ServicesAdapter.ViewHolder.ClickListener
import app.property.management.model.OfferedService
import io.realm.RealmList
import kotlinx.android.synthetic.main.service_selection_layout.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 23/08/2017.
 */
class ServiceSelection : AppCompatActivity(), ClickListener {

    override fun onItemClicked(position: Int) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_selection_layout)

        setSupportActionBar(toolbar)

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val servicesAdapter = ServicesAdapter(this, getServices(), this)
        recyclerView.adapter = servicesAdapter
    }

    private fun getServices() : RealmList<OfferedService> {
        val services : RealmList<OfferedService> = RealmList()
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
}