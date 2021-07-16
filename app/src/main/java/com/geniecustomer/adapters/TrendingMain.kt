package com.geniecustomer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.geniecustomer.R
import com.geniecustomer.api.Urls
import com.geniecustomer.interfaces.OnCategoryClicked
import com.geniecustomer.view.fragments.trendingResponse.Data
import kotlinx.android.synthetic.main.home_grid_item.view.*

class TrendingMain(var context: Context, var list: ArrayList<Data>) :
    RecyclerView.Adapter<TrendingMain.ViewHolder>() {

    var onCategoryClicked: OnCategoryClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingMain.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_grid_item, parent, false)
        return ViewHolder(view)
    }


    fun onCategoryClicked(onCategoryClicked: OnCategoryClicked) {
        this.onCategoryClicked = onCategoryClicked
    }


    override fun onBindViewHolder(holder: TrendingMain.ViewHolder, position: Int) {
        try {

            Glide.with(context).load(Urls.BASE_URL + list[position].image).into(holder.imageview)

            holder.textview.text = list[position].name

            holder.mainClick.setOnClickListener {
                onCategoryClicked?.onClick(
                    list[position].id!!,
                    list[position].category?._id!!,
                    list[position].name!!
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textview = itemView.textview
        val imageview = itemView.imageview
        val mainClick = itemView.mainClick
    }


}