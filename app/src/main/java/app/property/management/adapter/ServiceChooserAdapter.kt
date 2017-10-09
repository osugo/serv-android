package app.property.management.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import app.property.management.R
import app.property.management.activity.Details
import app.property.management.model.OfferedService
import io.realm.RealmResults

/**
 * Created by kombo on 07/10/2017.
 */
class ServiceChooserAdapter(private val context: Context, private val services: RealmResults<OfferedService>) : RecyclerView.Adapter<ServiceChooserAdapter.ViewHolder>() {

    val colors = arrayOf("#99FF4081", "#9900c61e", "#99bdd000", "#99ed215d", "#99ee5837", "#9958d093", "#99ff9041", "#991fa4c7")

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.service_layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(context, services[holder.adapterPosition], colors[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = services.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, offeredService: OfferedService, color: String, position: Int) {
            val background = itemView.findViewById<LinearLayout>(R.id.service_background)
            val service = itemView.findViewById<TextView>(R.id.service)

            service.text = offeredService.title
            background.setBackgroundColor(Color.parseColor(color))

            background.setOnClickListener {
                context.startActivity(Intent(context, Details::class.java).putExtra(Details.SELECTED_SERVICE, position))
            }
        }
    }
}