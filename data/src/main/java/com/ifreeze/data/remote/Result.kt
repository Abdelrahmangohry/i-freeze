package com.app.data.remote

//Generic class
sealed class NetWorkState {
    data class Success(val data: String?) : NetWorkState()
    data class Error(val th: Throwable) : NetWorkState()
    object Loading : NetWorkState()
    object StopLoading: NetWorkState()

}











