package com.geniecustomer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Repository
import com.geniecustomer.model.service_providers.ServiceProvidersListRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FliterDataViewModel(var repository: Repository) : ViewModel() {

    val disposable = CompositeDisposable()
    val responseLiveData = MutableLiveData<ApiResponse>()

    fun observeDataResponse(): MutableLiveData<ApiResponse> {
        return responseLiveData
    }


    fun hitGetDashboarddata(token: String) {
        disposable.add(repository.getDashboardData(token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }

    fun hitGetProvidersList(token: String,providersListRequest: ServiceProvidersListRequest) {
        disposable.add(repository.getProvidersList(token,providersListRequest)
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