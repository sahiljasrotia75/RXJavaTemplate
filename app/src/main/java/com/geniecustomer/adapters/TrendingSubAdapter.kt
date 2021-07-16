package com.geniecustomer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.OnCategoryClicked
import com.geniecustomer.model.service_providers.service_obj
import kotlinx.android.synthetic.main.trending_sub_item.view.*

class TrendingSubAdapter (val context : Context, val service_id : String, val main_list: ArrayList<service_obj>, val isClickable : Boolean) : RecyclerView.Adapter<TrendingSubViewHolder>() {

    var  onCategoryClicked : OnCategoryClicked?=null

    fun onCategoryClicked(onCategoryClicked: OnCategoryClicked){
        this.onCategoryClicked = onCategoryClicked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingSubViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.trending_sub_item,null)
        return TrendingSubViewHolder(view)
    }

    override fun getItemCount(): Int {
        return main_list.size
    }

    override fun onBindViewHolder(holder: TrendingSubViewHolder, position: Int) {
        holder.tv_sub_header.text = main_list.get(position).name
        holder.itemView.setOnClickListener {
            if(isClickable){

                if(onCategoryClicked!=null){
                    onCategoryClicked?.onClick(service_id,main_list.get(position).id,main_list.get(position).name)
                }

//                ((context as HomeActivity).supportFragmentManager.fragments.get(0) as HomeFragment).dialog?.dismiss()
//                (context as BaseActivity).startActivity(Intent(context,ServiceManListActivity::class.java)
//                    .putExtra("service_id",)
//                    .putExtra("category_id",)
            }
        }
//        val adapter =
//        holder.sub_recycler_view.

    }
}

class TrendingSubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_sub_header = itemView.tv_sub_header

}