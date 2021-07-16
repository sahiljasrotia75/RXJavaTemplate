package com.geniecustomer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.model.search_model.ListItem
import kotlinx.android.synthetic.main.row_for_search.view.*

class SearchItemAdapter (var context : Context, var list : List<ListItem>) : RecyclerView.Adapter<SearchItemAdapter.Holder>() {

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
            if(clickListener != null){
                clickListener!!.onClickItem(position)
            }
        }

    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName = itemView.tvName
        var clChangePass = itemView.clChangePass
    }
}