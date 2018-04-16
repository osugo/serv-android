package app.android.serv.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import app.android.serv.R
import app.android.serv.adapter.RequestsAdapter
import app.android.serv.model.Request
import app.android.serv.util.RealmUtil
import app.android.serv.view.DividerItemDecoration
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

//        val requests = realm.where(Request::class.java).findAll()
//
//        if (requests.isNotEmpty()) {
//            recycler.layoutManager = LinearLayoutManager(this)
//            recycler.addItemDecoration(DividerItemDecoration(this))
//
//            val adapter = RequestsAdapter(this, requests)
//            recycler.adapter = adapter
//        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            startActivity(Intent(this, Properties::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            true
        }
        else -> false
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }
}