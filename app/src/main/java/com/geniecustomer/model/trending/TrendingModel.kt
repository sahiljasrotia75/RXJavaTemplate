package com.geniecustomer.model.trending

import com.google.gson.annotations.SerializedName

data class TrendingModel(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)