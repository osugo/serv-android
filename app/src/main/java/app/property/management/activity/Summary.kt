package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import app.property.management.R
import app.property.management.adapter.RequestsAdapter
import app.property.management.model.Request
import app.property.management.util.RealmUtil
import app.property.management.view.DividerItemDecoration
import io.realm.Realm
import kotlinx.android.synthetic.main.requests.*
import kotlinx.android.synthetic.main.toolbar.*

/**
 * Created by kombo on 23/11/2017.
 */
class Summary : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.requests)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val requests = realm.where(Request::class.java).findAll()

        if (requests.isNotEmpty()) {
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.addItemDecoration(DividerItemDecoration(this))

            val adapter = RequestsAdapter(this, requests)
            recycler.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }
}