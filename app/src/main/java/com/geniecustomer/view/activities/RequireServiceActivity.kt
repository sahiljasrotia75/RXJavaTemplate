package com.geniecustomer.view.activities

import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.geniecustomer.R
import com.geniecustomer.adapters.LaterDatesAdapter
import com.geniecustomer.adapters.LaterTimeAdapter
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Status
import com.geniecustomer.base.BaseActivity
import com.geniecustomer.base.MyApp
import com.geniecustomer.interfaces.RvClickPostion
import com.geniecustomer.model.*
import com.geniecustomer.model.service_providers.info.TimeSlotListItem
import com.geniecustomer.viewmodels.BookingViewModel
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_require_service.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RequireServiceActivity : BaseActivity() {

    companion object {
        var newAddress: Place? = null
    }

    var date_adapter: LaterDatesAdapter? = null
    var time_adapter: LaterTimeAdapter? = null
    var review_detail_model: ReviewDetailModel? = null
    var date_list: ArrayList<DateModel> = ArrayList()

    //var time_list: ArrayList<String> = ArrayList()
    var latlng: LatLng? = null
    var selected_pos: ArrayList<Int>? = ArrayList()
    var selectedtimeSlots: ArrayList<String> = ArrayList()
    var time_selected_pos: MutableList<TimeModel> = ArrayList()
    var selected_times: MutableList<String> = ArrayList()
    private var timeSlotsList: List<TimeSlotListItem?>? = null
    var dateFormatter = SimpleDateFormat("HH:mm")
    val yearFormat = SimpleDateFormat("MMM-yyyy")
    val formatDate = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var events: List<Event>

    //  val formatDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    var consecutive = true
    var selectedDate = ""
    val model by viewModel<BookingViewModel>()
    var typeOfBooking = 1
    var previousDay = ""
    var currentDay = ""
    var providerCity = ""
    var currentUserCity = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_require_service)

        Log.e("BADSHAH", "Provider City: $providerCity")
        selected_pos?.add(0)

        providerCity = intent.getStringExtra("providerCity")!!

        val currentDate = Calendar.getInstance().time
        selectedDate = formatDate.format(currentDate)
        tvSelectedDate.text = yearFormat.format(currentDate)

        time_selected_pos = ArrayList()
        date_adapter = LaterDatesAdapter(this, date_list, selected_pos)
        time_adapter = LaterTimeAdapter(this, time_selected_pos as ArrayList<TimeModel>)
        date_recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        time_recycler_view.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        date_recycler_view.adapter = date_adapter
        time_recycler_view.adapter = time_adapter

        model.bookingResponse()
            .observe(this, Observer<ApiResponse> { this.consumeResponse(it!!, 1) })
        if (intent != null && intent.hasExtra("booking_id")) {
            model.getTimeSlot(user_obj?.token ?: "", intent.getStringExtra("booking_id")!!)
            review_detail_model = intent.getSerializableExtra("review_model") as ReviewDetailModel
            typeOfBooking = 2
            if (review_detail_model?.bookingType.equals("Schedule", true)) {
                tv_select_date.visibility = View.VISIBLE
                date_recycler_view.visibility = View.GONE
                compactcalendar_view.visibility = View.VISIBLE
                tv_select_time.visibility = View.VISIBLE
                time_recycler_view.visibility = View.VISIBLE
                tvSelectedDate.visibility = View.VISIBLE
                line1.visibility = View.VISIBLE
                line2.visibility = View.VISIBLE
            }
        } else if (intent != null && intent.hasExtra("review_model")) {
            review_detail_model = intent.getSerializableExtra("review_model") as ReviewDetailModel
            typeOfBooking = 1
            if (review_detail_model?.bookingType.equals("Schedule", true)) {
                tv_select_date.visibility = View.VISIBLE
                date_recycler_view.visibility = View.GONE
                compactcalendar_view.visibility = View.VISIBLE
                tv_select_time.visibility = View.VISIBLE
                time_recycler_view.visibility = View.VISIBLE
                tvSelectedDate.visibility = View.VISIBLE
                line1.visibility = View.VISIBLE
                line2.visibility = View.VISIBLE
            }
            timeSlotsList = intent.getParcelableArrayListExtra("time_slots")

            addDateSlotsToCalendar()
            setDateFormat(selectedDate)
        }


        compactcalendar_view.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                // time_selected_pos.clear()
                //txtDateTime.setText(formatDate.format(dateClicked))
                val monthFormat = SimpleDateFormat("MM")
                val dayFormat = SimpleDateFormat("dd")
                val formatofYear = SimpleDateFormat("yyyy")

                val currentMonth = monthFormat.format(currentDate)
                currentDay = dayFormat.format(currentDate)
                val currentYear = formatofYear.format(currentDate)

                val month = monthFormat.format(dateClicked)
                previousDay = dayFormat.format(dateClicked)
                val previousYear = formatofYear.format(dateClicked)


                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, -1)
                if (dateClicked?.before(calendar.time)!!) {
                    showToast("You can't select previous dates")
                    return
                } else {
                    selectedDate = formatDate.format(dateClicked)
                    setDateFormat(selectedDate)
                    tvSelectedDate.text = yearFormat.format(dateClicked)
                }

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                tvSelectedDate.text = yearFormat.format(firstDayOfNewMonth)
            }

        })



        time_adapter?.setOnClick(object : RvClickPostion {
            override fun onRvItemClicked(position: Int) {
                time_selected_pos.get(position).isSelected =
                    !time_selected_pos.get(position).isSelected
                time_adapter?.notifyDataSetChanged()
            }

        })

        if (sharedPref.contains("address")) {
            val address = MyApp.gson.fromJson(
                sharedPref.getString("address", ""),
                Address::class.java
            )
            Log.e("LocationFetch", "${address}")
            tv_location_value.text = address.address
            currentUserCity = address.address!!
            latlng = LatLng(address.latitude ?: 0.0, address.longitude ?: 0.0)
        }
    }

    private fun addDateSlotsToCalendar() {
        try {
            if (review_detail_model?.bookingType.equals("Schedule", true)) {
                if (timeSlotsList!!.isNotEmpty()) {
                    for (item in timeSlotsList!!) {
                        val timeInMillis = getMilliFromDate(item?.date)
                        val ev1 = Event(Color.GREEN, timeInMillis)
                        events = compactcalendar_view!!.getEvents(timeInMillis)
                        for (ii in events.indices) {
                            if (events[ii] == ev1) {
                                compactcalendar_view?.removeEvent(ev1)
                            }
                        }
                        compactcalendar_view.addEvent(ev1)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDateFormat(selectedDate: String) {

        Log.e("DATE", " selectedDate " + selectedDate)
        time_selected_pos.clear()

        for (i in 0 until timeSlotsList?.size!!) {

            Log.e("dmclsdmlc", "scsada 111:-  ${timeSlotsList?.get(i).toString()}")
            Log.e("dmclsdmlc", "scsada :-  ${timeSlotsList?.get(i)?.date.toString()}")


            if (timeSlotsList?.get(i)?.date != null) {

                val ss = formatDate.parse(timeSlotsList?.get(i)?.date!!)

                if (formatDate.format(ss).toString().equals(selectedDate)) {


                    val current_time = dateFormatter.parse(getCurrentTime())
                    val slot_start_time =
                        dateFormatter.parse(timeSlotsList?.get(i)?.startTime!!)
                    val slot_end_time =
                        dateFormatter.parse(timeSlotsList?.get(i)?.endTime!!)
                    val time_slot =
                        timeSlotsList?.get(i)?.startTime + " - " + timeSlotsList?.get(
                            i
                        )?.endTime

                    if (previousDay > currentDay) {
                        val time_slot =
                            timeSlotsList?.get(i)?.startTime + " - " + timeSlotsList?.get(
                                i
                            )?.endTime
                        Log.e("datetime", "" + time_slot)
                        time_selected_pos.add(
                            TimeModel(
                                slot_start_time,
                                slot_end_time,
                                i,
                                timeSlotsList?.get(i)?.id ?: "",
                                time_slot,
                                false
                            )
                        )
                        Collections.sort(time_selected_pos)

                    } else {
                        if (slot_start_time?.after(current_time) == true) {

                            Log.e("GetIfCondition", "$current_time")

                            val time_slot =
                                timeSlotsList?.get(i)?.startTime + " - " + timeSlotsList?.get(
                                    i
                                )?.endTime
                            Log.e("datetime", "" + time_slot)
                            time_selected_pos.add(
                                TimeModel(
                                    slot_start_time,
                                    slot_end_time,
                                    i,
                                    timeSlotsList?.get(i)?.id ?: "",
                                    time_slot,
                                    false
                                )
                            )
                            Collections.sort(time_selected_pos)
                        } else {
                            continue
                        }


                    }


                    /*  for (j in 0 until timeSlotsList?.get(i)?.list?.size!!) {

                          val current_time = dateFormatter.parse(getCurrentTime())
                          val slot_start_time =
                              dateFormatter.parse(timeSlotsList?.get(i)?.list!!.get(j)?.startTime!!)
                          val slot_end_time =
                              dateFormatter.parse(timeSlotsList?.get(i)?.list!!.get(j)?.endTime!!)
                          val time_slot =
                              timeSlotsList?.get(i)?.list!!.get(j)?.startTime + " - " + timeSlotsList?.get(
                                  i
                              )?.list!!.get(j)?.endTime

                          if (previousDay > currentDay) {
                              val time_slot =
                                  timeSlotsList?.get(i)?.list!!.get(j)?.startTime + " - " + timeSlotsList?.get(
                                      i
                                  )?.list!!.get(j)?.endTime
                              Log.e("datetime", "" + time_slot)
                              time_selected_pos.add(
                                  TimeModel(
                                      slot_start_time,
                                      slot_end_time,
                                      i,
                                      timeSlotsList?.get(i)?.list!!.get(j)?.id ?: "",
                                      time_slot,
                                      false
                                  )
                              )
                              Collections.sort(time_selected_pos)

                          } else {
                              if (slot_start_time?.after(current_time) == true) {

                                  Log.e("GetIfCondition", "$current_time")

                                  val time_slot =
                                      timeSlotsList?.get(i)?.list!!.get(j)?.startTime + " - " + timeSlotsList?.get(
                                          i
                                      )?.list!!.get(j)?.endTime
                                  Log.e("datetime", "" + time_slot)
                                  time_selected_pos.add(
                                      TimeModel(
                                          slot_start_time,
                                          slot_end_time,
                                          i,
                                          timeSlotsList?.get(i)?.list!!.get(j)?.id ?: "",
                                          time_slot,
                                          false
                                      )
                                  )
                                  Collections.sort(time_selected_pos)
                              } else {
                                  continue
                              }


                          }

                      }*/
                }

            }

        }


        Log.e("almkamxl", Gson().toJson(timeSlotsList?.size))
        Log.e("almkamxl", Gson().toJson(time_selected_pos.size))
        time_adapter?.notifyDataSetChanged()
    }

    private fun getCurrentTime(): String {
        val delegate = "HH:mm"
        return SimpleDateFormat(delegate).format(Calendar.getInstance().time)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_yourLoc -> {
                if (tv_location_value.text.toString().isEmpty()) {
                    showToast("Please select location")
                } else if (tv_desc_value.text.toString().isEmpty()) {
                    showToast("Please add description")
                } else {
//                    if (currentUserCity.contains(providerCity)){
                    if (review_detail_model?.bookingType.equals("Schedule", true)) {
                        var previous: Date? = null
                        consecutive = true
                        selected_times.clear()
                        selectedtimeSlots.clear()
                        //Log.e("Selected_times", "" + time_selected_pos)

                        //Log.e("Sorted_time_slots", "" + time_selected_pos)
                        for (i in 0 until time_selected_pos.size) {
                            if (time_selected_pos.get(i).isSelected) {
                                if (previous == null)
                                    previous = time_selected_pos.get(i).end_time
                                else
                                    if (previous.compareTo(time_selected_pos.get(i).start_time) != 0) {
                                        Log.e("not consecitive", "ttrue")
                                        consecutive = false
                                        break
                                    } else
                                        previous = time_selected_pos.get(i).end_time
                                selected_times.add(time_selected_pos.get(i).id)

                                selectedtimeSlots.add(time_selected_pos[i].time_slot)
                            }

                        }


                        if (consecutive) {
                            if (review_detail_model?.task_list?.size != selected_times.size)
                                showToast("Number of selected time slots must be equal to number of services selected.")
                            else {
                                if (user_obj != null) {
                                    review_detail_model?.fullName =
                                        user_obj?.firstName ?: "" + " " + user_obj?.lastName ?: ""
                                    review_detail_model?.phone = user_obj?.phone ?: ""
                                }

                                for (i in selectedtimeSlots.indices) {
                                    review_detail_model?.task_list?.get(i)?.timeSlot =
                                        selectedtimeSlots[i]
                                    review_detail_model?.task_list?.get(i)?.date = selectedDate
                                }

                                review_detail_model?.address = tv_location_value.text.toString()
                                review_detail_model?.address = currentUserCity
                                review_detail_model?.task_details = tv_desc_value.text.toString()
                                review_detail_model?.scheduleTime = selected_times
                                startActivity(
                                    Intent(this, ConfirmBookingActivity::class.java)
                                        .putExtra("typeOfBooking", typeOfBooking)
                                        .putExtra("review_model", review_detail_model)
                                )
                            }
                        } else {
                            showToast("Please select consecutive time slots.")
                        }

//                        val it = mp.entrySet().iterator()
//                        while (it.hasNext()) {
//                            val pair = it.next() as Map.Entry<*, *>
//                            println(pair.key + " = " + pair.value)
//                            it.remove() // avoids a ConcurrentModificationException
//                        }

                    } else {

                        if (user_obj != null) {
                            review_detail_model?.fullName =
                                user_obj?.firstName ?: "" + " " + user_obj?.lastName ?: ""
                            review_detail_model?.phone = user_obj?.phone ?: ""
                        }
                        review_detail_model?.address = tv_location_value.text.toString()
                        review_detail_model?.address = currentUserCity
                        review_detail_model?.task_details = tv_desc_value.text.toString()
                        startActivity(
                            Intent(
                                this,
                                ConfirmBookingActivity::class.java
                            ).putExtra("review_model", review_detail_model)
                                .putExtra("typeOfBooking", typeOfBooking)
                        )
                    }
//                }else{
//                        Toast.makeText(context,"Your location is not under the service area for this provider.",Toast.LENGTH_LONG).show()
//                    }
                }
            }
            R.id.iv_back -> {
                hideKeyboard()
                finish()
            }
            R.id.tv_location_value -> {
                Places.initialize(this, getString(R.string.places_api_key))
                val placesClient = Places.createClient(this)
                val fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS_COMPONENTS
                )
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this)
                startActivityForResult(intent, LOCATIONPERMISSIONCODE)

            }
        }
    }

    override fun getRequestCode(requestcode: Int, data: Intent?) {
        super.getRequestCode(requestcode, data)
        when (requestcode) {
            LOCATIONPERMISSIONCODE -> {
                hideKeyboard()
                val place = Autocomplete.getPlaceFromIntent(data!!)
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(
                        place.latLng?.latitude ?: 0.0,
                        place.latLng?.longitude ?: 0.0,
                        1
                    )
                val cityName = addresses[0].locality

                newAddress = place

                Log.i(
                    "call",
                    "Place: " + place.name + ", " + place.id + " , " + place.latLng + " , " + place.addressComponents
                )
                Log.e("AddressGet", "${place.address}")
//                tv_location_value.text = place.name
                tv_location_value.text = place.address
//                currentUserCity = place.name!!
                currentUserCity = place.address!!
                latlng = LatLng(place.latLng?.latitude ?: 0.0, place.latLng?.longitude ?: 0.0)
            }
        }
    }

    private fun consumeResponse(apiResponse: ApiResponse, type: Int) {

        when (apiResponse.status) {

            Status.LOADING -> showProgress()

            Status.SUCCESS -> {
                hideProgress()
                try {
                    renderSuccessResponse(apiResponse.data!!, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Status.ERROR -> {
                hideProgress()
                Log.e("FAILURE", apiResponse.error?.message ?: "")
                showSnackBar(apiResponse.error?.message ?: "")
            }

            else -> {
            }
        }
    }

    private fun renderSuccessResponse(response: JsonElement, type: Int) {
        Log.e("kmncsdkmckdsm", "cmldsml     $response")
        if (!response.isJsonNull) {
            val data: String = MyApp.gson.toJson(response)
            val model = MyApp.gson.fromJson(data, TimeSlotsResponse::class.java)
            timeSlotsList = model.data!!
            setDateFormat(selectedDate)
            if (review_detail_model?.task_details!!.isNotEmpty()) {
                tv_desc_value.setText(review_detail_model?.task_details!!)
            }
        }
    }


}