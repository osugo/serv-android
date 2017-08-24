package app.property.management.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import app.property.management.R
import app.property.management.model.OfferedService
import com.bumptech.glide.Glide
import io.realm.RealmList
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * Created by kombo on 24/08/2017.
 */
class ServicesAdapter(private val context: Context, private val services : RealmList<OfferedService>,
                      private val clickListener: ViewHolder.ClickListener) : SelectableAdapter<ServicesAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(services[position], context, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.service_layout_item, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var clickListener: ClickListener? = null

        override fun onClick(view: View?) {
            clickListener?.onItemClicked(adapterPosition)
        }

        fun bindItems(offeredService: OfferedService, context: Context, clickListener: ClickListener){
            val background = itemView.findViewById<ImageView>(R.id.background)
            val name = itemView.findViewById<TextView>(R.id.name)
            val layout = itemView.findViewById<FrameLayout>(R.id.layout)

            this.clickListener = clickListener

            Glide.with(context).load(R.drawable.apart_five).bitmapTransform(BlurTransformation(context)).into(background)
            name.text = offeredService.service

            layout.setOnClickListener(this)
        }

        public interface ClickListener {
            fun onItemClicked(position: Int)
        }
    }
}