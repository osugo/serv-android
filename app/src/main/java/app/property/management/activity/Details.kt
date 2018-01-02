package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import app.property.management.R
import app.property.management.util.RealmUtil
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import io.realm.Realm
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*


/**
 * Created by kombo on 07/10/2017.
 */
class Details : AppCompatActivity() {

    private lateinit var realm: Realm

    companion object {
        val TAG = Details::class.java.simpleName!!
        val SELECTED_SERVICE = "title"
        val PROPERTY = "name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)

        realm = Realm.getInstance(RealmUtil.getRealmConfig())

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Schedule"

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

            override fun onDateSelected(date: Calendar?, position: Int) {

            }

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

        realm.close()
    }
}