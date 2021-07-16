package com.geniecustomer.model

import java.util.*

data class TimeModel(
    val start_time: Date,
    val end_time: Date,
    val position: Int,
    val id: String,
    val time_slot: String,
    var isSelected: Boolean = false
) : Comparable<TimeModel> {
    override fun compareTo(other: TimeModel): Int {
        return start_time.compareTo(other.start_time)
    }

}