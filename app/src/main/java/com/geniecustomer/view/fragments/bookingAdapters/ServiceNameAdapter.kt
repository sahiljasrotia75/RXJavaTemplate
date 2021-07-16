package com.geniecustomer.view.fragments.bookingAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.model.booking.ServicesItem
import kotlinx.android.synthetic.main.service_name_adapter.view.*

class ServiceNameAdapter(var context: Context) :
    RecyclerView.Adapter<ServiceNameAdapter.ViewHolder>() {

    var servicesList = ArrayList<ServicesItem?>()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layout =
            LayoutInflater.from(context).inflate(R.layout.service_name_adapter, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            if (servicesList[position]?.service != null) {
                if (servicesList[position]?.service?.category != null) {
                    if (position < 1) {
                        holder.serviceName.text = "${servicesList[position]?.service?.name}"
                    } else {
                        holder.serviceName.text = ", ${servicesList[position]?.service?.name}"
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount() = servicesList.size


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceName = itemView.serviceName
    }


    fun submitList(services: ArrayList<ServicesItem?>) {
        try {
            servicesList.clear()
            servicesList.addAll(services)
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}