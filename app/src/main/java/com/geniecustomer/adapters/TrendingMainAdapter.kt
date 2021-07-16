package com.geniecustomer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.OnCategoryClicked
import com.geniecustomer.model.service_providers.service_obj
import kotlinx.android.synthetic.main.trending_main_item.view.*

class TrendingMainAdapter (val context : Context, val main_list: ArrayList<service_obj>, val sub_list: ArrayList<ArrayList<service_obj>>,val isClickable:Boolean) : RecyclerView.Adapter<TrendingViewHolder>() {


    var  onCategoryClicked : OnCategoryClicked?=null

    fun onCategoryClicked(onCategoryClicked: OnCategoryClicked){
        this.onCategoryClicked = onCategoryClicked
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.trending_main_item,parent,false)
        return TrendingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return main_list.size
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        holder.tv_header.text = main_list.get(position).name
        // sub_list.get(position)

        val adapter = TrendingSubAdapter(
            context,
            main_list.get(position).id,
            sub_list.get(position),
            isClickable
        )
        holder.sub_recycler_view.adapter = adapter
        holder.sub_recycler_view.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter.onCategoryClicked(object : OnCategoryClicked {
            override fun onClick(service_id: String, category_id: String, service_name: String) {
                onCategoryClicked?.onClick(service_id, category_id, service_name)
            }
        })

    }

}



class TrendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val sub_recycler_view = itemView.sub_recycler_view
    val tv_header = itemView.tv_header
}
