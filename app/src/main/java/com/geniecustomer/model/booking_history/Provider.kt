package com.geniecustomer.model.booking_history

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Provider(

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

	@field:SerializedName("document")
	val document: String? = null,

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

	@field:SerializedName("canChangePassword")
	val canChangePassword: Boolean? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("countryCode")
	val countryCode: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("gallery")
	val gallery: List<String?>? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

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

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("_id")
	val _id: String? = null,

	@field:SerializedName("providerStatus")
	val providerStatus: Int? = null
) : Serializable