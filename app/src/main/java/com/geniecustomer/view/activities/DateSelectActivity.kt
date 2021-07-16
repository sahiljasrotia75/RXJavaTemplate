package com.geniecustomer.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import com.geniecustomer.R
import com.geniecustomer.base.BaseActivity
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import kotlinx.android.synthetic.main.activity_date_select.*
import java.text.SimpleDateFormat
import java.util.*

class DateSelectActivity : BaseActivity() {

    var selectedDate : Date? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_select)


        val currentMonthDat = compactcalendar_view.firstDayOfCurrentMonth
        val yearFormat = SimpleDateFormat(" MMM-yyyy")
        txtDateTime.text = yearFormat.format(currentMonthDat)
        compactcalendar_view.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                Log.e("SELECTED", " " + dateClicked)
                var yearFormat = SimpleDateFormat("yyyy")
                var dateFormat = SimpleDateFormat("EEE MMM dd")
                var year = yearFormat.format(dateClicked!!)
                var allDate = dateFormat.format(dateClicked)
                Log.e("SELECTED", " year " + year)
                Log.e("SELECTED", " allDate " + allDate)
                selectedDate = dateClicked
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                Log.e("DATE", "Month was scrolled to: " + firstDayOfNewMonth)
                val yearFormat = SimpleDateFormat(" MMM-yyyy")
                txtDateTime.text = yearFormat.format(firstDayOfNewMonth)
            }

        })
    }

    fun backClick(v : View){
        when(v.id){
            R.id.txtNextClick->{

                compactcalendar_view.scrollRight()
            }
            R.id.txtPreviousClick->{
                compactcalendar_view.scrollLeft()
            }
            R.id.ivback->{
                finish()
            }
            R.id.txtSelectClick -> {
                if (selectedDate != null) {
                    val selectedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    selectedDate.format(selectedDate)
                    Log.e("DATE", "Selected: " + selectedDate.format(selectedDate))
                    //finish()
                }

            }
        }
    }
}
