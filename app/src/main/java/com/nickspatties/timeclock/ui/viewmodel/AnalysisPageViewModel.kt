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
        state.events = events
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


    val state = AnalysisPageViewModelState(
        events = events?: listOf()
    )
}

// todo change this class to handle one event first
class AnalysisPageViewModelState(
    events: List<TimeClockEvent>
) {
    var events by mutableStateOf(events)
    var selectedMillis by mutableStateOf(0L)
    var selectedAnalysisRowId by mutableStateOf(-1L)
    var rangeName by mutableStateOf("all time")
    val analysisRows: List<AnalysisRow>
        get() {
            val filteredEvents = filterEventsByNumberOfDays(events)

            // count total milliseconds
            var totalMillis = 0L
            filteredEvents.forEach {
                totalMillis += it.endTime - it.startTime
            }
            // transform the rows
            return (::sortByNamesAndTotalMillis)(filteredEvents)
        }

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

    private fun getTotalMillis() : Long {
//        var totalMillis = 0L
//        analysisRows.forEach { event : AnalysisRow ->
//            totalMillis += event.millis
//        }
//        return totalMillis
        return 0L
    }

    fun changeSelectedAnalysisRowId(id: Long) {
        val totalMillis = getTotalMillis()
        selectedAnalysisRowId =
            if (selectedAnalysisRowId == id) -1L else id
        selectedMillis = if (selectedAnalysisRowId == -1L) {
            totalMillis
        } else {
            // find the event that has been selected
            val matchingRow = analysisRows.find {
                id == it.id
            }
            matchingRow?.millis ?: totalMillis
        }
    }
}