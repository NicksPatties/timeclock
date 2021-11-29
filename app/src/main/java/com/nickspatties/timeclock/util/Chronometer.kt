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
    private var mRunning: Boolean = false
    private var mOnChronometerTickListener: () -> Unit = {}
    private val mDefaultDelay = 1000L
    private var mDelay = mDefaultDelay
    private var mTickFrequency = mDefaultDelay

    /* TODO create an interface for different listeners, then when you're creating the ViewModel
     *  for this app, have the ViewModel implement the members of that interface. Stuff like:
     *    onChronometerStart
     *    onChronometerEnd
     */
    /**
     * Assigns the function that executes with each tick of the chronometer.
     *
     * @param function the code to execute
     */
    fun setOnChronometerTickListener(function: () -> Unit) {
        mOnChronometerTickListener = function
    }

    /**
     * Starts the chronometer, which executes the onChronometerTickListener after a given
     * delay, and repeats with a given frequency
     *
     * @param customDelay the amount of time in millis that the onChronometerTickListener should
     * execute for the first time
     * @param customTickFrequency the amount of time in millis between each subsequent execution
     * of the onChronometerTickListener
     */
    fun start(customDelay: Long = mDefaultDelay, customTickFrequency: Long = mDefaultDelay) {
        mRunning = true
        mDelay = customDelay
        mTickFrequency = customTickFrequency
        mHandler.postDelayed(mTickRunnable, mDelay)
    }

    /**
     * Stops the onChronometerTickListener from being executed anymore
     */
    fun stop() {
        mRunning = false
        mHandler.removeCallbacks(mTickRunnable)
    }

    private val mTickRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mRunning) {
                mOnChronometerTickListener()
                mHandler.postDelayed(this, mTickFrequency)
            }
        }
    }
}