package com.nickspatties.timeclock.util

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

/**
 * Counts up from the current time by a specific delay until the timer is stopped. Used for running
 * timers and the like
 */
class Chronometer {

    // TODO: Should I allow this class to execute on a different thread?
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var mBase: Long = 0
    private var mRunning: Boolean = false
    private var mOnChronometerTickListener: () -> Unit = {}

    // TODO: create an interface for different listeners, then when you're creating the ViewModel
    // for this app, have the ViewModel implement the members of that interface
    fun setOnChronometerTickListener(function: () -> Unit) {
        mOnChronometerTickListener = function
    }

    fun start() {
        mBase = SystemClock.elapsedRealtime()
        mRunning = true
        mHandler.postDelayed(mTickRunnable, 1000)
    }

    fun stop() {
        mRunning = false
        mHandler.removeCallbacks(mTickRunnable)
    }

    private val mTickRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mRunning) {
                dispatchChronometerTick()
                mHandler.postDelayed(this, 1000)
            }
        }
    }

    fun dispatchChronometerTick() {
        mOnChronometerTickListener()
    }

}