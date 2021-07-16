package com.geniecustomer.model.chat.chat

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ChatListResponse(

	@field:SerializedName("data")
	val data: List<DataItem>? = null,
	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("count")
	val count: Int? = 0


) : Serializable