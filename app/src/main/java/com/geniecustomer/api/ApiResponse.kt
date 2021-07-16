package  com.geniecustomer.api

import com.google.gson.JsonElement

class ApiResponse(status: Status, data: JsonElement?, error: Throwable?) {
  val status: Status
  val data: JsonElement?
  val error: Throwable?

  companion object {
    fun loading(): ApiResponse {
      return ApiResponse(Status.LOADING, null, null)
    }

    fun success(data: JsonElement): ApiResponse {
      return ApiResponse(Status.SUCCESS, data, null)
    }

    fun error(error: Throwable): ApiResponse {
      return ApiResponse(Status.ERROR, null, error)
    }
  }

  init {
    this.status = status
    this.data = data
    this.error = error
  }
}