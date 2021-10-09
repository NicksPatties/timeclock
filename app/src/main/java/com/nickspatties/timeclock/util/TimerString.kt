package com.nickspatties.timeclock.util

fun getTimerString(currSeconds: Int) : String {
    var remainingSeconds = currSeconds

    val hours = remainingSeconds / 3600
    remainingSeconds %= 3600

    val minutes = remainingSeconds / 60
    remainingSeconds %= 60

    // decorate string
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}