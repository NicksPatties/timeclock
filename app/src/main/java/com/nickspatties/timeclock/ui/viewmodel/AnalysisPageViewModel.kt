package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.sortByNamesAndTotalMillis

class AnalysisPageViewModel (
    timeClockEvents: LiveData<List<TimeClockEvent>>,
    application: Application
): AndroidViewModel(application) {
    /**
     * Analysis page properties
     */
    private val allTimeAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = "All time"
    )
    private val todayAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = "Today",
        daysInRange = 1
    )
    private val lastWeekAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = "Last week",
        daysInRange = 7
    )
    private val lastMonthAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = "Last month",
        daysInRange = 30
    )
    var analysisPanes = listOf(
        allTimeAnalysisPane,
        todayAnalysisPane,
        lastWeekAnalysisPane,
        lastMonthAnalysisPane
    )
    private var currDateRangeIndex = 0
    var currAnalysisPane by mutableStateOf(analysisPanes[currDateRangeIndex])

    fun onDateRangeStartButtonClick() {
        if (currDateRangeIndex > 0) {
            updateAnalysisPane(currDateRangeIndex - 1)
        }
    }

    fun isDateRangeStartButtonVisible(): Boolean {
        return currDateRangeIndex > 0
    }

    fun onDateRangeEndButtonClick() {
        if (currDateRangeIndex < analysisPanes.size - 1) {
            updateAnalysisPane(currDateRangeIndex + 1)
        }
    }

    fun isDateRangeEndButtonVisible(): Boolean {
        return currDateRangeIndex < analysisPanes.size - 1
    }

    fun updateAnalysisPane(index: Int) {
        // reset selected row for curr pane
        analysisPanes[currDateRangeIndex].selectedAnalysisRowId = -1
        // change the current index
        currDateRangeIndex = index
        currAnalysisPane = analysisPanes[currDateRangeIndex]
    }
}