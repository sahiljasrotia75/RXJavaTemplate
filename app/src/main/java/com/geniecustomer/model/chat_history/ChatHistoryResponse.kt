package com.geniecustomer.model.chat_history

import com.google.gson.annotations.SerializedName

data class ChatHistoryResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,


	@field:SerializedName("bookingStatus")
	val bookingStatus: String? = null
)