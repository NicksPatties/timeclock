package com.nickspatties.timeclock.ui.viewmodel

import com.nickspatties.timeclock.util.generateColorFromString

class AnalysisRow(val name: String, val millis: Long, val id: Long) {
    val color = generateColorFromString(name)

    /**
     * Gets the percentage of time that an AnalysisRow represents with a given
     * amount of total milliseconds.
     *
     * @param totalMillis the total number of milliseconds that this row's millis value divides into
     * @return a percentage, or 0f if totalMillis is less than or equal to 0
     */
    fun getPercentage(totalMillis: Long): Float {
        if(totalMillis <= 0L) return 0f
        return millis / totalMillis.toFloat() * 100f
    }
}