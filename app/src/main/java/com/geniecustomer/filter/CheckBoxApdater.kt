package com.geniecustomer.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.HitFilterApi
import com.geniecustomer.model.service_providers.service_obj
import kotlinx.android.synthetic.main.category_items_design.view.*

class CheckBoxApdater(val list: MutableList<service_obj>,
                      _context: Context,var hitListener: HitFilterApi
) : RecyclerView.Adapter<CheckBoxApdater.Items>() {
    lateinit var viewHolder : Items

    var clicklisterner : Clicklisterner?= null

    interface Clicklisterner {
        fun ClickListerner(position: Int, checked: Boolean)
    }

    fun setOnclickItem(clickListerner : Clicklisterner){
        this.clicklisterner = clickListerner
    }


    val DATA=1 // if you are using hetrogenious list then use multiple type and handle the view

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Items {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_items_design, parent, false)
        return  Items(view)

    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun getItemViewType(position: Int): Int {
        return DATA
    }
    override fun onBindViewHolder(holder: Items, position: Int) {
        holder.setIsRecyclable(false)
        holder.itemView.tvCatItem.text = list.get(position).name

        holder.cbCatItems.isChecked = list.get(position).selected
        holder.cbCatItems.setOnCheckedChangeListener { compoundButton, b ->
            if(clicklisterner != null){
                clicklisterner?.ClickListerner(position,b)
                hitListener.onHitFilter(true)
            }

        }
    }

    inner class Items(viewHolder: View) : RecyclerView.ViewHolder(viewHolder) {
        var fruitName = viewHolder.tvCatItem
        var cbCatItems = viewHolder.cbCatItems
    }
}