package com.nickspatties.timeclock.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.filterEventsByNumberOfDays

class AnalysisPane(
    val eventData: LiveData<List<TimeClockEvent>>,
    val rowTransformation: (List<TimeClockEvent>) -> List<AnalysisRow>,
    val rangeName: String,
    private val daysInRange: Int = -1
) {

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
}
