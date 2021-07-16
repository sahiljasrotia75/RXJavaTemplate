package com.geniecustomer.model

import java.io.Serializable

data class TaskObject(
    var id: String = "",
    var name: String = "",
    var desc: String = "",
    var rate: Double = 0.0,
    var priceType: String = "",
    var rateType: String = "",
    var timeSlot: String = "",
    var date: String = ""
):Serializable