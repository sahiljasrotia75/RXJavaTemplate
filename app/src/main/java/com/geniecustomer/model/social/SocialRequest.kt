package com.geniecustomer.model.social

import com.google.gson.annotations.SerializedName

data class SocialRequest(

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("socialId")
	val socialId: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("deviceType")
val deviceType: String? = null,

@field:SerializedName("pushToken")
val pushToken: String? = null
)
