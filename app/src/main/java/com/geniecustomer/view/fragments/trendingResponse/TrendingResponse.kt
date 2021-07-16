package com.geniecustomer.view.fragments.trendingResponse

data class TrendingResponse(
    var count: Int? = null,
    var `data`: List<Data>? = null,
    var message: String? = null,
    var success: Boolean? = null
)