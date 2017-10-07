package app.property.management.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import app.property.management.R
import app.property.management.model.OfferedService
import io.realm.RealmList

/**
 * Created by kombo on 07/10/2017.
 */
class ServiceChooserAdapter(private val context: Context, private val services : RealmList<OfferedService>) : RecyclerView.Adapter<ServiceChooserAdapter.ViewHolder>() {

    val colors = arrayOf("#90FF4081", "#9000c61e", "#90bdd000", "#90ed215d", "#90ee5837", "#9058d093", "#90ff9041", "#901fa4c7")

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.service_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(context, services[position], colors[position])
    }

    override fun getItemCount(): Int = services.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, offeredService: OfferedService, color: String){
            val background = itemView.findViewById<LinearLayout>(R.id.service_background)
            val service = itemView.findViewById<TextView>(R.id.service)

            service.text = offeredService.service
            background.setBackgroundColor(Color.parseColor(color))
        }
    }
}