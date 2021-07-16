package com.geniecustomer.model.search_model

import com.google.gson.annotations.SerializedName

data class SearchResponse(

	@field:SerializedName("data")
	val data: List<DataItem>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)