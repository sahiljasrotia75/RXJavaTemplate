package com.geniecustomer.model.otp

import com.google.gson.annotations.SerializedName

data class OtpRequest(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("otpId")
	val otpId: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("otp")
	val otp: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("countryCode")
	val countryCode: String? = null,

	@field:SerializedName("deviceType")
	val deviceType: String? = null,

	@field:SerializedName("pushToken")
     val pushToken: String? = null
)