package app.android.serv.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import app.android.serv.Constants
import app.android.serv.R
import app.android.serv.adapter.PropertyResultsAdapter
import app.android.serv.model.Property
import app.android.serv.util.RealmUtil
import app.android.serv.view.DividerItemDecoration
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.properties.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 22/11/2017.
 */
class Properties : AppCompatActivity() {

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

        val service = intent.getStringExtra(Constants.SERVICE)

        val results: RealmResults<Property> = realm.where(Property::class.java).findAll()
        val adapter = PropertyResultsAdapter(this, service, results, true, true)
        propertiesRecycler.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_properties, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.add -> {
            startActivity(Intent(this, MapActivity::class.java))
            true
        }
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }


}