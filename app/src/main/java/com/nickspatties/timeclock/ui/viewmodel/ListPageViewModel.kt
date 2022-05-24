package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.util.decorateMillisToDateString
import kotlinx.coroutines.launch

class ListPageViewModel(
    private val database: TimeClockEventDao,
    timeClockEvents: LiveData<List<TimeClockEvent>>,
    application: Application
): AndroidViewModel(application) {
    val groupedEventsByDate = Transformations.map(timeClockEvents) { events ->
        val listRows: List<ListRow> = events.map {
            ListRow(it.name, it.startTime, it.endTime, it.id)
        }
        listRows.groupBy {
            decorateMillisToDateString(it.startTime)
        }
    }
    var editingEventId by mutableStateOf(-1L)

    fun changeEditId(id: Long) {
        editingEventId = id
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            val eventToDelete = database.get(id)
            if(eventToDelete != null) {
                database.delete(eventToDelete)
                editingEventId = -1
            } else {
                showToast("Failed to delete event")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}