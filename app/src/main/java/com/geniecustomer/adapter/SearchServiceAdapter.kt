package com.geniecustomer.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.model.search_model.ListItem
import com.geniecustomer.view.activities.ServiceManListActivity
import kotlinx.android.synthetic.main.row_for_search.view.*

class SearchServiceAdapter (var context : Context, var list : List<ListItem>) : RecyclerView.Adapter<SearchServiceAdapter.Holder>() {

    var clickListener: ClickItem? = null

    interface ClickItem {
        fun onClickItem(position: Int)
    }

    fun setonClickItem(clickListener: ClickItem) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view  = LayoutInflater.from(context).inflate(R.layout.row_item_for_search,parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.tvName.text = list.get(position).name
       /* if(list.get(position).type.equals("category",ignoreCase = true)){
            holder.tvCatType.setText(list.get(position).type)
        }else if(list.get(position).type.equals("service",ignoreCase = true)){
            holder.tvCatType.setText("in "+list.get(position)?.type)
        }*/

        holder.clChangePass.setOnClickListener {
               val city = (context as BaseActivity).sharedPref.getString("location","")
                Log.e("CITY"," "+city)
                context.startActivity(Intent(context, ServiceManListActivity::class.java)
                        .putExtra("service_id",list.get(position).category)
                        .putExtra("category_id",list.get(position).id)
                        .putExtra("city",city)
                        .putExtra("service_name",list.get(position).name))
        }

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName = itemView.tvName
        var clChangePass = itemView.clChangePass
    }
}