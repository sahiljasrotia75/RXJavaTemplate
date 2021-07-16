package com.geniecustomer.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R

import com.geniecustomer.model.search_model.DataItem
import com.geniecustomer.model.search_model.ListItem
import kotlinx.android.synthetic.main.row_for_search.view.*

class SearchAdapter (var context : Context, var list : MutableList<DataItem>) : RecyclerView.Adapter<SearchAdapter.Holder>() {

    var clickListener: ClickItem? = null
    var adapter : SearchItemAdapter? = null
    var adapterService : SearchServiceAdapter? = null
    var params: ConstraintLayout.LayoutParams ?= null

    interface ClickItem {
        fun onClickItem(position: Int)
    }

    fun setonClickItem(clickListener: ClickItem) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

            val view  = LayoutInflater.from(context).inflate(R.layout.row_for_search,parent,false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.setIsRecyclable(false)
        val catlist : ArrayList<ListItem> = ArrayList()
        val servicelist : ArrayList<ListItem> = ArrayList()

        if(list.get(position).name.equals("Categories")) {
            holder.cateory_card.visibility = View.VISIBLE
            holder.tvName.text = list.get(position).name
            holder.item_rv.layoutManager = LinearLayoutManager(context)
            catlist.clear()
            for(i in list.get(position).list!!.indices){
                catlist.add(list.get(position).list!!.get(i))
            }
            adapter = SearchItemAdapter(context,catlist)
            holder.item_rv.adapter = adapter
            adapter!!.notifyDataSetChanged()
            listerer(holder.item_rv)
            Log.e("CATE","INNER : size "+list.get(position).list!!.size)
            if(list.get(position).list!!.size == 0){
                hideView(holder.cateory_card)
                holder.cateory_card.visibility = View.GONE
            }
        }

        if(list.get(position).name.equals("Services")) {
            Log.e("SERVICE","INNER : size "+list.get(position).list!!.size)



            holder.item_service_rv.visibility = View.VISIBLE
            holder.tvServiceName.text = list.get(position).name
            holder.service_card.visibility = View.VISIBLE
            servicelist.clear()
            servicelist.addAll(list.get(position).list!!)
            holder.item_service_rv.layoutManager = LinearLayoutManager(context)
            adapterService = SearchServiceAdapter(context,servicelist)
            holder.item_service_rv.adapter = adapterService
            adapterService!!.notifyDataSetChanged()

            if(list.get(position).list!!.size == 0){
                hideView(holder.service_card)
                holder.service_card.visibility = View.GONE
            }


        }

        /*holder.tvName.setText(list.get(position).name)
        if(list.get(position).type.equals("category",ignoreCase = true)){
            holder.tvCatType.setText(list.get(position).type)
        }else if(list.get(position).type.equals("service",ignoreCase = true)){
            holder.tvCatType.setText("in "+list.get(position)?.type)
        }

        holder.clChangePass.setOnClickListener {

            if(clickListener != null){
                clickListener!!.onClickItem(position)
            }
        }*/

    }

    fun listerer(itemRv: RecyclerView) {
        adapter!!.setonClickItem(object : SearchItemAdapter.ClickItem{
            override fun onClickItem(position: Int) {
                clickListener!!.onClickItem(position)
            }
        })
    }


    fun hideView(rootLayout: ConstraintLayout) {
        params = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params!!.height = 0
        rootLayout.layoutParams = params
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName = itemView.tvName
        var tvServiceName = itemView.tvServiceName
        var item_rv = itemView.item_rv
        var item_service_rv = itemView.item_service_rv
        var clChangePass = itemView.clChangePass
        var cateory_card = itemView.cateory_card
        var service_card = itemView.service_card
    }
}