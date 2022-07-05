package com.nickspatties.timeclock.receiver

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.nickspatties.timeclock.MainActivity
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.util.CLOCK_IN_PROGRESS_NOTIFICATION_ID

/**
 * Sends a notification to the user that will return them to the application when a countdown event
 * is complete.
 */
class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        val mainIntent = Intent(context, MainActivity::class.java)
        val pendingMainIntent = PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val timerCompleteNotification = NotificationCompat.Builder(
            context,
            context.getString(R.string.alarm_channel_id)
        )
            .setSmallIcon(R.drawable.ic_baseline_clock_24)
            .setContentTitle("Event completed! Good work!")
            .setContentText("You have finished your task. Good job!")
            .setContentIntent(pendingMainIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val TIMER_COMPLETE_NOTIFICATION_ID = 1
        notificationManager.cancel(CLOCK_IN_PROGRESS_NOTIFICATION_ID)
        notificationManager.notify(
            TIMER_COMPLETE_NOTIFICATION_ID,
            timerCompleteNotification.build()
        )
    }
}