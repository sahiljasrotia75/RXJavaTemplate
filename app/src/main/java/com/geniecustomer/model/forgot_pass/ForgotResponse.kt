package com.geniecustomer.model.forgot_pass

import com.google.gson.annotations.SerializedName

data class ForgotResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)