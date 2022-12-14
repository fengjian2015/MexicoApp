package com.fly.cashhill.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.fly.cashhill.MyApplication

import java.lang.ref.WeakReference


object ActivityManager {
    private var sCurrentActivityWeakRef: WeakReference<Activity>? = null
    private var sCloseActivityWeakRefs = ArrayList<WeakReference<Activity>?>()


    fun addCloseActivity(activity: Activity){
        sCloseActivityWeakRefs.add(WeakReference(activity))
    }

    fun finishCloseActivity(){
        for ( weakReference in sCloseActivityWeakRefs){
            if (weakReference?.get() != null){
                weakReference.get()!!.finish()
            }
        }
        sCloseActivityWeakRefs.clear()
    }

    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        val let = sCurrentActivityWeakRef?.let {
            currentActivity = it.get()!!
        }
        return currentActivity
    }

    fun registerActivityLifecycleCallbacks(){
        MyApplication.application.registerActivityLifecycleCallbacks(object:Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            }

            override fun onActivityStarted(p0: Activity) {
            }

            override fun onActivityResumed(p0: Activity) {
                sCurrentActivityWeakRef = WeakReference(p0)
            }

            override fun onActivityPaused(p0: Activity) {
            }

            override fun onActivityStopped(p0: Activity) {
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityDestroyed(p0: Activity) {
            }

        })
    }

}