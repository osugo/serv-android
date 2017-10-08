package app.property.management.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.property.management.R
import app.property.management.model.OfferedService
import io.realm.RealmList

/**
 * Created by kombo on 24/08/2017.
 */
class ServicesAdapter(private val context: Context, private val services : RealmList<OfferedService>,
                      private val clickListener: ViewHolder.ClickListener) : SelectableAdapter<ServicesAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(services[position], context, clickListener, isSelected(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.service_layout_item, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var clickListener: ClickListener? = null

        override fun onClick(view: View?) {
            clickListener?.onItemClicked(adapterPosition)
        }

        fun bindItems(offeredService: OfferedService, context: Context, clickListener: ClickListener, isSelected: Boolean){
            this.clickListener = clickListener

            val name = itemView.findViewById<TextView>(R.id.name)
            name.text = offeredService.title
            name.setOnClickListener(this)
            name.setBackgroundResource(if(isSelected) R.drawable.service_item_background_selected else R.drawable.service_item_background_normal)
            name.setTextColor(if(isSelected) Color.WHITE else context.resources.getColor(R.color.textColorPrimary))
        }

        interface ClickListener {
            fun onItemClicked(position: Int)
        }
    }
}