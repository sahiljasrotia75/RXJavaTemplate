package com.geniecustomer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.geniecustomer.R
import com.geniecustomer.api.Urls
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.model.chat_history.DataItem
import com.geniecustomer.utils.GlideApp
import kotlinx.android.synthetic.main.conversation_item_receive.view.*
import kotlinx.android.synthetic.main.conversation_item_send.view.*
import kotlinx.android.synthetic.main.offer_receive_request.view.*
import java.text.DecimalFormat

class ConversationAdapter(
    val context: Context,
    val main_list: ArrayList<DataItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var df = DecimalFormat("0.00")
    var itemClick : ItemClick ?=null

    interface ItemClick{
        fun onItemClick(position: Int,type: Int)
    }

    fun setOnItemClick(itemClick : ItemClick ){
        this.itemClick = itemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.conversation_item_send, parent, false)
            return ConversationViewHolderSend(view)
        } else if (viewType == 1) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.conversation_item_receive, parent, false)
            return ConversationViewHolderReceive(view)
        }else if (viewType == 2) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.offer_receive_request, parent, false)
            return ConversationViewHolderOffer(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.conversation_item_receive, parent, false)
            return ConversationViewHolderReceive(view)
        }

    }

    override fun getItemCount(): Int {
        return main_list.size
    }

    override fun getItemViewType(position: Int): Int {
        return main_list.get(position).typeText
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        when (holder.itemViewType) {
            0 -> {
                (holder as ConversationViewHolderSend).tv_send.text =
                    main_list.get(position).message!!.toString().trim()
                if (position != main_list.size - 1)
                    if (main_list.get(position + 1).typeText == 0) {
                        holder.tv_send.background =
                            (context as BaseActivity).resources.getDrawable(R.drawable.send_chat_back)
                    } else {
                        holder.tv_send.background =
                            (context as BaseActivity).resources.getDrawable(R.drawable.send_chat_back_last)

                        //  (holder as ConversationViewHolderSend).constraint_parent.setPadding(0,0,0,(context as BaseActivity).resources.getDimensionPixelSize(R.dimen._20sdp))
                    }
                else {
                    holder.tv_send.background =
                        (context as BaseActivity).resources.getDrawable(R.drawable.send_chat_back_last)
                    //  (holder as ConversationViewHolderSend).constraint_parent.setPadding(0,0,0,(context as BaseActivity).resources.getDimensionPixelSize(R.dimen._20sdp))
                }
            }
            1 -> {

                (holder as ConversationViewHolderReceive).tv_recieve.text =
                    main_list.get(position).message!!.toString().trim()

                if (position != main_list.size - 1)
                    if (main_list.get(position + 1).typeText == 1) {
                        holder.iv_dp.visibility = View.INVISIBLE
                        holder.tv_recieve.background =
                            (context as BaseActivity).resources.getDrawable(R.drawable.receive_chat_back)
                    } else {

                        GlideApp.with(context)
                            .load(Urls.BASE_URL + main_list.get(position).sender?.image)
                            .placeholder(R.drawable.dummy_placeholder)
                            .error(R.drawable.dummy_placeholder)
                            .into(holder.iv_dp)
                        holder.iv_dp.visibility = View.VISIBLE
                        holder.tv_recieve.background =
                            (context as BaseActivity).resources.getDrawable(R.drawable.receive_chat_back_last)
                        //   (holder as ConversationViewHolderReceive).layout.setPadding(0,0,0,(context as BaseActivity).resources.getDimensionPixelSize(R.dimen._20sdp))
                    }
                else {
                    holder.tv_recieve.background =
                        (context as BaseActivity).resources.getDrawable(R.drawable.receive_chat_back_last)
                    holder.iv_dp.visibility = View.VISIBLE
                    GlideApp.with(context)
                        .load(Urls.BASE_URL + main_list.get(position).sender?.image)
                        .placeholder(R.drawable.dummy_placeholder)
                        .error(R.drawable.dummy_placeholder)
                        .into(holder.iv_dp)
                    //   (holder as ConversationViewHolderReceive).layout.setPadding(0,0,0,(context as BaseActivity).resources.getDimensionPixelSize(R.dimen._20sdp))
                }

            }
            2 -> {

                if (position != main_list.size - 1) {

                } else {
                    (holder as ConversationViewHolderOffer).main_layout.setPadding(
                        0,
                        0,
                        0,
                        (context as BaseActivity).resources.getDimensionPixelSize(R.dimen._20sdp)
                    )
                }

                if (main_list.get(position).status!!.isEmpty()) {
                    (holder as ConversationViewHolderOffer).tv_accept.visibility = View.VISIBLE
                    holder.tv_accept.visibility = View.VISIBLE
                    holder.tv_decline.visibility = View.VISIBLE
                    holder.tv_status.visibility = View.GONE
                } else if (main_list[position].status!!.equals("Accepted", true)) {
                    (holder as ConversationViewHolderOffer).tv_accept.visibility = View.GONE
                    holder.tv_accept.visibility = View.GONE
                    holder.tv_decline.visibility = View.GONE
                    holder.tv_status.visibility = View.VISIBLE
                    holder.tv_status.text = "Accepted"
                } else if (main_list[position].status!!.equals("Rejected", true)) {
                    (holder as ConversationViewHolderOffer).tv_accept.visibility = View.GONE
                    holder.tv_accept.visibility = View.GONE
                    holder.tv_decline.visibility = View.GONE
                    holder.tv_status.visibility = View.VISIBLE
                    holder.tv_status.text = "Declined"
                } else if (main_list[position].status!!.equals("Pending", true)) {
                    (holder as ConversationViewHolderOffer).tv_accept.visibility = View.GONE
                    holder.tv_accept.visibility = View.VISIBLE
                    holder.tv_decline.visibility = View.VISIBLE
                    holder.tv_status.visibility = View.GONE
                } else {
                    (holder as ConversationViewHolderOffer).tv_accept.visibility = View.GONE
                    holder.tv_accept.visibility = View.GONE
                    holder.tv_decline.visibility = View.GONE
                    holder.tv_status.visibility = View.VISIBLE
                    holder.tv_status.text = "Expired"
                }
                holder.tv_accept.setOnClickListener {
                    if (itemClick != null) {
                        itemClick?.onItemClick(position, 1)
                    }
                    /*(context as ConversationActivity).startActivity(
                        Intent(context,PaymentActivity::class.java)
                            .putExtra("details",context.booking_details))*/
                }

                val amountPay = main_list.get(position).negotiateAmount
//                holder.tv_amount.setText("$"+amountPay)

                holder.tv_amount.text = "$" + String.format("%.2f", amountPay.toFloat())

                holder.tv_decline.setOnClickListener {
                    if (itemClick != null) {
                        itemClick?.onItemClick(position, 2)
                    }
                }
            }
        }

    }
}

class ConversationViewHolderSend(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_send = itemView.tv_send
    val constraint_parent = itemView.constraint_parent
}
class ConversationViewHolderOffer(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_accept = itemView.tv_accept
    val tv_decline = itemView.tv_decline
    val tv_amount = itemView.tv_amount
    val tv_status = itemView.tv_status
    val main_layout = itemView.main_layout
}

class ConversationViewHolderReceive(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tv_recieve = itemView.tv_recieve
    val iv_dp = itemView.iv_dp
    val layout = itemView.layout
}