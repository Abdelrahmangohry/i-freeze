package com.app.data.remote


object Constants {
    object PrefKeys {
        var BASE_URL = ""
        var APP_KEY = ""
        var THEME = "THEME"
        var LANG = "LANG"
        const val TOKEN = "TOKEN"
        const val USER: String = "user"
    }


    object ERROR_API {
        //deleted or unauthorized 401
        const val UNAUTHRIZED = "unAuthroized"

        //500
        const val SERVER_ERROR = "serverError"

        //400
        const val BAD_REQUEST = "bad_request"

        //404
        const val NOT_FOUND = "not_found"

        //no internet connection
        const val CONNECTION_ERROR = "connection_error"

    }

}