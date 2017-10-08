package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import app.property.management.R
import app.property.management.adapter.ServiceChooserAdapter
import app.property.management.model.OfferedService
import app.property.management.util.RealmUtil
import io.realm.Realm
import kotlinx.android.synthetic.main.service_selection_layout.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 07/10/2017.
 */
class ServiceChooser : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_selection_layout)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        background.setActualImageResource(R.drawable.apart_three)

        categories.layoutManager = LinearLayoutManager(this)

        val services = realm.where(OfferedService::class.java).findAll()

        if(services.isNotEmpty()) {
            val adapter = ServiceChooserAdapter(this, services)
            categories.adapter = adapter
        }
    }
}