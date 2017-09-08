package app.property.management.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import android.widget.EditText
import app.property.management.R
import app.property.management.adapter.ServicesAdapter
import app.property.management.dialog.InfoDialog
import app.property.management.model.OfferedService
import app.property.management.util.RealmUtil
import com.google.android.flexbox.*
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.realm.Realm
import io.realm.RealmList
import java.util.*

/**
 * Created by kombo on 25/08/2017.
 */
class ServiceSelection : Fragment(), ServicesAdapter.ViewHolder.ClickListener {

    private lateinit var servicesAdapter: ServicesAdapter
    private lateinit var onSelectionComplete: OnServicesSelectionComplete

    interface OnServicesSelectionComplete {
        fun onComplete()
    }

    override fun onItemClicked(position: Int) {
        val offeredService = getServices()[position]
        val service = offeredService.service
        val image = offeredService.image

        val fragment = DescriptionDialog.newInstance(position, service!!, image)
        fragment.setTargetFragment(this, 0)
        fragment.show(activity.supportFragmentManager, "description")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.service_selection_layout, container, false)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView) as RecyclerView

        val flexBoxLayoutManager = FlexboxLayoutManager()
        flexBoxLayoutManager.flexDirection = FlexDirection.ROW
        flexBoxLayoutManager.flexWrap = FlexWrap.WRAP
        flexBoxLayoutManager.justifyContent = JustifyContent.CENTER
        flexBoxLayoutManager.alignItems = AlignItems.STRETCH

        recyclerView.layoutManager = flexBoxLayoutManager

        servicesAdapter = ServicesAdapter(activity, getServices(), this)
        recyclerView.adapter = servicesAdapter

        return view
    }

    private fun getServices(): RealmList<OfferedService> {
        val services: RealmList<OfferedService> = RealmList()
        services.add(OfferedService("Electrical Services", null))
        services.add(OfferedService("Lift Services", null))
        services.add(OfferedService("Plumbing Services", null))
        services.add(OfferedService("Fumigation Services", null))
        services.add(OfferedService("AC Maintenance Services", null))
        services.add(OfferedService("Property Inspection Services", null))
        services.add(OfferedService("Handyman Services", null))
        services.add(OfferedService("Ground Maintenance Services", null))

        return services
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> servicesAdapter.toggleSelection(data?.extras!!.getInt("pos"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_service_selection, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.done -> {
                if (servicesAdapter.selectedItemCount > 0)
                    InfoDialog().show(activity.supportFragmentManager, "InfoDialog")
//                    onSelectionComplete.onComplete()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            onSelectionComplete = context as OnServicesSelectionComplete
        } catch (e: ClassCastException) {
            throw ClassCastException(String.format("%s must implement interfaces", context.toString()))
        }
    }

    class DescriptionDialog : DialogFragment(), View.OnClickListener, View.OnTouchListener, DatePickerDialog.OnDateSetListener {

        override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            val text = "$dayOfMonth/${monthOfYear + 1}/$year"
            schedule?.setText(text)
        }

        override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
            return when (motionEvent?.action) {
                MotionEvent.ACTION_DOWN -> {
                    val now = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show(activity.fragmentManager, "DatePickerDialog")
                    true
                }
                else -> false
            }
        }

        override fun onClick(view: View?) {
            when (view?.id) {
                R.id.done -> {
                    val description = problemDescription?.text.toString()

                    targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, Intent().putExtra("pos", position))
                    dismissAllowingStateLoss()
                    //  var activity: Activity
//                if(description.isBlank()) {
//                    toast("Please enter a description of the problem to proceed")
//                }
//
//                if(description.isNotBlank()){
//                    try {
//                        realm.executeTransaction {
//                            val request = Request(offeredService, description)
//                            realm.copyToRealmOrUpdate(request)
//                        }
//                    } catch (e: RealmException){
//                        Log.e("DescriptionFragment", e.message, e)
//                    } finally {
//
//
//                        dismissAllowingStateLoss()
//                    }
//                }
                }
            }
        }

        lateinit var realm: Realm
        private var position: Int? = 0
        private var problemDescription: EditText? = null
        private var schedule: EditText? = null
        private var offeredService: OfferedService? = null

        companion object {
            private val SERVICE = "service"
            private val IMAGE = "image"
            private val POS = "position"

            fun newInstance(position: Int, service: String, image: String?): DescriptionDialog {
                val bundle = Bundle()
                bundle.putString(SERVICE, service)
                bundle.putString(IMAGE, image)
                bundle.putInt(POS, position)

                val descriptionDialog = DescriptionDialog()
                descriptionDialog.arguments = bundle

                return descriptionDialog
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            realm = Realm.getInstance(RealmUtil.getRealmConfig())
        }

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater?.inflate(R.layout.description_layout, container, false)

            problemDescription = view?.findViewById(R.id.problemDescription)
            schedule = view?.findViewById(R.id.date)

            val done = view?.findViewById<Button>(R.id.done)

            offeredService = OfferedService(arguments.getString(SERVICE), arguments.getString(IMAGE))
            position = arguments.getInt(POS)

            done?.setOnClickListener(this)
            schedule?.setOnTouchListener(this)

            return view
        }

        override fun onStart() {
            super.onStart()

            dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        override fun onDestroyView() {
            super.onDestroyView()

            realm.close()
        }
    }

}