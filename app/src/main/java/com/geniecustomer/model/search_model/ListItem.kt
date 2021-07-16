package com.geniecustomer.model.search_model

import com.google.gson.annotations.SerializedName

data class ListItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("type")
	val type: String? = null
)