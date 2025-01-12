package com.shujushuo.tracking.sdk

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner

object LifecycleObserver : DefaultLifecycleObserver {

    private var registered = false

    fun register() {
        if (registered) return
        registered = true
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun unregister() {
        if (registered)
            ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        TrackingSdk.trackStartup(30)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        TrackingSdk.log("onStop")
        TrackingSdk.flushAllEvent()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        TrackingSdk.log("onPause")
        TrackingSdk.flushAllEvent()

    }
}