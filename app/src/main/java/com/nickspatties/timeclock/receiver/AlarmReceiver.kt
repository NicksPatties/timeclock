package com.nickspatties.timeclock.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.nickspatties.timeclock.util.cancelClockInProgressNotification
import com.nickspatties.timeclock.util.sendTimerCompleteNotification

/**
 * Sends a notification to the user that will return them to the application when a countdown event
 * is complete.
 */
class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelClockInProgressNotification()
        notificationManager.sendTimerCompleteNotification(context)
    }
}