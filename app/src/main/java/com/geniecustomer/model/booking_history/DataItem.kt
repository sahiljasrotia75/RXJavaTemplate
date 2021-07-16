
package com.geniecustomer.model.booking

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("ongoing")
	val ongoing: List<Data>? = null,

	@field:SerializedName("pastOrder")
	val pastOrder: List<Data>? = null
)