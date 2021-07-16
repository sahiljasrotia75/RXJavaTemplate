package com.geniecustomer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Repository
import com.geniecustomer.model.otp.OtpRequest
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OtpViewModel(var repository: Repository) : ViewModel() {

    var disposable = CompositeDisposable()
    val responseLiveData = MutableLiveData<ApiResponse>()

    fun otpResponse(): MutableLiveData<ApiResponse> {
        return responseLiveData
    }


    fun hitVerifyOtp(request: OtpRequest) {
        disposable.add(repository.getOtp(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun hitResendOtp(request: JsonObject) {
        disposable.add(repository.getResendOtp(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

    override fun onCleared() {
        try {
            disposable.clear()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}