package com.geniecustomer.model.edit_profile

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EditProfileRequest(

    @field:SerializedName("firstName")
    val firstName: String? = null,

    @field:SerializedName("lastName")
    val lastName: String? = null,

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("latitude")
    val latitude: Double? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("longitude")
    val longitude: Double? = null,

    @field:SerializedName("countryCode")
    val countryCode: String? = null

):Serializable