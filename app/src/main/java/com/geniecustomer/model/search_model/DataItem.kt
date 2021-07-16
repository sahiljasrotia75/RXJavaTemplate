package com.geniecustomer.model.search_model

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("list")
	var list: List<ListItem>? = null
)