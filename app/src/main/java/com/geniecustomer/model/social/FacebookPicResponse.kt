package com.geniecustomer.model.social

import com.google.gson.annotations.SerializedName

data class FacebookPicResponse(

	@field:SerializedName("data")
	val data: Data? = null
)