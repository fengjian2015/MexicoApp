package com.fly.cashhill.network

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors

object NetworkScheduler {
    var scheduler: Scheduler? = null
    @JvmStatic
    fun <T> compose(): ObservableTransformer<T, T> {
        if(scheduler == null){
            scheduler =  Schedulers.from(Executors.newFixedThreadPool(10))
        }

        return ObservableTransformer { observable ->
            observable.subscribeOn(scheduler)
                    .unsubscribeOn(scheduler)
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}