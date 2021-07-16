package com.geniecustomer.model.edit_profile

import com.geniecustomer.model.signin.Data
import com.google.gson.annotations.SerializedName

data class EditProfileResponse(

    @field:SerializedName("data")
    val data: Data? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)