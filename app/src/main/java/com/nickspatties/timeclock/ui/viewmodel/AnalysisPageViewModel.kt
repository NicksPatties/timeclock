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
import com.nickspatties.timeclock.util.filterEventsByNumberOfDays
import com.nickspatties.timeclock.util.sortByNamesAndTotalMillis

class AnalysisPageViewModel (
    timeClockEvents: LiveData<List<TimeClockEvent>>,
    context: Context
): ViewModel() {

    val coolTransformation = Transformations.map(timeClockEvents) { events ->
        state.analysisPane.onEventsUpdate(events)
    }

    val events: List<TimeClockEvent> = timeClockEvents.value?: listOf()

    /**
     * Analysis page properties
     */


    val state = AnalysisPageViewModelState(
        events = events
    )
}

// todo change this class to handle one event first
class AnalysisPageViewModelState(
    events: List<TimeClockEvent>
) {

    var analysisPane = AnalysisPane(
        events = events,
        rangeName = "all of the time"
    )

    fun onDateRangeStartButtonClick() {
//        if (currDateRangeIndex > 0) {
//            updateAnalysisPane(currDateRangeIndex - 1)
//        }
    }

    fun isDateRangeStartButtonVisible(): Boolean {
//        return currDateRangeIndex > 0
        return false
    }

    fun onDateRangeEndButtonClick() {
//        if (currDateRangeIndex < panes.size - 1) {
//            updateAnalysisPane(currDateRangeIndex + 1)
//        }
    }

    fun isDateRangeEndButtonVisible(): Boolean {
        //return currDateRangeIndex < panes.size - 1
        return false
    }

    fun updateAnalysisPane(index: Int) {
//        panes[currDateRangeIndex].resetSelectedRowAndMillis()
//        // change the current index
//        currDateRangeIndex = index
//        currAnalysisPane = panes[currDateRangeIndex]
    }
}