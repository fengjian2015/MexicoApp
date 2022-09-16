package com.fly.cashhill.network.bean

import com.fly.cashhill.MyApplication
import com.fly.cashhill.R
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

open abstract class HttpResponse<T : Any> : Observer<T> {
    abstract fun businessFail(statusCode: Int, httpErrorBean: HttpErrorBean)
    abstract fun businessSuccess(data: T)

    override fun onNext(t: T) {
        if (t == null) {
            val apiErrorType =
                HttpErrorBean(-1, MyApplication.application.getString(R.string.http_network_error))
            businessFail(apiErrorType.status, apiErrorType)
            return
        }
        businessSuccess(t)
    }

    override fun onError(e: Throwable) {
        val apiErrorType =
            HttpErrorBean(-1, MyApplication.application.getString(R.string.http_network_error))
        businessFail(apiErrorType.status, apiErrorType)
    }

    override fun onComplete() {

    }

    override fun onSubscribe(d: Disposable) {

    }
}