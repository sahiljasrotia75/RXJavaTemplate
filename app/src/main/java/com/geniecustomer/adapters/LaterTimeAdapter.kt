package com.geniecustomer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.TimeModel
import kotlinx.android.synthetic.main.later_time_item.view.*


class LaterTimeAdapter(val context: Context, val time_model: ArrayList<TimeModel>) :
    RecyclerView.Adapter<LaterTimeViewHolder>() {

    var rvClickPostion: RvClickPostion? = null
    fun setOnClick(rvClickPostion: RvClickPostion) {
        this.rvClickPostion = rvClickPostion
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaterTimeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.later_time_item, parent, false)
        return LaterTimeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return time_model.size
    }

    override fun onBindViewHolder(holder: LaterTimeViewHolder, position: Int) {

        holder.tv_time.text = "" + time_model.get(position).time_slot

        if(time_model.get(position).isSelected){
            holder.tv_time.setTextColor(ContextCompat.getColor(context,R.color.colorWhite))
            holder.parent_item.background = ContextCompat.getDrawable(context,R.drawable.ongoing_back)
        }else{
            holder.tv_time.setTextColor(ContextCompat.getColor(context,R.color._7a828e))
            holder.parent_item.background = ContextCompat.getDrawable(context,R.drawable.home_grid_corners)
        }
        holder.itemView.setOnClickListener {
            if (rvClickPostion != null)
                rvClickPostion?.onRvItemClicked(position)
        }
    }
}

class LaterTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_time = itemView.tv_time
    val parent_item = itemView.parent_item
}