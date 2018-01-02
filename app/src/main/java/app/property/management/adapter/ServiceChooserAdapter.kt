package app.property.management.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import app.property.management.R
import app.property.management.activity.MapActivity
import app.property.management.model.OfferedService
import app.property.management.view.SquareImageView
import com.bumptech.glide.Glide
import io.realm.RealmResults

/**
 * Created by kombo on 07/10/2017.
 */
class ServiceChooserAdapter(private val context: Context, private val services: RealmResults<OfferedService>, private val propertyName: String?)
    : RecyclerView.Adapter<ServiceChooserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.service_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(context, services[holder.adapterPosition]!!, propertyName)
    }

    override fun getItemCount(): Int = services.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, offeredService: OfferedService, propertyName: String?) {
            val background = itemView.findViewById(R.id.background) as LinearLayout
            val service = itemView.findViewById(R.id.service) as TextView
            val icon = itemView.findViewById(R.id.icon) as SquareImageView

            service.text = offeredService.title
//            icon.setImageResource(offeredService.icon!!)
            Glide.with(context).load(offeredService.icon).into(icon)

            background.setOnClickListener {
                context.startActivity(Intent(context, MapActivity::class.java).putExtra(MapActivity.SELECTED_SERVICE, offeredService.title))
            }
        }
    }
}