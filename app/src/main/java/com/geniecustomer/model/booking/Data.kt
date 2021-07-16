package com.geniecustomer.model.booking

import com.geniecustomer.model.ScheduleItem
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(

    @field:SerializedName("reason")
    val reason: String? = null,

    @field:SerializedName("scheduleTime")
    val scheduleTime: ArrayList<ScheduleItem?> = ArrayList(),

    @field:SerializedName("isPaymentDone")
    val isPaymentDone: Boolean? = null,

    @field:SerializedName("isUserRated")
    val isUserRated: Boolean? = false,

    @field:SerializedName("cancelDate")
    val cancelDate: Any? = null,

    @field:SerializedName("amount")
    val amount: Double? = 0.0,

    @field:SerializedName("negotiateAmount")
    val negotiateAmount: Double? = 0.0,

    @field:SerializedName("priceType")
    val priceType: String? = null,

    @field:SerializedName("services")
    val services: ArrayList<ServicesItem?> = ArrayList(),

    @field:SerializedName("terminateDate")
    val terminateDate: Any? = null,

    @field:SerializedName("ratingCount")
    val ratingCount: Int? = null,

    @field:SerializedName("acceptedDate")
    val acceptedDate: String? = null,

    @field:SerializedName("completedDate")
    val completedDate: Any? = null,

    @field:SerializedName("sentBy")
    val sentBy: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("sentTo")
    val sentTo: SentTo? = null,

    @field:SerializedName("rejectDate")
    val rejectDate: Any? = null,

    @field:SerializedName("bookingType")
    val bookingType: String? = null,

    @field:SerializedName("__v")
    val V: Int? = null,

    @field:SerializedName("location")
    val location: Location? = null,

    @field:SerializedName("avgRating")
    val avgRating: Float? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("startDate")
    val startDate: Any? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("desc")
    val desc: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,

    @field:SerializedName("orderId")
    val orderId: String? = "",

    @field:SerializedName("isUserCompleted")
    var isUserCompleted: Boolean? = null,

    @field:SerializedName("tax")
    val tax: Double? = 0.0

): Serializable