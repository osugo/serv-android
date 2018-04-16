package app.android.serv.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.android.serv.R
import app.android.serv.model.Request

/**
 * Created by kombo on 23/11/2017.
 */
class RequestsAdapter(val context: Context, private val requests: ArrayList<Request>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.requests_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(context, requests[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = requests.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, request: Request, pos: Int) {
            val position = itemView.findViewById(R.id.position) as TextView
            val propertyName = itemView.findViewById(R.id.propertyName) as TextView
            val issue = itemView.findViewById(R.id.issue) as TextView
            val description = itemView.findViewById(R.id.description) as TextView

            position.text = "${pos + 1}."
            propertyName.text = request.propertyName
            issue.text = request.serviceName
            description.text = request.description
        }
    }
}