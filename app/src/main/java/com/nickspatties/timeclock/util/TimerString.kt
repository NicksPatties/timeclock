package com.nickspatties.timeclock.util

import com.nickspatties.timeclock.data.TimeClockEvent
import java.util.*

fun convertMillisToHoursMinutesSeconds(millis: Long): Triple<Int, Int, Int> {
    val seconds = millis / 1000
    return convertSecondsToHoursMinutesSeconds(seconds.toInt())
}

fun convertSecondsToHoursMinutesSeconds(seconds: Int): Triple<Int, Int, Int> {
    var remainingSeconds = seconds

    val hours = remainingSeconds / 3600
    remainingSeconds %= 3600

    val minutes = remainingSeconds / 60
    remainingSeconds %= 60

    return Triple(hours, minutes, remainingSeconds)
}

fun decorateMillisLikeStopwatch(millis: Long) : String {
    val (hours, minutes, seconds) = convertMillisToHoursMinutesSeconds(millis)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun decorateMillisWithDecimalHours(millis: Long) : String {
    val hms = convertMillisToHoursMinutesSeconds(millis)
    val hours = hms.first
    val minutes = hms.second
    val seconds = hms.third

    // get fraction for seconds to minutes
    val decimalSeconds = seconds / 60f

    // get fraction of minutes to hours
    val decimalMinutes = (minutes + decimalSeconds ) / 60f

    val decimalHours = hours + decimalMinutes

    val precision = 2

    return "%.${precision}f".format(decimalHours)
}

fun decorateMillisWithWholeHoursAndMinutes(millis: Long) : String {
    val hms = convertMillisToHoursMinutesSeconds(millis)
    val hours = hms.first
    val minutes = hms.second
    val seconds = hms.third

    val roundedMinute = if (seconds >= 30) 1 else 0
    val addedMinutes = minutes + roundedMinute
    return "%d hours %d minutes".format(hours, addedMinutes)
}

fun getTimerString(currSeconds: Int) : String {
    val (hours, minutes, seconds) = convertSecondsToHoursMinutesSeconds(currSeconds)

    // decorate string
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun calculateCurrSeconds(
    event: TimeClockEvent?,
    currentTimeMillis: Long = System.currentTimeMillis()
): Int {
    if (event == null) return 0
    return ((currentTimeMillis - event.startTime) / 1000).toInt()
}

fun decorateMillisToDateString(millis: Long) : String {
    // set calendar object that will contain day, month, and year information
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    val locale = Locale.getDefault()
    val monthString = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, locale)
    val dayString = cal.get(Calendar.DAY_OF_MONTH)
    val yearString = cal.get(Calendar.YEAR)
    return "%s %d, %d".format(monthString, dayString, yearString)
}