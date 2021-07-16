package com.geniecustomer.model.signup

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("otpId")
	val otpId: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)