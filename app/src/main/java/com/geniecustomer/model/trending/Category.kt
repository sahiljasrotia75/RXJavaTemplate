package com.geniecustomer.model.trending

import com.google.gson.annotations.SerializedName

data class Category(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)