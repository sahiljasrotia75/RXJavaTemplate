package com.geniecustomer.model.edit_profile

import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(

	@field:SerializedName("data")
	val data: Any? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)