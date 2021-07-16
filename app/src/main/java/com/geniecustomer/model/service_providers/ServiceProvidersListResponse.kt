package com.geniecustomer.model.service_providers

import com.google.gson.annotations.SerializedName

data class ServiceProvidersListResponse(

	@field:SerializedName("data")
	val data: List<DataItem>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("count")
	val count: Int? = 0,

	@field:SerializedName("message")
	val message: String? = null
)