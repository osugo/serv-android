package app.property.management.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import app.property.management.Constants
import app.property.management.R
import app.property.management.model.OfferedService
import app.property.management.model.Property
import app.property.management.model.Request
import app.property.management.util.RealmUtil
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import io.reactivex.Completable
import io.realm.Realm
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.details.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.toast
import java.util.*


/**
 * Created by kombo on 07/10/2017.
 */
class Details : AppCompatActivity(), View.OnClickListener {

    private lateinit var realm: Realm
    private var time: String? = null
    private var date: String? = null
    private var property: Property? = null
    private var desc: String? = null
    private var service: OfferedService? = null

    companion object {
        val TAG = Details::class.java.simpleName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Schedule"

        property = realm.where(Property::class.java).equalTo("name", intent.getStringExtra(Constants.PROPERTY)).findFirst()
        service = realm.where(OfferedService::class.java).equalTo("title", intent.getStringExtra(Constants.SERVICE)).findFirst()

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
        if(isEmpty(desc)){
            snackbar(parentLayout, "Please enter a description")
            return
        }

        if(!isEmpty(desc)){
           Completable.fromAction({
               val request = Request(System.currentTimeMillis(), service, property, desc, date, time)

               try {
                   realm.executeTransaction {
                       realm.copyToRealmOrUpdate(request)
                   }
               } catch(ex: RealmException){
                   Log.e(TAG, ex.localizedMessage, ex)
               }

               toast("Request has been logged")
           }).subscribe({
               startActivity(Intent(this, Summary::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
               finish()
           })
        }
    }

    private fun isEmpty(text: String?) : Boolean {
        return text!!.isBlank() or text.isEmpty()
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

        realm.close()
    }
}