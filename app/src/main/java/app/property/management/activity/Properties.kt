package app.property.management.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import app.property.management.R
import app.property.management.adapter.PropertyResultsAdapter
import app.property.management.model.Property
import app.property.management.util.RealmUtil
import app.property.management.view.DividerItemDecoration
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.properties.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 22/11/2017.
 */
class Properties: AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.properties)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        propertiesRecycler.layoutManager = LinearLayoutManager(this)
        propertiesRecycler.addItemDecoration(DividerItemDecoration(this))

        val results: RealmResults<Property> = realm.where(Property::class.java).findAll()
        val adapter = PropertyResultsAdapter(this, results, true, true)
        propertiesRecycler.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_properties, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId){
        R.id.add -> {
            startActivity(Intent(this, PropertySelection::class.java))
            true
        }
        else -> false
    }
}