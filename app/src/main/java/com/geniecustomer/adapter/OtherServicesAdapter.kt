package com.geniecustomer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.model.service_providers.info.OtherServices
import kotlinx.android.synthetic.main.other_service_provider.view.*
import java.text.DecimalFormat

class OtherServicesAdapter(var context: Context) :
    RecyclerView.Adapter<OtherServicesAdapter.ViewHolder>() {

    private var otherServicesList: ArrayList<OtherServices> = ArrayList()
    var df = DecimalFormat("0.00")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OtherServicesAdapter.ViewHolder {
        val layout =
            LayoutInflater.from(context).inflate(R.layout.other_service_provider, parent, false)
        return ViewHolder(layout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OtherServicesAdapter.ViewHolder, position: Int) {
        try {
            val data_item = otherServicesList[position]
            holder.tv_service_type.text = data_item.name
            holder.tv_desc.text = data_item.desc

            if (data_item.priceType.equals("Fixed", true)) {
                holder.tv_charges.text = "$" + data_item.price
            } else {
                holder.tv_charges.text = "Starting from $" + data_item.price
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return otherServicesList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_service_type = itemView.tv_service_type1
        val tv_desc = itemView.tv_desc1
        val tv_charges = itemView.tv_charges1
    }


    fun submitList(otherServices: ArrayList<OtherServices>) {
        try {
            otherServicesList.clear()
            otherServicesList.addAll(otherServices)
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}