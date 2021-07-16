package com.geniecustomer.model.service_providers.info

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ListItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isBlock")
	val isBlock: Boolean? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("service")
	val service: String? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("startTime")
	val startTime: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("day")
	val day: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
):Serializable