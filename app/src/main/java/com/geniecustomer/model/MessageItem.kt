package com.geniecustomer.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MessageItem(

    @field:SerializedName("receiver")
    val receiver: String? = null,

    @field:SerializedName("sender")
    val sender: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("bookingId")
    val bookingId: String? = null,

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("image")
    val image: String? = "",

    var bookingStatus : String = "",

    var type : String = ""
):Serializable