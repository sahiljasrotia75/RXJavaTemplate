package com.geniecustomer.model.search

import com.google.gson.annotations.SerializedName

data class SearchModel(

	@field:SerializedName("data")
	val data: List<DataItem>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)