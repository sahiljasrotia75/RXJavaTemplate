package com.geniecustomer.model

import com.geniecustomer.model.service_providers.info.TimeSlotListItem
import com.google.gson.annotations.SerializedName

data class TimeSlotsResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
    val data : List<TimeSlotListItem?>? = null
	)