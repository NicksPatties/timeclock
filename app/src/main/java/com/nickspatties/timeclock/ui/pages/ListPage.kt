package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.ui.components.ListPageDateHeader
import com.nickspatties.timeclock.ui.components.TimeClockListItem
import com.nickspatties.timeclock.ui.components.TimeClockListItemEditor
import com.nickspatties.timeclock.util.MockTimeClockEventsGroupedByDate

@Composable
fun ListPage(
    groupedEvents: Map<String, List<TimeClockEvent>>?,
    editingEventId: Long,
    onListItemClick: (Long) -> Unit,
    onDeleteButtonClick: (TimeClockEvent) -> Unit,
    onCancelButtonClick: (Long) -> Unit,
) {
    Scaffold() {
        if (groupedEvents.isNullOrEmpty()) {
            NothingHereText()
        } else {
            TimeClockList(
                groupedEvents = groupedEvents,
                editingId = editingEventId,
                onDeleteButtonClick = onDeleteButtonClick,
                onCancelButtonClick = onCancelButtonClick,
                onListItemClick = onListItemClick
            )
        }
    }
}

@Composable
fun NothingHereText() {
    Column {
        Text(
            text = "Looks like there's nothing here"
        )
        Text(
            text = "Record some events to fill this list!"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeClockList (
    groupedEvents: Map<String, List<TimeClockEvent>>,
    editingId: Long = -1,
    onCancelButtonClick: (Long) -> Unit,
    onDeleteButtonClick: (TimeClockEvent) -> Unit,
    onListItemClick: (Long) -> Unit
) {
    LazyColumn () {
        groupedEvents.forEach { (dateString, events) ->
            stickyHeader {
                ListPageDateHeader(dateString = dateString)
            }
            items (events) { item ->
                when {
                    editingId == item.id -> {
                        TimeClockListItemEditor(
                            event = item,
                            onCancelButtonClick = { onCancelButtonClick(-1) },
                            onDeleteButtonClick = { onDeleteButtonClick(item) }
                        )
                    }
                    item.isRunning -> {
                        TimeClockListItem(
                            event = item,
                            onClick = {}
                        )
                    }
                    else -> {
                        TimeClockListItem(
                            event = item,
                            onClick = { onListItemClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListPagePreview() {
    ListPage(
        groupedEvents = MockTimeClockEventsGroupedByDate,
        editingEventId = -1,
        onListItemClick = {},
        onDeleteButtonClick = {},
        onCancelButtonClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ListPageEmptyPreview() {
    ListPage(
        groupedEvents = emptyMap(),
        editingEventId = -1,
        onListItemClick = {},
        onDeleteButtonClick = {},
        onCancelButtonClick = {}
    )
}

