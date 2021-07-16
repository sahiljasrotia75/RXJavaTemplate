package com.geniecustomer.model.booking_history

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OngoingItem(

	@field:SerializedName("scheduleTime")
	val scheduleTime: List<Any?>? = null,

	@field:SerializedName("tax")
	val tax: Int? = null,

	@field:SerializedName("services")
	val services: List<ServicesItem?>? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("transactionId")
	val transactionId: String? = null,

	@field:SerializedName("totalAmount")
	val totalAmount: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("provider")
	val provider: Provider? = null,

	@field:SerializedName("bookingType")
	val bookingType: String? = null,

	@field:SerializedName("ratingCount")
	val ratingCount: Double? = null,

	@field:SerializedName("avgRating")
	val avgRating: Double? = null,

	@field:SerializedName("desc")
	val desc: String? = "",

	@field:SerializedName("location")
	val location: Location? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null

) :Serializable