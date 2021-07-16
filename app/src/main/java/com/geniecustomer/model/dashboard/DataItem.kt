package com.geniecustomer.model.dashboard

import com.google.gson.annotations.SerializedName

data class DataItem(

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

	@field:SerializedName("serviceList")
	val serviceList: List<ServiceListItem?>? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null ,

	var isSelected : Boolean = false
)