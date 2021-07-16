package com.geniecustomer.model.signin

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("isBlock")
	val isBlock: Boolean? = null,

	@field:SerializedName("isPhoneVerified")
	val isPhoneVerified: Boolean? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("profileStatus")
	val profileStatus: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("isPushEnabled")
	val isPushEnabled: Boolean? = null,

	@field:SerializedName("isApprove")
	val isApprove: Boolean? = null,

	@field:SerializedName("isEmailVerify")
	val isEmailVerify: Boolean? = null,

	@field:SerializedName("token")
	val token: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("avgRating")
	val avgRating: Double? = 0.0,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("phone")
	var phone: String? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("countryCode")
	val countryCode: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null,

	@field:SerializedName("canChangePassword")
	val canChangePassword: Boolean? = null



)