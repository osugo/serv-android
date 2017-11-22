package app.property.management.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import app.property.management.R
import app.property.management.model.OfferedService
import app.property.management.model.Property
import app.property.management.model.Request
import app.property.management.util.RealmUtil
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.reactivex.Completable
import io.realm.Realm
import io.realm.exceptions.RealmException
import kotlinx.android.synthetic.main.details.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.toast
import java.util.*


/**
 * Created by kombo on 07/10/2017.
 */
class Details : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    lateinit var realm: Realm
    lateinit var service: OfferedService
    lateinit var property: Property
    lateinit var date: String
    lateinit var time: String

    companion object {
        val TAG = Details::class.java.simpleName
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

        val request: String? = intent.getStringExtra(SELECTED_SERVICE)
        val name: String? = intent.getStringExtra(PROPERTY)

        service = realm.where(OfferedService::class.java).equalTo("title", request).findFirst()!!
        property = realm.where(Property::class.java).equalTo("name", name).findFirst()!!

        toolbar.title = service.title

        schedule.setOnClickListener({
            if (description.text.isNotEmpty())
                showDatePickerDialog()
            else
                toast("Please enter a description to proceed")
        })
    }

    private fun showDatePickerDialog() {
        val now = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog.newInstance(
                this@Details,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.isThemeDark = true //set dark them for dialog?
        datePickerDialog.vibrate(true) //vibrate on choosing date?
        datePickerDialog.dismissOnPause(true) //dismiss dialog when onPause() called?
        datePickerDialog.showYearPickerFirst(false) //choose year first?
        datePickerDialog.accentColor = ContextCompat.getColor(this, R.color.app_theme) // custom accent color
        datePickerDialog.setTitle("Please select a date") //dialog title
        datePickerDialog.show(fragmentManager, "DatePickerDialog")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        date = "$dayOfMonth/$monthOfYear/$year"

        showTimePickerDialog()
    }

    private fun showTimePickerDialog() {
        val now = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog.newInstance(this@Details,
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
        timePickerDialog.isThemeDark = true //Dark Theme?
        timePickerDialog.vibrate(false) //vibrate on choosing time?
        timePickerDialog.dismissOnPause(false) //dismiss the dialog onPause() called?
        timePickerDialog.enableSeconds(true) //show seconds?

        //Handling cancel event
        timePickerDialog.setOnCancelListener({ Toast.makeText(this@Details, "Cancel choosing time", Toast.LENGTH_SHORT).show() })
        timePickerDialog.show(fragmentManager, "TimePickerDialog") //show time picker dialog
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val hourString = if (hourOfDay < 10) "0" + hourOfDay else "" + hourOfDay
        val minuteString = if (minute < 10) "0" + minute else "" + minute
        val secondString = if (second < 10) "0" + second else "" + second

        time = "$hourString:$minuteString:$secondString"

        Completable.fromAction({
            try {
                realm.executeTransaction({
                    val request = Request()
                    request.service = service
                    request.property = property
                    request.description = description.text.toString()
                    request.date = date
                    request.time = time

                    realm.copyToRealmOrUpdate(request)
                })
            } catch (ex: RealmException) {
                Log.e(TAG, ex.localizedMessage, ex)
            }
        }).subscribe {
            ConfirmationDialog().show(supportFragmentManager, "ConfirmationDialog")
        }
    }

    class ConfirmationDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setMessage("Request has been logged successfully. Would you like complete action or choose another service?")
                    .setPositiveButton("Complete Action", { _, _ ->
                        startActivity(Intent(activity, Summary::class.java))

                        dismiss()
                    }).setNegativeButton("Choose Other", { _, _ ->
                activity.onBackPressed()

                dismiss()
            }).create()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        realm.close()
    }
}