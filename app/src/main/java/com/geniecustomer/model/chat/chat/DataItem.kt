package com.geniecustomer.model.chat.chat

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("booking")
	val booking: bookingItem? = null,

	@field:SerializedName("chatList")
	val chatList: List<ChatListItem?>? = null
) : Serializable