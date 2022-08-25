package com.nickspatties.timeclock.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.filterEventsByNumberOfDays
import com.nickspatties.timeclock.util.sortByNamesAndTotalMillis

class AnalysisPane(
    events: List<TimeClockEvent>,
    val rangeName: String,
    private val daysInRange: Int = -1
) {
    var events by mutableStateOf(events)

    // variables updated when new events arrive
    private lateinit var filteredEvents : List<TimeClockEvent>
    private var totalMillis: Long = 0L
    lateinit var analysisRows: List<AnalysisRow>
    var selectedMillis by mutableStateOf(totalMillis)
    // end updated event variables

    var selectedAnalysisRowId by mutableStateOf(-1L)

    init {
        onEventsUpdate(events)
    }

    fun onEventsUpdate(inputEvents: List<TimeClockEvent>) {
        events = inputEvents
        filteredEvents = filterEventsByNumberOfDays(events, daysInRange)
        var millis = 0L
        filteredEvents.forEach {
            millis += it.endTime - it.startTime
        }
        totalMillis = millis
        selectedMillis = if (selectedAnalysisRowId == -1L) {
            totalMillis
        } else {
            // find the event that has been selected
            val matchingRow = analysisRows.find {
                selectedAnalysisRowId == it.id
            }
            matchingRow?.millis ?: totalMillis
        }
        analysisRows = (::sortByNamesAndTotalMillis)(filteredEvents)
    }

    fun changeSelectedAnalysisRowId(id: Long) {
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
