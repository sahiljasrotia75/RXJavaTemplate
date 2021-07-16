package com.geniecustomer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.api.Urls
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.service_providers.DataItem
import com.geniecustomer.utils.GlideApp
import kotlinx.android.synthetic.main.service_man_list_item.view.*

class ServiceManListItem(val context: Context, val textview_list: ArrayList<DataItem>) :
    RecyclerView.Adapter<ServiceManListItemViewHolder>() {

    var rvClickPostion: RvClickPostion? = null
    fun setOnClick(rvClickPostion: RvClickPostion) {
        this.rvClickPostion = rvClickPostion
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServiceManListItemViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.service_man_list_item, parent, false)
        return ServiceManListItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return textview_list.size
    }

    override fun onBindViewHolder(holder: ServiceManListItemViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            if (rvClickPostion != null)
                rvClickPostion?.onRvItemClicked(position)
        }

        GlideApp.with(context)
            .load(Urls.BASE_URL + textview_list.get(position).image)
            .fallback(ContextCompat.getDrawable(context, R.drawable.ic_dummy_dp))
            .into(holder.iv_provider_image)
        holder.tv_name.text = textview_list[position].name
        if (textview_list[position].ratingCount!!.toInt() < 2) {
            holder.tv_ratings_num.text = "(" + textview_list[position].ratingCount + " Review)"
        } else {
            holder.tv_ratings_num.text = "(" + textview_list[position].ratingCount + " Reviews)"
        }
        holder.tv_desc.text = textview_list[position].bio
        holder.rating_bar.rating = textview_list[position].avgRating!!
        if (textview_list[position].price.isNotEmpty()) {
            if (textview_list[position].priceType == "Negotiate") {
                holder.tv_price.text = "Starting from $ ${textview_list[position].price}"
            } else {
                holder.tv_price.text = "$ ${textview_list[position].price}"
            }
        }
    }
}

class ServiceManListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val iv_provider_image = itemView.iv_provider_image
    val tv_name = itemView.tv_name
    val rating_bar = itemView.rating_bar
    val tv_ratings_num = itemView.tv_ratings_num

    //    val tv_exp = itemView.tv_exp
    val tv_desc = itemView.tv_desc
    val tv_price = itemView.tv_price
//    val tv_charges = itemView.tv_charges
}