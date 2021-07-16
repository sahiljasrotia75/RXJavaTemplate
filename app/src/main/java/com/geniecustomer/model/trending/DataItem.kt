package com.geniecustomer.model.trending

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("list")
	val list: List<ListItem?>? = null
)