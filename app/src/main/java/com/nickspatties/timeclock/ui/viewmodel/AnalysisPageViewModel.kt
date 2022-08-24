package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.nickspatties.timeclock.R
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
        rangeName = getApplication<Application>().applicationContext
            .getString(R.string.analysis_page_all_time)
    )
    private val todayAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = getApplication<Application>().applicationContext
            .getString(R.string.analysis_page_today),
        daysInRange = 1
    )
    private val lastWeekAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = getApplication<Application>().applicationContext
            .getString(R.string.analysis_page_last_week),
        daysInRange = 7
    )
    private val lastMonthAnalysisPane = AnalysisPane(
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = getApplication<Application>().applicationContext
            .getString(R.string.analysis_page_last_month),
        daysInRange = 30
    )
    private var analysisPanes = listOf(
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
        analysisPanes[currDateRangeIndex].resetSelectedRowAndMillis()
        // change the current index
        currDateRangeIndex = index
        currAnalysisPane = analysisPanes[currDateRangeIndex]
    }
}