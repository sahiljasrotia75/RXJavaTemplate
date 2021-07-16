package com.geniecustomer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.ServiceRemoveListener
import com.geniecustomer.model.TaskObject
import kotlinx.android.synthetic.main.booking_detail_item.view.*
import java.text.DecimalFormat

class BookingDetailAdapter(
    val context: Context,
    val main_list: ArrayList<TaskObject>,
    val type: Int,
    val serviceremovedlistener: ServiceRemoveListener,
    val bookingType: String?
) : RecyclerView.Adapter<BookingDetailViewHolder>() {

    var df = DecimalFormat("0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingDetailViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.booking_detail_item, parent, false)
        return BookingDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return main_list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookingDetailViewHolder, position: Int) {
        holder.tv_service_type.text = main_list[position].name

        Log.e("sksankdlasd", "333 ----->  $bookingType")

        if (bookingType == "Flexible") {
            holder.tv_time_slot.visibility = View.VISIBLE
            holder.tv_time_slot.text = "Flexible"
        } else {
            if (main_list[position].timeSlot.isEmpty()) {
                holder.tv_time_slot.visibility = View.GONE
            } else {
                holder.tv_time_slot.visibility = View.VISIBLE
                if (!main_list[position].date.isNullOrEmpty()) {
                    holder.tv_time_slot.text =
                        "${main_list[position].timeSlot} | ${main_list[position].date}"
                } else {
                    holder.tv_time_slot.text = "${main_list[position].timeSlot} "
                }
            }
        }


        holder.tv_desc.text = main_list.get(position).desc
        Log.e("BADSHAH", "lets see the type: ${main_list[position].priceType}")
        if (main_list[position].priceType.equals("Fixed", true)) {
            holder.tv_charges.text = "$" + df.format(main_list[position].rate)
        } else {
//            holder.tv_charges.setText("$"+df.format(main_list[position].rate)+"/"+main_list.get(position).rateType)
            holder.tv_charges.text = "$" + df.format(main_list[position].rate)
        }

        if(type == 0){
            holder.tv_select.visibility = View.VISIBLE
            holder.tv_select.text = context.getString(R.string.remove)
            holder.tv_select.setBackgroundResource(R.drawable.select_round_corner_bg_red)
        }else{
            holder.tv_select.visibility = View.INVISIBLE
        }

        holder.tv_select.setOnClickListener {
            serviceremovedlistener.onServiceRemoved(position)
        }

    }
}

class BookingDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_service_type = itemView.tv_service_type
    val tv_desc = itemView.tv_desc
    val tv_charges = itemView.tv_charges
    val tv_select = itemView.tv_select
    val tv_time_slot = itemView.tv_time_slot

}