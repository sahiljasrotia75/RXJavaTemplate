package com.geniecustomer.model.signin

import com.google.gson.annotations.SerializedName

data class SigninRequest(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("countryCode")
	val countryCode: String? = null ,

	@field:SerializedName("deviceType")
	val deviceType: String? = null ,

	@field:SerializedName("pushToken")
	val pushToken: String? = null
)