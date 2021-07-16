package com.geniecustomer.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.model.MessageItem
import com.geniecustomer.model.chat.chat.DataItem
import com.geniecustomer.utils.GlideApp
import com.geniecustomer.view.activities.ConversationActivity
import kotlinx.android.synthetic.main.chat_list_layout.view.*

class ChatListAdapter (val context : Context, val main_list: ArrayList<DataItem>) : RecyclerView.Adapter<ChatListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout,parent,false)
        return ChatListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return main_list.size
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {

        holder.tv_name.text = main_list.get(position).name ?: ""
        if(main_list.get(position).chatList?.get(0)?.message!!.isEmpty())
            holder.tv_id.text = "offer"
        else
            holder.tv_id.text = main_list.get(position).chatList?.get(0)?.message

        GlideApp.with(context).load(Urls.BASE_URL+main_list.get(position).image)
            .placeholder(R.drawable.dummy_placeholder)
            .error(R.drawable.dummy_placeholder)
            .into(holder.iv_profyl)

        holder.tv_days.text =
            (context as BaseActivity).getTime(main_list.get(position).chatList?.get(0)?.createdAt)
        holder.itemView.setOnClickListener {
            context.editor.putString("lastMessage", main_list.get(position).id).apply()
            context.startActivity(
                Intent(context, ConversationActivity::class.java)
                    .putExtra(
                        "messageItem",
                        MessageItem(
                            main_list.get(position).id,
                            context.user_obj?._id,
                            "",
                            main_list.get(position).booking?.id ?: "",
                            main_list.get(position).name ?: "",
                            main_list.get(position).image ?: "",
                            main_list.get(position).booking?.status ?: ""
                        )
                    )
            )
        }

    }
}

class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var iv_profyl = itemView.iv_profyl
    var tv_name = itemView.tv_name
    var tv_id = itemView.tv_id
    var tv_days = itemView.tv_days

}