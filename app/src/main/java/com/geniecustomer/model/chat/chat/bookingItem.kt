package com.geniecustomer.model.chat.chat

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class bookingItem(
    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("amount")
    val amount: Double? = 0.0,

    @field:SerializedName("orderId")
    val orderId: String? = null
) : Serializable