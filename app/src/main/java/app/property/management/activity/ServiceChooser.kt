package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import app.property.management.R
import app.property.management.adapter.ServiceChooserAdapter
import app.property.management.model.OfferedService
import app.property.management.model.Property
import app.property.management.util.RealmUtil
import app.property.management.view.GridItemDecoration
import io.realm.Realm
import kotlinx.android.synthetic.main.service_selection_layout.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 07/10/2017.
 */
class ServiceChooser : AppCompatActivity() {

    lateinit var property: Property

    companion object {
        val PROPERTY_NAME = "name"
    }

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_selection_layout)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val name = intent.getStringExtra(PROPERTY_NAME)

        categories.layoutManager = GridLayoutManager(this, 2)
        categories.addItemDecoration(GridItemDecoration(2, 5, false))

        val services = realm.where(OfferedService::class.java).findAll()

        if (services.isNotEmpty()) {
            val adapter = ServiceChooserAdapter(this, services, name)
            categories.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}