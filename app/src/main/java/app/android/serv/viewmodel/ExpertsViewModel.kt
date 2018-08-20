package app.android.serv.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import app.android.serv.model.Expert
import app.android.serv.rest.ErrorHandler
import app.android.serv.rest.RestClient
import app.android.serv.rest.RestInterface
import io.reactivex.schedulers.Schedulers

/**
 * Created by kombo on 09/08/2018.
 */
class ExpertsViewModel : ViewModel() {

    private var experts: MutableLiveData<ArrayList<Expert>>? = null

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    fun getExperts(): MutableLiveData<ArrayList<Expert>> {
        if (experts == null) {
            experts = MutableLiveData()

            loadExperts()
        }

        return experts!!
    }

    private fun loadExperts(){
        restInterface.getExperts
                .subscribeOn(Schedulers.io())
                .subscribe({
                    experts?.postValue(it)
                }) {
                    experts?.postValue(ArrayList())
                    ErrorHandler.showError(it)
                }
    }
}