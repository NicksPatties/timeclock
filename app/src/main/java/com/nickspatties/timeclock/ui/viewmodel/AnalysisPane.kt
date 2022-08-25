package com.nickspatties.timeclock.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.filterEventsByNumberOfDays

class AnalysisPane(
    var eventsValue: List<TimeClockEvent>? = null,
    val eventData: LiveData<List<TimeClockEvent>>,
    val rowTransformation: (List<TimeClockEvent>) -> List<AnalysisRow>,
    val rangeName: String,
    private val daysInRange: Int = -1
) {

    private val events = eventsValue ?: listOf()

    // todo try and do the following things
    // pass only a list of events into this class
    // getTotalMillis keeps breaking due to NPE regarding assigning selectedMillis, perhaps edit
    //   getTotalMillis to take in a list of AnalysisRows as a param, so I can
    //   or perhaps an init block to assign selectedMillis to the correct value after everything's been assigned

    val rowData: LiveData<List<AnalysisRow>> = Transformations.map(eventData) { events ->
        // filter events by range
        val filteredEvents = filterEventsByNumberOfDays(events, daysInRange)

        // count total milliseconds
        var totalMillis = 0L
        filteredEvents.forEach {
            totalMillis += it.endTime - it.startTime
        }
        selectedMillis = totalMillis
        // transform the rows
        return@map rowTransformation(filteredEvents)
    }

    private fun getTotalMillis() : Long {
        var totalMillis = 0L
        rowData.value?.forEach { event : AnalysisRow ->
            totalMillis += event.millis
        }
        return totalMillis
    }
    var selectedMillis by mutableStateOf(getTotalMillis())
    var selectedAnalysisRowId by mutableStateOf(-1L)

    fun changeSelectedAnalysisRowId(id: Long) {
        val totalMillis = getTotalMillis()
        selectedAnalysisRowId =
            if (selectedAnalysisRowId == id) -1L else id
        selectedMillis = if (selectedAnalysisRowId == -1L) {
            totalMillis
        } else {
            // find the event that has been selected
            val matchingRow = rowData.value?.find {
                id == it.id
            }
            matchingRow?.millis ?: totalMillis
        }
    }

    fun resetSelectedRowAndMillis() {
        selectedMillis = getTotalMillis()
        selectedAnalysisRowId = -1
    }
}
