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
import app.android.serv.activity.Details
import com.bumptech.glide.Glide
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

/**
 * Created by kombo on 22/11/2017.
 */
class PropertyResultsAdapter(private val context: Context, private val service: String, data: OrderedRealmCollection<Property>?, autoUpdate: Boolean, updateOnModification: Boolean) :
        RealmRecyclerViewAdapter<Property, PropertyResultsAdapter.ViewHolder>(data, autoUpdate, updateOnModification) {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(data!![holder.adapterPosition], context, service)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.property_layout, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(property: Property, context: Context, service: String) {
            val name = itemView.findViewById(R.id.name) as TextView
            val location = itemView.findViewById(R.id.location) as TextView
            val icon = itemView.findViewById(R.id.icon) as ImageView
            val layout = itemView.findViewById(R.id.layout) as RelativeLayout

            name.text = property.name
            location.text = property.location

            when (property.propertyType) {
                context.getString(R.string.apartment) -> Glide.with(context).load(R.drawable.ic_apartments).into(icon)
                context.getString(R.string.commercial) -> Glide.with(context).load(R.drawable.ic_commercial).into(icon)
                context.getString(R.string.estate) -> Glide.with(context).load(R.drawable.ic_estate).into(icon)
            }

            layout.setOnClickListener {
                context.startActivity(Intent(context, Details::class.java).putExtra(Constants.PROPERTY, property.name).putExtra(Constants.SERVICE, service))
            }
        }
    }
}