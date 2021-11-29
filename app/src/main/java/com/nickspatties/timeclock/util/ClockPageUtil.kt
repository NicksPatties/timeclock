package com.nickspatties.timeclock.util

/**
 * Finds the amount of time in milliseconds that the first tick of the clock should
 * take place based on an event's start time, and the clock's tick frequency. This
 * is used to have the clock tick more accurately when returning to the clock page.
 */
fun findEventStartTimeDelay(
    startTime: Long,
    tickFrequency: Long = 1000L,
    currentTime: Long = System.currentTimeMillis()
): Long {
    val totalEventTime = currentTime - startTime
    return tickFrequency - totalEventTime % tickFrequency
}
