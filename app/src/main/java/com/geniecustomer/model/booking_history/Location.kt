package com.geniecustomer.model.booking_history

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Location(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null
) : Serializable