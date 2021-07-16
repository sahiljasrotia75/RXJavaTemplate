package com.geniecustomer.model.service_providers.info

import com.google.gson.annotations.SerializedName

data class UserId(

	@field:SerializedName("googleId")
	val googleId: String? = null,

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

	@field:SerializedName("document")
	val document: Any? = null,

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

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("canChangePassword")
	val canChangePassword: Boolean? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("gallery")
	val gallery: List<Any?>? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("providerStatus")
	val providerStatus: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)