package com.geniecustomer.model.service_providers

import com.google.gson.annotations.SerializedName

data class ServiceProvidersListRequest(

	@field:SerializedName("provider")
	val provider: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("service")
	val service: String? = null,

	@field:SerializedName("limit")
	val limit: Int? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null,

	@field:SerializedName("skip")
	val skip: Int? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("type")
	val type: String? = null ,

	@field:SerializedName("sortingType")
	var sortingType : String
)