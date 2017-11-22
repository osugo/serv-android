package app.property.management.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import app.property.management.R
import app.property.management.activity.Details
import app.property.management.model.OfferedService
import app.property.management.view.SquareImageView
import com.bumptech.glide.Glide
import io.realm.RealmResults

/**
 * Created by kombo on 07/10/2017.
 */
class ServiceChooserAdapter(private val context: Context, private val services: RealmResults<OfferedService>) : RecyclerView.Adapter<ServiceChooserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.service_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(context, services[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = services.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, offeredService: OfferedService, position: Int) {
            val background = itemView.findViewById<RelativeLayout>(R.id.background)
            val service = itemView.findViewById<TextView>(R.id.service)
            val icon = itemView.findViewById<SquareImageView>(R.id.icon)

            service.text = offeredService.title
            Glide.with(context).load(offeredService.icon).centerCrop().into(icon)

            background.setOnClickListener {
                context.startActivity(Intent(context, Details::class.java).putExtra(Details.SELECTED_SERVICE, position))
            }
        }
    }
}