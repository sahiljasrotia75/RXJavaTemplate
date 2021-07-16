package com.geniecustomer.model.service_providers.info

import com.google.gson.annotations.SerializedName

data class ReviewItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("provider")
	val provider: String? = null,

	@field:SerializedName("review")
	val review: String? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("rating")
	val rating: Float? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: UserId? = null,

	@field:SerializedName("bookingId")
	val bookingId: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)