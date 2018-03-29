package app.android.serv.activity

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import app.android.serv.R
import app.android.serv.adapter.ServicesAdapter
import app.android.serv.event.ErrorEvent
import app.android.serv.model.Service
import app.android.serv.rest.ErrorHandler
import app.android.serv.rest.RestClient
import app.android.serv.rest.RestInterface
import app.android.serv.util.NetworkHelper
import app.android.serv.view.GridItemDecoration
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!isFinishing) {
            hideProgressDialog()

            alert(errorEvent.message) {
                yesButton {
                    it.dismiss()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_selection_layout)

        setSupportActionBar(toolbar)

        getServices()

        categories.layoutManager = GridLayoutManager(this, 2)
        categories.addItemDecoration(GridItemDecoration(2, 5, false))
    }

    private fun getServices() {
        if (NetworkHelper.isOnline(this)) {
            showProgressDialog()

            disposable.add(
                    restInterface.getServices()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                hideProgressDialog()
                                showServices(it)
                            }) {
                                ErrorHandler.showError(it)
                            }
            )
        }
    }

    private fun showServices(services: ArrayList<Service>) {

        if (services.isNotEmpty()) {
            services.forEach {
                when (it.name) {
                    "Electrical" -> it.icon = icons[0]
                    "List Maintenance" -> it.icon = icons[1]
                    "Plumbing" -> it.icon = icons[2]
                    "Fumigation" -> it.icon = icons[3]
                    "AC Maintenance" -> it.icon = icons[4]
                    "Property Inspection" -> it.icon = icons[5]
                    "Handyman Services" -> it.icon = icons[6]
                    "Ground Maintenance" -> it.icon = icons[7]
                }
            }

            val adapter = ServicesAdapter(this, services)
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

    override fun onDestroy() {
        super.onDestroy()
        hideProgressDialog()
        disposable.clear()
    }
}