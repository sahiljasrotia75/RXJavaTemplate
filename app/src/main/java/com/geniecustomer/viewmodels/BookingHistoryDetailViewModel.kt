package com.geniecustomer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody

class BookingHistoryDetailViewModel(var repository: Repository) : ViewModel() {

    val disposable = CompositeDisposable()
    val responseLiveData = MutableLiveData<ApiResponse>()

    fun bookingResponse(): MutableLiveData<ApiResponse> {
        return responseLiveData
    }


    fun historyBookingDetail(token : String , id: String) {
        disposable.add(repository.historyBookingDetail(token,id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

    fun isUserComplete(token : String , id: String) {
        disposable.add(repository.isUserComplete(token,id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

  fun reScheduleBooking(token : String , obj: RequestBody) {
        disposable.add(repository.reScheduleBooking(token,obj)
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