package com.nickspatties.timeclock.util

const val MILLIS_PER_SECOND = 1000L
const val SECONDS_PER_MINUTE = 60L
const val MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE
const val MINUTES_PER_HOUR = 60L
const val MILLIS_PER_HOUR = MILLIS_PER_MINUTE * MINUTES_PER_HOUR

fun convertHoursMinutesSecondsToMillis(hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Long {
    return MILLIS_PER_HOUR * hours + MILLIS_PER_MINUTE * minutes + MILLIS_PER_SECOND * seconds
}

fun convertHoursMinutesSecondsToSeconds(hours: Int = 0, minutes: Int = 0, seconds: Int = 0): Int {
    if (hours == 0 && minutes == 0) return seconds
    return hours * 60 * 60 + minutes * 60 + seconds
}

fun convertMillisToHoursMinutesSeconds(millis: Long): Triple<Int, Int, Int> {
    val seconds = millis / MILLIS_PER_SECOND
    return convertSecondsToHoursMinutesSeconds(seconds.toInt())
}

fun convertSecondsToHoursMinutesSeconds(seconds: Int): Triple<Int, Int, Int> {
    val secondsPerHour = SECONDS_PER_MINUTE * MINUTES_PER_HOUR
    var remainingSeconds = seconds

    val hours = remainingSeconds / secondsPerHour.toInt()
    remainingSeconds %= secondsPerHour.toInt()

    val minutes = remainingSeconds / SECONDS_PER_MINUTE.toInt()
    remainingSeconds %= SECONDS_PER_MINUTE.toInt()

    return Triple(hours, minutes, remainingSeconds)
}