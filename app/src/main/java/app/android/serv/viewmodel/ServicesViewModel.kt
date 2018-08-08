package app.android.serv.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import app.android.serv.model.Service
import app.android.serv.rest.ErrorHandler
import app.android.serv.rest.RestClient
import app.android.serv.rest.RestInterface
import app.android.serv.util.RealmUtil
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList

/**
 * Created by kombo on 13/06/2018.
 */
class ServicesViewModel : ViewModel() {

    private var services: MutableLiveData<RealmList<Service>>? = null

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    fun getServices(): MutableLiveData<RealmList<Service>> {
        if (services == null) {
            services = MutableLiveData()

            fetchServicesFromRealm()
        }

        return services!!
    }

    private fun fetchServicesFromRealm() {
        val serviceList = RealmList<Service>()

        Realm.getInstance(RealmUtil.realmConfig).use {
            val results = it.where(Service::class.java).findAll()

            if (results.isNotEmpty()) {
                val items = it.copyFromRealm(results)
                serviceList.addAll(items)
            }
        }

        if (serviceList.isNotEmpty())
            services?.value = serviceList

        loadFromNetwork()
    }

    private fun loadFromNetwork(){
        restInterface.getServices()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    services?.postValue(it)
                }) {
                    services?.postValue(null)
                    ErrorHandler.showError(it)
                }
    }
}