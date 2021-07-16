package com.geniecustomer.model.booking_history

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ServiceItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
) : Serializable