package com.geniecustomer.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ScheduleItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("isBlock")
	val isBlock: Boolean? = null,

	@field:SerializedName("isDeleted")
	val isDeleted: Boolean? = null,

	@field:SerializedName("service")
	val service: Any? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("startTime")
	val startTime: String? = "",

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = "",

	@field:SerializedName("day")
	val day: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("dateTime_in_miliseconds")
	val dateTime_in_miliseconds: Long? = null

) : Serializable