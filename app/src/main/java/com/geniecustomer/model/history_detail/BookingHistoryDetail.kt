package com.geniecustomer.model.history_detail

import com.geniecustomer.model.booking_history.OngoingItem
import com.google.gson.annotations.SerializedName

data class BookingHistoryDetail(
    @field:SerializedName("data")
    val data: OngoingItem? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)