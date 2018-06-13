package app.android.serv.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import app.android.serv.Constants
import app.android.serv.R
import app.android.serv.adapter.PropertyResultsAdapter
import app.android.serv.event.ErrorEvent
import app.android.serv.model.Property
import app.android.serv.rest.RestClient
import app.android.serv.rest.RestInterface
import app.android.serv.util.RealmUtil
import app.android.serv.view.DividerItemDecoration
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.properties.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 22/11/2017.
 */
class Properties : BaseActivity() {

    private var serviceId: String? = null

    private val disposable = CompositeDisposable()

    private val realm by lazy {
        Realm.getInstance(RealmUtil.realmConfig)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!isFinishing)
            alert(errorEvent.message) {
                yesButton {
                    it.dismiss()
                }
            }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.properties)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        serviceId = intent.getStringExtra(Constants.SERVICE_ID)

        propertiesRecycler.layoutManager = LinearLayoutManager(this)
        propertiesRecycler.addItemDecoration(DividerItemDecoration(this))

        addProperty.setImageResource(R.drawable.ic_add)
        addProperty.setOnClickListener { startActivity(intentFor<MapActivity>(Constants.SERVICE_ID to serviceId)) }

        loadUserProperties()
    }

    private fun loadUserProperties() {
        val properties = realm.where(Property::class.java).findAll()

        if (properties.isNotEmpty())
            showProperties(properties)
    }

    private fun showProperties(properties: RealmResults<Property>) {
        val adapter = PropertyResultsAdapter(this, serviceId!!, properties, true, true)
        propertiesRecycler.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        disposable.clear()
    }
}