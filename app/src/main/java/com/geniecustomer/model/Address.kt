package com.geniecustomer.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Address(

	@field:SerializedName("address")
	var address: String? = null,

	@field:SerializedName("latitude")
	var latitude: Double? = null,

	@field:SerializedName("longitude")
	var longitude: Double? = null
):Serializable