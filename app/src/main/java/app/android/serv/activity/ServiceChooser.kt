package app.android.serv.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.MenuItem
import app.android.serv.R
import app.android.serv.adapter.ServicesAdapter
import app.android.serv.event.ErrorEvent
import app.android.serv.model.Service
import app.android.serv.util.NetworkHelper
import app.android.serv.util.RealmUtil
import app.android.serv.view.GridItemDecoration
import app.android.serv.viewmodel.ServicesViewModel
import io.realm.Realm
import io.realm.RealmList
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.service_selection_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

/**
 * Created by kombo on 07/10/2017.
 */
class ServiceChooser : BaseActivity() {

    private var servicesAdapter: ServicesAdapter? = null

    private val icons = intArrayOf(
            R.drawable.light_bulb,
            R.drawable.elevator,
            R.drawable.plumbing,
            R.drawable.fumigator,
            R.drawable.air_conditioner,
            R.drawable.house_inspection,
            R.drawable.handyman,
            R.drawable.landscaping
    )

    private val realm by lazy {
        Realm.getInstance(RealmUtil.realmConfig)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!isFinishing) {
            hideProgressDialog()

            alert(errorEvent.message) {
                yesButton {
                    it.dismiss()
                }
            }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_selection_layout)

        setSupportActionBar(toolbar)

        servicesAdapter = ServicesAdapter(this)
        categories.adapter = servicesAdapter

        getServices()

        categories.layoutManager = GridLayoutManager(this, 2)
        categories.addItemDecoration(GridItemDecoration(2, 5, false))
    }

    private fun getServices() {
        showProgressDialog()

        if (NetworkHelper.isOnline(this)) {
            val servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)
            servicesViewModel.getServices().observe(this, Observer {
                it?.let {
                    hideProgressDialog()
                    showServices(it)
                }
            })
        } else {
            hideProgressDialog()

            alert("Network connection unavailable") {
                yesButton {
                    it.dismiss()
                }
            }.show()
        }
    }

    private fun showServices(services: RealmList<Service>) {

        if (services.isNotEmpty()) {
            services.forEach {
                when (it.name) {
                    "Electrical" -> it.icon = icons[0]
                    "Lift Maintenance" -> it.icon = icons[1]
                    "Plumbing" -> it.icon = icons[2]
                    "Fumigation" -> it.icon = icons[3]
                    "AC Maintenance" -> it.icon = icons[4]
                    "Property Inspection" -> it.icon = icons[5]
                    "Handyman Services" -> it.icon = icons[6]
                    "Ground Maintenance" -> it.icon = icons[7]
                }
            }

            saveServicesToRealm(services)

            servicesAdapter?.setData(services)
            servicesAdapter?.notifyDataSetChanged()
        }
    }

    private fun saveServicesToRealm(services: RealmList<Service>) {
        try {
            Realm.getInstance(RealmUtil.realmConfig).use {
                it.executeTransaction {
                    it.copyToRealmOrUpdate(services)
                }
            }
        } catch (e: RealmException) {
            Log.e(TAG, e.localizedMessage, e)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {
            System.exit(0)
            true
        }
        else -> false
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressDialog()

        realm?.close()
    }

    companion object {
        private val TAG = ServiceChooser::class.java.simpleName
    }
}