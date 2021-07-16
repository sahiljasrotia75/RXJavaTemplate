package com.geniecustomer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.SelectServiceClick
import com.geniecustomer.model.service_providers.info.ReviewItem
import com.geniecustomer.model.service_providers.info.ServiceListItem
import kotlinx.android.synthetic.main.provider_review_item.view.*
import kotlinx.android.synthetic.main.select_service_item.view.*
import java.text.DecimalFormat

class SelectServiceAdapter(val context : Context, val textview_list: ArrayList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val SERVICE_LIST_IEM = 0
    val REVIEW_LIST_IEM = 1
    var selectServiceInterface : SelectServiceClick?=null
    var df = DecimalFormat("0.00")

    fun selectServiceClick(selectServiceClick: SelectServiceClick){
        this.selectServiceInterface = selectServiceClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == SERVICE_LIST_IEM){
            val view = LayoutInflater.from(context).inflate(R.layout.select_service_item,parent,false)
            return SelectServiceViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.provider_review_item,parent,false)
            return ProvidersReviewViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
       return textview_list.size
    }

    override fun getItemViewType(position: Int): Int {
        if(textview_list.get(position) is ServiceListItem)
            return SERVICE_LIST_IEM
        else
            return REVIEW_LIST_IEM
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == SERVICE_LIST_IEM)
        {
            val data_item = textview_list.get(position) as ServiceListItem
            (holder as SelectServiceViewHolder).tv_service_type.text = data_item.name
            holder.tv_desc.text = data_item.desc

            if (data_item.priceType.equals("Fixed", true)) {
                holder.tv_charges.text = "$" + df.format(data_item.price)
            } else {
//                holder.tv_charges.setText("Starting from $" + df.format(data_item.price) + "/" + data_item.payType)
                holder.tv_charges.text = "Starting from $" + df.format(data_item.price)
            }
            if (data_item.isSelected) {
                holder.tv_select.background =
                    ContextCompat.getDrawable(context, R.drawable.select_round_corner_bg_green)
                holder.tv_select.text = "Selected"
            }else{
                holder.tv_select.background =
                    ContextCompat.getDrawable(context, R.drawable.select_corner_bg)
                holder.tv_select.text = "Select"
            }
            holder.itemView.setOnClickListener {
                selectServiceInterface?.onClick(position,data_item.isSelected)
            }
        }
        else if(getItemViewType(position) == REVIEW_LIST_IEM) {
            val data_item = textview_list.get(position) as ReviewItem
            (holder as ProvidersReviewViewHolder).tv_name.text =
                "" + data_item.userId?.firstName + " " + data_item.userId?.lastName
            holder.tv_desc.text = data_item.review
            // holder.tv_ratings_num.setText(textview_list.size)
            holder.rating_bar.rating = data_item.rating ?: 0f
        }

    }
}

class SelectServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val tv_service_type = itemView.tv_service_type
    val tv_desc = itemView.tv_desc
    val tv_charges = itemView.tv_charges
    val tv_select = itemView.tv_select
}

class ProvidersReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val tv_ratings_num = itemView.tv_ratings_num
    val tv_desc = itemView.tv_desc2
    val tv_name = itemView.tv_name
    val rating_bar = itemView.rating_bar
}