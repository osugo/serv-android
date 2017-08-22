package app.property.management.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import app.property.management.R
import app.property.management.dialog.InfoDialog
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class Home : AppCompatActivity() {

    var service: String? = null
    var provider: String? = null

    var serviceList: LinkedList<String>? = null
    var serviceProviders: LinkedList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        serviceList = LinkedList(Arrays.asList("Plumber", "Electrician", "Lift Operator", "AC Operator", "DStv Installation"))
        serviceProviders = LinkedList(Arrays.asList("Davis & Shirtliff", "KPLC", "Nairobi Water", "MultiChoice"))

        serviceSpinner.attachDataSource(serviceList)
        providerSpinner.attachDataSource(serviceProviders)

        serviceSpinner.setOnItemSelectedListener(serviceSelectListener)
        providerSpinner.setOnItemSelectedListener(providerSelectListener)

        proceed.setOnClickListener {
            if (service == null)
                showToast("Please choose a service")

            if (provider == null)
                showToast("Please select a preferred provider")

            if (description.text.toString().isEmpty())
                showToast("Please enter a description")

            if (service != null && provider != null && description.text.toString().isNotEmpty())
                showDialog()
        }
    }

    private val serviceSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
            service = serviceList!![position]
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private val providerSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
            provider = serviceProviders!![position]
        }
    }

    private fun showDialog() {
        InfoDialog().show(supportFragmentManager, "InfoDialog")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
}
