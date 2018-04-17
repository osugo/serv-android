package app.android.serv.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import app.android.serv.Constants
import app.android.serv.R
import app.android.serv.event.ErrorEvent
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
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.alert
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.yesButton
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by kombo on 07/10/2017.
 */
class Details : BaseActivity(), View.OnClickListener {

    private var time: String? = null
    private var date: String? = null
    private var propertyId: String? = null
    private var desc: String? = null
    private var title: String? = null
    private var serviceId: String? = null

    private val disposable = CompositeDisposable()

    private val restInterface by lazy {
        RestClient.client.create(RestInterface::class.java)
    }

    companion object {
        val TAG = Details::class.java.simpleName
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorEvent(errorEvent: ErrorEvent) {
        if (!isFinishing) {
            alert(errorEvent.message) {
                yesButton {
                    it.dismiss()
                }
            }.show()
        }
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
            }
        }

        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            time = "$hourOfDay : $minute"
        }

        submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        desc = description.text.toString()
        title = name.text.toString()

        if (isEmpty(title)) {
            snackbar(parentLayout, "Please enter a title")
            return
        }

        if (isEmpty(desc)) {
            snackbar(parentLayout, "Please enter a description")
            return
        }

        if (date == null) {
            date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }

        if (time == null) {
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
        }

        if (!isEmpty(desc) && !isEmpty(title) && propertyId != null && date != null && time != null && !isFinishing) {
            if (NetworkHelper.isOnline(this)) {
                showProgressDialog()

                disposable.add(
                        restInterface.makeRequest(
                                Request(title, desc, propertyId, Commons.user!!.id, date, time, serviceId))
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
        }
    }

    private fun showMessage() {
        if (!isFinishing)
            alert("Your request has been made. We will notify you shortly", "Thank You") {
                yesButton {
                    startActivity(intentFor<ServiceChooser>().clearTop())
                    it.dismiss()
                }
            }.show()
    }

    private fun isEmpty(text: String?): Boolean {
        return text!!.isBlank() or text.isEmpty()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        disposable.clear()
    }
}