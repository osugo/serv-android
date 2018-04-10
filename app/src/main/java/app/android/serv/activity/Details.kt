package app.android.serv.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import app.android.serv.Constants
import app.android.serv.R
import app.android.serv.model.Request
import app.android.serv.rest.ErrorHandler
import app.android.serv.rest.RestClient
import app.android.serv.rest.RestInterface
import app.android.serv.util.Commons
import app.android.serv.util.NetworkHelper
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.details.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.yesButton
import java.util.*


/**
 * Created by kombo on 07/10/2017.
 */
class Details : BaseActivity(), View.OnClickListener {

    private var time: String? = null
    private var date: String? = null
    private var propertyId: String? = null
    private var desc: String? = null
    private var serviceId: String? = null

    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        val TAG = Details::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Schedule"

        propertyId = intent.getStringExtra(Constants.PROPERTY_ID)
        serviceId = intent.getStringExtra(Constants.SERVICE_ID)

        /* end after 1 month from now */
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.MONTH, 1)

        /* start before 1 month from now */
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.MONTH, -1)

        val horizontalCalendar = HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build()

        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {

            override fun onDateSelected(calendar: Calendar?, position: Int) {
                date = android.text.format.DateFormat.format("dd/MM/yyyy", calendar!!).toString()
                Log.e(TAG, "Date is: $date")
            }
        }

        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            time = "$hourOfDay : $minute"
            Log.e(TAG, "Time is $time")
        }

        submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        desc = description.text.toString()
        if (isEmpty(desc)) {
            snackbar(parentLayout, "Please enter a description")
            return
        }

        if (!isEmpty(desc) && !isFinishing) {
            if (NetworkHelper.isOnline(this)) {
                disposable.add(
                        restInterface.makeRequest(
                                Request(null, null, desc, null, propertyId, null, null,
                                        Commons.user!!.id, serviceId, null, null, null, null))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    hideProgressDialog()

                                    showMessage()
                                }) {
                                    hideProgressDialog()
                                    ErrorHandler.showError(it)
                                }
                )
            } else {
                snackbar(parentLayout, getString(R.string.network_unavailable))
            }
//           Completable.fromAction({
//               val request = Request(System.currentTimeMillis(), service, property, desc, date, time)
//
//               try {
//                   realm.executeTransaction {
//                       realm.copyToRealmOrUpdate(request)
//                   }
//               } catch(ex: RealmException){
//                   Log.e(TAG, ex.localizedMessage, ex)
//               }
//
//               toast("Request has been logged")
//           }).subscribe({
//               startActivity(Intent(this, Summary::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
//               finish()
//           })
        }
    }

    private fun showMessage() {
        if (!isFinishing)
            alert("Your request has been made. We will notify you shortly") {
                yesButton {
                    it.dismiss()
                }
            }.show()
    }

    private fun isEmpty(text: String?): Boolean {
        return text!!.isBlank() or text.isEmpty()
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.clear()
    }
}