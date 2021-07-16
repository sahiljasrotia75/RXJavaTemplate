package com.geniecustomer.model.chat_history

import com.google.gson.annotations.SerializedName

data class Sender(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("canChangePassword")
	val canChangePassword: Boolean? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)