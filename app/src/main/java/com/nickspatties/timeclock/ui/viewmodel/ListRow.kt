package com.nickspatties.timeclock.ui.viewmodel

import com.nickspatties.timeclock.util.decorateMillisLikeStopwatch
import com.nickspatties.timeclock.util.decorateMillisToTimeString
import com.nickspatties.timeclock.util.generateColorFromString

class ListRow(
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val id: Long
) {
    val isRunning = startTime == endTime
    val color = generateColorFromString(title)
    val startTimeString = decorateMillisToTimeString(startTime)
    val endTimeString = decorateMillisToTimeString(endTime)
    val elapsedTime = endTime - startTime
    val elapsedTimeString = decorateMillisLikeStopwatch(elapsedTime)
}