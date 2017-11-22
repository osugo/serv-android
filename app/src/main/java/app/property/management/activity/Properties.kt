package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import app.property.management.R
import app.property.management.adapter.PropertyResultsAdapter
import app.property.management.model.Property
import app.property.management.util.RealmUtil
import app.property.management.view.DividerItemDecoration
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.properties.*

/**
 * Created by kombo on 22/11/2017.
 */
class Properties: AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.properties)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        propertiesRecycler.layoutManager = LinearLayoutManager(this)
        propertiesRecycler.addItemDecoration(DividerItemDecoration(this))

        val results: RealmResults<Property> = realm.where(Property::class.java).findAll()
        val adapter = PropertyResultsAdapter(this, results, true, true)
        propertiesRecycler.adapter = adapter
    }
}