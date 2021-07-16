package com.geniecustomer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Repository
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignupViewModel(var repository: Repository) : ViewModel() {

    lateinit var disposable : Disposable
    val responseLiveData = MutableLiveData<ApiResponse>()

    fun signupResponse(): MutableLiveData<ApiResponse> {
        return responseLiveData
    }


    fun hitRegisterUser(request: JsonObject) {
        disposable = repository.executeRegisterUser(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            )
    }

    override fun onCleared() {
        try {
            disposable.dispose()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}