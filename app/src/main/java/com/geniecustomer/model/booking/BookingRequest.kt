package com.geniecustomer.model.booking

import com.google.gson.annotations.SerializedName

data class BookingRequest(

	@field:SerializedName("scheduleTime")
	val scheduleTime: List<String>? = null,

	@field:SerializedName("amount")
	val amount: Double? = 0.0,

	@field:SerializedName("sentTo")
	val sentTo: String? = null,

	@field:SerializedName("service")
	val service: List<String?>? = null,

	@field:SerializedName("bookingType")
	val bookingType: String? = null,

	@field:SerializedName("priceType")
	val priceType: String? = null,

	@field:SerializedName("location")
	val location: Location? = null,

	@field:SerializedName("desc")
	val desc: String? = null
)