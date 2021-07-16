package com.geniecustomer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.geniecustomer.api.ApiResponse
import com.geniecustomer.api.Repository
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UpdateprofileViewModel(var repository: Repository) : ViewModel() {

    var disposable = CompositeDisposable()
    val responseLiveData = MutableLiveData<ApiResponse>()

    fun observeEditProfile(): MutableLiveData<ApiResponse> {
        return responseLiveData
    }


    fun hitEditProfile(token : String, paramsMap: HashMap<String, RequestBody>) {
        disposable.add(repository.updateProfile(token,paramsMap)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun hitEditProfileWithImage(token : String, paramsMap: HashMap<String, RequestBody>, image: MultipartBody.Part) {
        disposable.add(repository.updateProfileWithImage(token,paramsMap,image)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun hitChangePassword(token : String, jsonObject: JsonObject) {
        disposable.add(repository.changePassword(token,jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun hitResetPassword(token : String, jsonObject: JsonObject) {
        disposable.add(repository.resetPassword(token,jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { d -> responseLiveData.setValue(ApiResponse.loading()) }
            .subscribe(
                { result -> responseLiveData.setValue(ApiResponse.success(result)) },
                { throwable -> responseLiveData.setValue(ApiResponse.error(throwable)) }
            ))
    }
    fun hitForgotPassword(jsonObject: JsonObject) {
        disposable.add(repository.forgotPassword(jsonObject)
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