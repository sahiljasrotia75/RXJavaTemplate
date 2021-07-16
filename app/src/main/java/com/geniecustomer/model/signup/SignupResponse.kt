package com.geniecustomer.model.signup

import com.google.gson.annotations.SerializedName

data class SignupResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)