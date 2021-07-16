package com.geniecustomer.model.otp

import com.google.gson.annotations.SerializedName

data class Data(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("isBlock")
	val isBlock: Boolean? = null,

	@field:SerializedName("isPhoneVerified")
	val isPhoneVerified: Boolean? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("profileStatus")
	val profileStatus: String? = null,

	@field:SerializedName("isActive")
	val isActive: Boolean? = null,

	@field:SerializedName("isApprove")
	val isApprove: Boolean? = null,

	@field:SerializedName("isEmailVerify")
	val isEmailVerify: Boolean? = null,

	@field:SerializedName("token")
	val token: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("otpId")
	val otpId: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)