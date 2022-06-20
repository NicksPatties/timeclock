package com.nickspatties.timeclock

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.widget.Toast

/**
 * A foreground service that displays a notification saying which event is currently being
 * tracked in the background. Currently only keeps track when the service starts, and when
 * the service ends.
 *
 * @see com.nickspatties.timeclock.ui.viewmodel.ClockPageViewModel
 */
class ClockService : Service() {

    private var taskName: String? = null
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    // currently doesn't do anything special when handling messages that are sent to the thread,
    // so no overrides just yet.
    private inner class ServiceHandler(looper: Looper) : Handler(looper)

    override fun onCreate() {
        HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND).apply {
            start()
            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        taskName = intent.getStringExtra("eventName")
        Toast.makeText(this, "$taskName starting", Toast.LENGTH_SHORT).show()

        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        serviceLooper?.quit()
        Toast.makeText(this, "$taskName service done", Toast.LENGTH_SHORT).show()
    }

    // removed the function used to do something during the correct interval

    // removed the runnable that's used to do something every second
}