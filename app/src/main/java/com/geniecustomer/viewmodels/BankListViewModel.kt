package com.geniecustomer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class BankListViewModel(var repository: Repository) : ViewModel(){
    val disposables = CompositeDisposable()
    val responseLiveData = MutableLiveData<ApiResponse>()

    fun liveDataCommonResponse(): MutableLiveData<ApiResponse> {
        return responseLiveData
    }


    fun bankList(token:String){
        disposables.add(
            repository.bankList(token)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe { responseLiveData.setValue(ApiResponse.loading()) }
                ?.subscribe(
                    { result -> responseLiveData.setValue(ApiResponse.success(result!!)) },
                    { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
                )!!)
    }


}