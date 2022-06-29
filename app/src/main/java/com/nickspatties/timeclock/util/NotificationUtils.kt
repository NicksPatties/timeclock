package com.nickspatties.timeclock.util

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat

fun getNotificationManager(context: Context) : NotificationManager {
    return ContextCompat.getSystemService(
        context,
        NotificationManager::class.java
    ) as NotificationManager
}