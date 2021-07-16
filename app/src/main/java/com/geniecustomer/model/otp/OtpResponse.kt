package com.geniecustomer.model.otp

import com.google.gson.annotations.SerializedName

data class OtpResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)