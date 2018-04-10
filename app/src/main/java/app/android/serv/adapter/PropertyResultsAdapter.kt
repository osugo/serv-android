package app.android.serv.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import app.android.serv.Constants
import app.android.serv.R
import app.android.serv.R.id.service
import app.android.serv.R.layout.properties
import app.android.serv.activity.Details
import app.android.serv.model.Property
import com.bumptech.glide.Glide
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

/**
 * Created by kombo on 22/11/2017.
 */
class PropertyResultsAdapter(private val context: Context, private val serviceId: String, private val properties: ArrayList<Property>) :
        RecyclerView.Adapter<PropertyResultsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(properties[holder.adapterPosition], context, serviceId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.property_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int  = properties.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(property: Property, context: Context, serviceId: String) {
            val name = itemView.findViewById(R.id.name) as TextView
            val location = itemView.findViewById(R.id.location) as TextView
            val icon = itemView.findViewById(R.id.icon) as ImageView
            val layout = itemView.findViewById(R.id.layout) as RelativeLayout

            name.text = property.name
            location.visibility = View.GONE
//            location.text = property.l

            when (property.propertyTypeName) {
                context.getString(R.string.apartment) -> Glide.with(context).load(R.drawable.ic_apartments).into(icon)
                context.getString(R.string.commercial) -> Glide.with(context).load(R.drawable.ic_commercial).into(icon)
                context.getString(R.string.estate) -> Glide.with(context).load(R.drawable.ic_estate).into(icon)
            }

            layout.setOnClickListener {
                context.startActivity(Intent(context, Details::class.java).putExtra(Constants.PROPERTY_ID, property.id).putExtra(Constants.SERVICE_ID, serviceId))
            }
        }
    }
}