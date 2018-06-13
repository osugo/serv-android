package app.android.serv.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import app.android.serv.Constants
import app.android.serv.R
import app.android.serv.activity.Details
import app.android.serv.model.Property
import app.android.serv.util.RealmUtil
import com.bumptech.glide.Glide
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.selector

/**
 * Created by kombo on 22/11/2017.
 */
class PropertyResultsAdapter(private val context: Context, private val serviceId: String, private val properties: OrderedRealmCollection<Property>,
                             autoUpdate: Boolean, updateOnModification: Boolean) :
        RealmRecyclerViewAdapter<Property, PropertyResultsAdapter.ViewHolder>(properties, autoUpdate, updateOnModification) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(properties[holder.adapterPosition]!!, context, serviceId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.property_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = properties.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(property: Property, context: Context, serviceId: String) {
            val name = itemView.find(R.id.name) as TextView
            val location = itemView.find(R.id.location) as TextView
            val icon = itemView.find(R.id.icon) as ImageView
            val layout = itemView.find(R.id.layout) as RelativeLayout
            val remove = itemView.find(R.id.remove) as ImageButton

            name.text = property.name
            location.visibility = View.GONE

            when (property.propertyTypeName) {
                context.getString(R.string.apartment) -> Glide.with(context).load(R.drawable.ic_apartments).into(icon)
                context.getString(R.string.commercial) -> Glide.with(context).load(R.drawable.ic_commercial).into(icon)
                context.getString(R.string.estate) -> Glide.with(context).load(R.drawable.ic_estate).into(icon)
            }

            layout.setOnClickListener {
                context.startActivity(context.intentFor<Details>(Constants.PROPERTY_ID to property.id, Constants.SERVICE_ID to serviceId))
            }

            remove.setOnClickListener {
                val options = listOf("Remove property")
                context.selector(null, options, {
                    _, _ -> removeProperty(property.id!!)
                })
            }
        }
    }

    private fun removeProperty(id: String){
        Realm.getInstance(RealmUtil.realmConfig).use {
            val item = it.where(Property::class.java).equalTo("id", id).findFirst()

            if(item != null)
                it.executeTransaction {
                    item.deleteFromRealm()
                }
        }
    }
}