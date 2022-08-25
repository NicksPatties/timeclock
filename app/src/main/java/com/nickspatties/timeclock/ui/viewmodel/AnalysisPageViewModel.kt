package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.sortByNamesAndTotalMillis

class AnalysisPageViewModel (
    timeClockEvents: LiveData<List<TimeClockEvent>>,
    context: Context
): ViewModel() {

    val coolTransformation = Transformations.map(timeClockEvents) { events ->
        allTimeAnalysisPane.eventsValue = events
        todayAnalysisPane.eventsValue = events
        lastWeekAnalysisPane.eventsValue = events
        lastMonthAnalysisPane.eventsValue = events
    }

    val events = timeClockEvents.value

    /**
     * Analysis page properties
     */
    private val allTimeAnalysisPane = AnalysisPane(
        eventsValue = events,
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = context
            .getString(R.string.analysis_page_all_time)
    )
    private val todayAnalysisPane = AnalysisPane(
        eventsValue = events,
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = context
            .getString(R.string.analysis_page_today),
        daysInRange = 1
    )
    private val lastWeekAnalysisPane = AnalysisPane(
        eventsValue = events,
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = context.getString(R.string.analysis_page_last_week),
        daysInRange = 7
    )
    private val lastMonthAnalysisPane = AnalysisPane(
        eventsValue = events,
        eventData = timeClockEvents,
        rowTransformation = ::sortByNamesAndTotalMillis,
        rangeName = context.getString(R.string.analysis_page_last_month),
        daysInRange = 30
    )

    val state = AnalysisPageViewModelState(
        panes = listOf(
            allTimeAnalysisPane,
            todayAnalysisPane,
            lastWeekAnalysisPane,
            lastMonthAnalysisPane
        )
    )
}

class AnalysisPageViewModelState(
    private val panes: List<AnalysisPane> = listOf()
) {
    private var currDateRangeIndex = 0
    var currAnalysisPane by mutableStateOf(panes[currDateRangeIndex])

    fun onDateRangeStartButtonClick() {
        if (currDateRangeIndex > 0) {
            updateAnalysisPane(currDateRangeIndex - 1)
        }
    }

    fun isDateRangeStartButtonVisible(): Boolean {
        return currDateRangeIndex > 0
    }

    fun onDateRangeEndButtonClick() {
        if (currDateRangeIndex < panes.size - 1) {
            updateAnalysisPane(currDateRangeIndex + 1)
        }
    }

    fun isDateRangeEndButtonVisible(): Boolean {
        return currDateRangeIndex < panes.size - 1
    }

    fun updateAnalysisPane(index: Int) {
        panes[currDateRangeIndex].resetSelectedRowAndMillis()
        // change the current index
        currDateRangeIndex = index
        currAnalysisPane = panes[currDateRangeIndex]
    }
}