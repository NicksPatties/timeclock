package com.nickspatties.timeclock

import android.app.Service
import android.content.Intent
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import android.widget.Toast

/**
 * Things to do
 *  1. create the HandlerThread that will run. This thread's looper and ServiceHandler will be assigned to the class's looper and servicehandler vars
 *    * is onCreate also in charge of defining what will be done when a tick occurs (like the chronometer callback function?)
 *  2. onStartCommand will
 */

class ClockService : Service() {

    private var taskName: String? = null
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private var count = 0
    private val mDefaultDelay = 1000L
    private var mDelay = mDefaultDelay
    private var mTickFrequency = mDefaultDelay
    private var mIsRunning = false

    // Handler that receives messages from the thread
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                doAThing()
            } catch (e: InterruptedException) {
                // Restore interrupt status.
                Thread.currentThread().interrupt()
            }
        }
    }

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND).apply {
            start()
            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
            mIsRunning = true
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        taskName = intent.getStringExtra("eventName")
        Toast.makeText(this, "$taskName starting", Toast.LENGTH_SHORT).show()

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            serviceHandler?.sendMessage(msg)
        }

        serviceHandler?.postDelayed(mTickRunnable, 0L)

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        serviceHandler?.removeCallbacks(mTickRunnable)
        mIsRunning = false
        Toast.makeText(this, "$taskName service done", Toast.LENGTH_SHORT).show()
    }

    fun doAThing() {
        Log.i("ClockService", "Doing a thing $count");
        count++
    }

    private val mTickRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mIsRunning) {
                doAThing()
                serviceHandler?.postDelayed(this, mTickFrequency)
            }
        }
    }
}