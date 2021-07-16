package com.geniecustomer.model.chat.chat

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Receiver(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("canChangePassword")
	val canChangePassword: Boolean? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("id")
	val id: String? = null
) : Serializable