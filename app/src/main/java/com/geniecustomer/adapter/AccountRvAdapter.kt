package com.geniecustomer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.AccountDataModel
import kotlinx.android.synthetic.main.account_rv_design.view.*

class AccountRvAdapter (val ctx: Context, val profileDataList: ArrayList<AccountDataModel>, var onclickPostion : RvClickPostion) :
    RecyclerView.Adapter<AccountRvAdapter.AccountViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.account_rv_design, parent, false)
        return AccountViewHolder(v)
    }

    override fun getItemCount(): Int {

        return profileDataList.size
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int)
    {
        holder.itemView.ivicon.setImageResource(profileDataList[position].icon)
        holder.itemView.tvTitle.text = profileDataList[position].title
        holder.itemView.setOnClickListener {
            onclickPostion.onRvItemClicked(position)
        }
        holder.itemView.tvTitle.setOnClickListener {
            onclickPostion.onRvItemClicked(position)
        }
    }

    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}