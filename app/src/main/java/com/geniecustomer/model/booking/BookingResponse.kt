package com.geniecustomer.model.booking

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BookingResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
):Serializable