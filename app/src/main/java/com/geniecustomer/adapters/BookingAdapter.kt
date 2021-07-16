package com.geniecustomer.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.model.booking.Data
import com.geniecustomer.utils.GlideApp
import com.geniecustomer.view.activities.BookingDetailActivity
import com.geniecustomer.view.fragments.bookingAdapters.ServiceNameAdapter
import com.geniecustomer.view.fragments.bookingAdapters.ServiceTimeAdapter
import kotlinx.android.synthetic.main.booking_item.view.*

class BookingAdapter (val context : Context, val main_list: MutableList<Data>, var type : Int) : RecyclerView.Adapter<BookingViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewholder {
        val view = LayoutInflater.from(context).inflate(R.layout.booking_item,parent,false)
        Log.e("type ", " $type")
        return BookingViewholder(view)
    }

    override fun getItemCount(): Int {
        Log.e("type ", " $type")
        return main_list.size
    }


    fun updateType(type2: Int) {
        type = type2
    }

    override fun onBindViewHolder(holder: BookingViewholder, position: Int) {

        Log.e("type ", " $type")

        Log.e("slasmdlas", "mslmas   ${main_list[position].status}")

        if (type == 1) {
            holder.tv_status.text = main_list.get(position).status
            if (main_list[position].status.equals("Completed")) {
                holder.tv_status.background =
                    ContextCompat.getDrawable(context, R.drawable.text_back_corner_completed)
            } else {
                holder.tv_status.background =
                    (context.resources.getDrawable(R.drawable.text_back_corner_cancel))
            }

            holder.tv_service_type.text =
                main_list.get(position).services.get(0)?.service?.category?.name
            GlideApp.with(context)
                .load(Urls.BASE_URL + main_list.get(position).services.get(0)?.service?.category?.image)
                .into(holder.iv_maid)
            holder.tv_address.text = main_list.get(position).location?.address
            if (main_list[position].sentTo != null) {
                holder.tv_order_id.text =
                    "Service Provider: ${main_list.get(position).sentTo?.name}"
            }

            /*val dateTime = (context as BaseActivity).getDateTime((main_list.get(position).createdAt!!))

            holder.tv_datetime.setText(dateTime)*/
        } else {


            if (main_list[position].status.equals("Accepted")) {
                holder.tv_status.background =
                    ContextCompat.getDrawable(context, R.drawable.text_back_corner_completed)
            } else {
                holder.tv_status.background =
                    (context.resources.getDrawable(R.drawable.text_back_corner))
            }

            holder.tv_status.text = main_list.get(position).status
            holder.tv_service_type.text =
                main_list.get(position).services.get(0)?.service?.category?.name
            GlideApp.with(context)
                .load(Urls.BASE_URL + main_list.get(position).services.get(0)?.service?.category?.image)
                .into(holder.iv_maid)
            holder.tv_address.text = main_list.get(position).location?.address

            if (main_list[position].sentTo != null) {
                holder.tv_order_id.text =
                    "Service Provider: ${main_list.get(position).sentTo?.name}"
            }
/*
            val dateTime = (context as BaseActivity).getDateTime((main_list.get(position).createdAt!!))
            holder.tv_datetime.setText(dateTime)*/
        }


        /** @Mukesh
         * 28 Nov 2020
         * */
        if (main_list[position].scheduleTime.isEmpty()) {
            holder.servicesTime.visibility = View.GONE
            holder.servicesTimeRecyclerView.visibility = View.GONE
        } else {
            holder.servicesTime.visibility = View.VISIBLE
            holder.servicesTimeRecyclerView.visibility = View.VISIBLE
            val serviceTimeAdapter = ServiceTimeAdapter(context)
            holder.servicesTimeRecyclerView.adapter = serviceTimeAdapter
            serviceTimeAdapter.submitList(main_list[position].scheduleTime)
        }


        /** @Mukesh
         * 28 Nov 2020
         * */
        if (main_list[position].services.isEmpty()) {
            holder.servicesNames.visibility = View.GONE
            holder.servicesNames.visibility = View.GONE
        } else {
            holder.servicesNames.visibility = View.VISIBLE
            holder.servicesNameRecycler.visibility = View.VISIBLE
            val serviceNameAdapter = ServiceNameAdapter(context)
            holder.servicesNameRecycler.adapter = serviceNameAdapter
            serviceNameAdapter.submitList(main_list[position].services)
        }



        if (main_list.get(position).scheduleTime.size != 0) {
            val dateTime = (context as BaseActivity).getDateBookingWithDay(
                main_list.get(position).scheduleTime.get(0)?.date
            )
            holder.tv_datetime.text = dateTime
            /*if (main_list[position].scheduleTime.size > 0){
            }else{
                holder.tv_datetime.setText(dateTime+" ${main_list.get(position).scheduleTime?.get(0)?.startTime} - ${main_list.get(position).scheduleTime?.get(0)?.endTime}")
            }*/
        } else {
            val dateTime =
                (context as BaseActivity).getDateBooking((main_list.get(position).createdAt!!))
            Log.e("klsmkajsckas", "$dateTime")
            holder.tv_datetime.text = dateTime
        }




        holder.itemView.setOnClickListener {
           // if(type == 0){
               // (context as BaseActivity).navigate(BookingDetailActivity::class.java)
                context.startActivity(
                    Intent(context,BookingDetailActivity::class.java)
                        .putExtra("_id",main_list.get(position).id)
                        .putExtra("details",main_list.get(position)))
            //}
        }

    }
}

class BookingViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_status = itemView.tv_status
    val iv_maid = itemView.iv_maid
    val tv_order_id = itemView.tv_order_id
    val tv_service_type = itemView.tv_service_type
    val tv_datetime = itemView.tv_datetime
    val tv_address = itemView.tv_address

    //@Mukesh
    val servicesNames = itemView.servicesNames
    val servicesNameRecycler = itemView.servicesNameRecycler
    val servicesTime = itemView.servicesTime
    val servicesTimeRecyclerView = itemView.servicesTimeRecyclerView

}