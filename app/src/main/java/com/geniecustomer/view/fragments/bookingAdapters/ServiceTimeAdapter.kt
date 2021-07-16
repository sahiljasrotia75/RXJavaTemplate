package com.geniecustomer.view.fragments.bookingAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.model.ScheduleItem
import kotlinx.android.synthetic.main.service_time_adapter.view.*

class ServiceTimeAdapter(var context: Context) :
    RecyclerView.Adapter<ServiceTimeAdapter.ViewHolder>() {

    val listSize = ArrayList<ScheduleItem?>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layout =
            LayoutInflater.from(context).inflate(R.layout.service_time_adapter, parent, false)
        return ViewHolder(layout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            if (listSize[position] != null) {
                if (listSize[position]?.startTime != null && listSize[position]?.endTime != null) {
                    holder.servicesTime.text =
                        "${listSize[position]?.startTime} - ${listSize[position]?.endTime}"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount() = listSize.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val servicesTime = itemView.servicesTime!!
    }


    fun submitList(scheduleTime: ArrayList<ScheduleItem?>) {
        try {
            listSize.clear()
            listSize.addAll(scheduleTime)
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}