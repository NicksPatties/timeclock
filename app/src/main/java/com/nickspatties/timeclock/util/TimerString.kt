package com.nickspatties.timeclock.util

import com.nickspatties.timeclock.data.TimeClockEvent
import java.util.*

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

fun decorateMillisToTimeString(millis: Long, timeZone: TimeZone = TimeZone.getDefault()) : String {
    val cal = Calendar.getInstance(timeZone)
    cal.timeInMillis = millis
    val amPmString = if (cal.get(Calendar.AM_PM) == 0) "AM" else "PM"
    val calHour = cal.get(Calendar.HOUR)
    val hours = if (calHour == 0) 12 else calHour
    val minutes = cal.get(Calendar.MINUTE)
    return "%d:%02d %s".format(hours, minutes, amPmString)
}