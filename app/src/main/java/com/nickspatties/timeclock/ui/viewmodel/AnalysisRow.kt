package com.nickspatties.timeclock.ui.viewmodel

import com.nickspatties.timeclock.util.generateColorFromString

class AnalysisRow(val name: String, val millis: Long, val id: Long) {
    val color = generateColorFromString(name)

    /**
     * Gets the percentage of time that an AnalysisRow represents with a given
     * amount of total milliseconds.
     *
     * @param totalMillis the total number of milliseconds of all events
     * @return a percentage value between 0 and 100, or 0 if totalMillis is less than or equal to 0
     */
    fun getPercentage(totalMillis: Long): Float {
        if(totalMillis <= 0L) return 0f
        return millis / totalMillis.toFloat() * 100f
    }
}