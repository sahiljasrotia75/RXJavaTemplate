package com.geniecustomer.model.booking_history

import com.geniecustomer.model.booking.DataItem
import com.google.gson.annotations.SerializedName

data class BookingHistoryResponse(

	@field:SerializedName("data")
	val data: DataItem? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)