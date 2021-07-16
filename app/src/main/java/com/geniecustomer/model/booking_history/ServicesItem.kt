package com.geniecustomer.model.booking_history

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServicesItem(

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("service")
	val service: Service? = null,

	@field:SerializedName("_id")
	val id: String? = null
) : Serializable