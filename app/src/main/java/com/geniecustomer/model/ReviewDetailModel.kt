package com.geniecustomer.model

import java.io.Serializable

data class ReviewDetailModel(
    var bookingId: String = "",
    var fullName : String = "",
    var phone : String = "",
    var address : String = "",
    var task_details : String = "",
    var provider_id : String = "",
    var provider_name : String = "",
    var provider_address :String = "",
    var provider_city :String = "",
    var provider_review_num : Int = 0,
    var provider_rating : Float = 0f,
    var task_list : List<TaskObject>?=null,
    var bookingType : String?=null,
    var scheduleTime : List<String>?=null,
    var price_type : String=""
):Serializable