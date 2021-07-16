package com.geniecustomer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Repository
import com.geniecustomer.model.booking.BookingRequest
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody

class BookingViewModel(var repository: Repository) : ViewModel() {

    val disposable = CompositeDisposable()
    val responseLiveData = MutableLiveData<ApiResponse>()
    val logout = MutableLiveData<ApiResponse>()

    fun bookingResponse(): MutableLiveData<ApiResponse> {
        return responseLiveData
    }


    fun logout(): MutableLiveData<ApiResponse> {
        return logout
    }


    fun hitGetChatList(token: String, skip: Int, limit: Int) {
        disposable.add(repository.getChatHistory(token, skip, limit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

    fun getProfile(token: String) {
        disposable.add(repository.getProfile(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }


    fun getChat(token: String, id : String) {
        disposable.add(repository.getChat(token,id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

    fun getTimeSlot(token: String, id : String) {
        disposable.add(repository.getTimeSlot(token,id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

    fun hitDoBooking(token : String , request: BookingRequest) {
        disposable.add(repository.hitDoBooking(token,request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun postRating(token : String , request: JsonObject) {
        disposable.add(repository.postRating(token,request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun suggestion(token : String , request: JsonObject) {
        disposable.add(repository.suggestion(token,request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun search(request: JsonObject) {
        disposable.add(repository.search(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

    fun hitCancelBookingRequest(token : String ,jsonObject: RequestBody) {
        disposable.add(repository.hitCancelBookingRequest(token,jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

    fun historyBooking(token : String , request: JsonObject) {
        disposable.add(repository.historyBooking(token, request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }


    fun logoutService(token: String) {
        disposable.add(repository.logoutUser(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> logout.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> logout.setValue(ApiResponse.success(result)) },
                { throwable -> logout.setValue(ApiResponse.error(throwable)) }
            )
        )
    }


//    fun hitSocialSignin(request: SocialRequest) {
//        disposable.add(repository.executeSocialLogin(request)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
//            .subscribe(
//                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
//                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
//            ))
//    }


    override fun onCleared() {
        try {
            disposable.clear()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}