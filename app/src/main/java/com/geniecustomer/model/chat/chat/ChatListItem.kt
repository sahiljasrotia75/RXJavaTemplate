package com.geniecustomer.model.chat.chat

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChatListItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("receiver")
	val receiver: Receiver? = null,

	@field:SerializedName("sender")
	val sender: Sender? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("isSeen")
	val isSeen: Boolean? = null,

	@field:SerializedName("bookingId")
	val bookingId: bookingItem? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
) : Serializable