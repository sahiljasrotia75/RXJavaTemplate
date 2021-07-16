package com.geniecustomer.model.signin

import com.google.gson.annotations.SerializedName

data class SigninResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)