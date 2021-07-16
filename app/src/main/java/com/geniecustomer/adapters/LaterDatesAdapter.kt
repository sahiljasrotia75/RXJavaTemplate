package com.geniecustomer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.DateModel
import kotlinx.android.synthetic.main.later_date_item.view.*

class LaterDatesAdapter(val context: Context, val main_list: ArrayList<DateModel>, val selected_pos: ArrayList<Int>?) :
    RecyclerView.Adapter<LaterDatesViewHolder>() {

    var rvClickPostion: RvClickPostion? = null
    fun setOnClick(rvClickPostion: RvClickPostion) {
        this.rvClickPostion = rvClickPostion
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaterDatesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.later_date_item, parent, false)
        return LaterDatesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return main_list.size
    }

    override fun onBindViewHolder(holder: LaterDatesViewHolder, position: Int) {
        holder.tv_date.text = "" + main_list.get(position).date
        holder.tv_month.text = "" + main_list.get(position).month
        if (selected_pos?.get(0) == position) {
            holder.tv_date.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            holder.tv_month.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            holder.parent_item.background =
                ContextCompat.getDrawable(context, R.drawable.ongoing_back1)
        } else {
            holder.tv_date.setTextColor(ContextCompat.getColor(context, R.color._7a828e))
            holder.tv_month.setTextColor(ContextCompat.getColor(context, R.color._7a828e))
            holder.parent_item.background =
                ContextCompat.getDrawable(context, R.drawable.home_grid_corners)
        }
        holder.itemView.setOnClickListener {
            if (rvClickPostion != null)
                rvClickPostion?.onRvItemClicked(position)
        }
    }
}

class LaterDatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_date = itemView.tv_date
    val tv_month = itemView.tv_month
    val parent_item = itemView.parent_item
}