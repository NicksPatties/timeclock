package com.nickspatties.timeclock.ui.viewmodel

import com.nickspatties.timeclock.util.generateColorFromString

class AnalysisRow(val name: String, val millis: Long, val id: Long) {
    val color = generateColorFromString(name)
    fun getPercentage(totalMillis: Long): Float {
        return millis / totalMillis.toFloat()
    }
}