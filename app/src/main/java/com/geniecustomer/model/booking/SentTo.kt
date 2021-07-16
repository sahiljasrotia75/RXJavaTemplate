package com.geniecustomer.model.booking

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SentTo(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("isBlock")
	val isBlock: Boolean? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("ratingList")
	val ratingList: List<Any?>? = null,

	@field:SerializedName("document")
	val document: String? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("avgRating")
	val avgRating: Float? = null,

	@field:SerializedName("profileStatus")
	val profileStatus: String? = null,

	@field:SerializedName("bio")
	val bio: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("isEmailVerify")
	val isEmailVerify: Boolean? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("gallery")
	val gallery: List<String?>? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("isPhoneVerified")
	val isPhoneVerified: Boolean? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("isPushEnabled")
	val isPushEnabled: Boolean? = null,

	@field:SerializedName("isApprove")
	val isApprove: Boolean? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("providerStatus")
	val providerStatus: Int? = null
): Serializable