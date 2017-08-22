package app.property.management.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.property.management.R
import app.property.management.model.Property

/**
 * Created by kombo on 17/08/2017.
 */
class PropertyAdapter : RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {

    private var properties : ArrayList<Property>? = ArrayList()

    public fun setData(propertyList : ArrayList<Property>){
        if(properties != null){
            properties!!.clear()
            properties!!.addAll(propertyList)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(properties!![holder.adapterPosition])
    }

    override fun getItemCount(): Int = properties!!.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.property_layout, parent, false)

        return ViewHolder(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(property: Property){
            val name = itemView.findViewById<TextView>(R.id.name) as TextView
            val location = itemView.findViewById<TextView>(R.id.location) as TextView

            name.text = property.name
            location.text = property.location
        }
    }
}