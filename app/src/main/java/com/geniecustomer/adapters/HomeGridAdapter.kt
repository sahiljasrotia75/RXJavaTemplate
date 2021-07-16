package com.geniecustomer.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.geniecustomer.R
import com.geniecustomer.api.Urls
import com.geniecustomer.interfaces.HomeItemClick
import com.geniecustomer.model.dashboard.DataItem
import com.geniecustomer.utils.GlideApp
import kotlinx.android.synthetic.main.home_grid_item.view.*

class HomeGridAdapter(val context : Context, val list: ArrayList<DataItem>) : RecyclerView.Adapter<ViewHolder>() {

    var homeItemClick : HomeItemClick?=null

    fun setOnHomeItemClick(homeItemClick: HomeItemClick){
        this.homeItemClick = homeItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_grid_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textview.text = list.get(position).name
        Log.e("smnckasckmkas", "Base Url :- ${Urls.BASE_URL + list[position].image}")
        GlideApp.with(context).load(Urls.BASE_URL + list[position].image).diskCacheStrategy(
            DiskCacheStrategy.ALL
        ).into(holder.imageview)

//        GlideApp.with(context).load(Urls.BASE_URL+list.get(position).image).into(object : CustomTarget<Drawable>(){
//            override fun onLoadCleared(placeholder: Drawable?) {
//
//            }
//
//            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                holder.textview.setCompoundDrawablesRelativeWithIntrinsicBounds(null,resource,null,null)
//            }
//
//        })

        holder.itemView.setOnClickListener {
            if(homeItemClick!=null){
                homeItemClick?.onClick(position)
            }
        }
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val textview = itemView.textview
    val imageview = itemView.imageview
}