package app.android.serv.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import app.android.serv.R
import app.android.serv.activity.MapActivity
import app.android.serv.model.Service
import app.android.serv.view.SquareImageView
import org.jetbrains.anko.find

/**
 * Created by kombo on 07/10/2017.
 */
class ServicesAdapter(private val context: Context, private val services: ArrayList<Service>)
    : RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.service_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(context, services[holder.adapterPosition])
    }

    override fun getItemCount(): Int = services.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, service: Service) {
            val background = itemView.find(R.id.background) as LinearLayout
            val name = itemView.find(R.id.service) as TextView
            val icon = itemView.find(R.id.icon) as SquareImageView

            name.text = service.name

            service.icon?.let {
                icon.setImageResource(it)
            }

            background.setOnClickListener {
//                                context.startActivity(Intent(context, MapActivity::class.java).putExtra(MapActivity.SELECTED_SERVICE, offeredService.title))
//                if (realm.where(Property::class.java).findAll().isNotEmpty())
//                    context.startActivity(Intent(context, Properties::class.java).putExtra(Constants.SERVICE, offeredService.title))
//                else
//                    context.startActivity(Intent(context, MapActivity::class.java).putExtra(Constants.SERVICE, offeredService.title))
            }
        }
    }
}