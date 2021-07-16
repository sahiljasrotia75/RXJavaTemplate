package com.geniecustomer.model.service_providers.info

import com.google.gson.annotations.SerializedName

data class ServiceProviderInfoResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)