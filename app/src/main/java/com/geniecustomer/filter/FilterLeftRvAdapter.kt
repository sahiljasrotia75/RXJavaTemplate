package com.geniecustomer.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.FilterListDataModel
import kotlinx.android.synthetic.main.row_for_filtercategory.view.*

class FilterLeftRvAdapter(
    private val activity: Activity,
    val catItems: ArrayList<FilterListDataModel>,
    private var itemClickListener: RvClickPostion
) : RecyclerView.Adapter<FilterLeftRvAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.row_for_filtercategory, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.clCatTxt.setBackgroundColor(Color.parseColor(catItems[position].bgcolor))
        holder.txt_capter_name.text = catItems[position].category

        holder.clCatTxt?.setOnClickListener {
            if (itemClickListener != null)
                itemClickListener.onRvItemClicked(position)
        }
    }


    override fun getItemCount(): Int {
        return catItems.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_capter_name = view.tvCat
        val clCatTxt = view.clCatTxt
    }
}