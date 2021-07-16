package com.geniecustomer.model.service_providers.info

import com.google.gson.annotations.SerializedName

data class ServiceListItem(

	@field:SerializedName("priceType")
	val priceType: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("payType")
	val payType: String? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("service")
	val service: List<ServiceItem?>? = null,

	@field:SerializedName("price")
	val price: Double? = 0.0,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("category")
	val category: Category? = null,

	@field:SerializedName("desc")
	val desc: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	var isSelected: Boolean = false,
	var position :Int?=null
)