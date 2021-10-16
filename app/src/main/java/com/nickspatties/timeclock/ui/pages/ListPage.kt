package com.nickspatties.timeclock.ui.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.ui.TimeClockViewModel
import com.nickspatties.timeclock.util.decorateMillisLikeStopwatch

@Composable
fun ListPage(viewModel: TimeClockViewModel) {
    val allEvents = viewModel.allEvents.observeAsState()
    Scaffold(modifier = Modifier.padding(horizontal = 10.dp)) {
        if (allEvents.value.isNullOrEmpty()) {
            NothingHereText()
        } else {
            TimeClockList(
                events = allEvents.value!!,
                editingId = viewModel.editingEventId,
                onDeleteButtonClick = viewModel::deleteEvent,
                onListItemClick = viewModel::changeEditId
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

@Composable
fun TimeClockList (
    events: List<TimeClockEvent>,
    editingId: Long = -1,
    onDeleteButtonClick: (TimeClockEvent) -> Unit,
    onListItemClick: (Long) -> Unit
) {
    val reversedEvents = events.reversed()
    LazyColumn {
        items (reversedEvents) { item ->
            if (editingId == item.id) {
                TimeClockListItemEditor(
                    event = item,
                    onDeleteButtonClick = { onDeleteButtonClick(item) }
                )
            } else {
                TimeClockListItem(
                    event = item,
                    onClick = { onListItemClick(item.id) }
                )
            }
        }
    }
}

@Composable
fun TimeClockListItem(
    event: TimeClockEvent,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column {
            Text(text = event.name, style = MaterialTheme.typography.body1)
            val elapsedTime = event.endTime - event.startTime
            Text(
                text = "Elapsed time: ${decorateMillisLikeStopwatch(elapsedTime)}",
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
fun TimeClockListItemEditor(
    event: TimeClockEvent,
    onDeleteButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(8.dp)
    ) {
        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = event.name, style = MaterialTheme.typography.body1)
            Text(text = "startTime ${event.startTime}", style = MaterialTheme.typography.body1)
            Text(text = "endTime: ${event.endTime}", style = MaterialTheme.typography.body1)
            Row (
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Button(
                    onClick = onDeleteButtonClick
                ) {
                    Text(text = "Delete", style = MaterialTheme.typography.body1)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestTimeClockListItem() {
    val testEvent = TimeClockEvent(
        "Event name that is kinda long to write",
        startTime = 100L,
        endTime = 200L
    )
    TimeClockListItem(testEvent) {}
}

@Preview(showBackground = true)
@Composable
fun TestTimeClockListItemEditor() {
    val testEvent = TimeClockEvent(
        "Event name that is kinda long to write",
        startTime = 100L,
        endTime = 200L
    )
    TimeClockListItemEditor(
        event = testEvent,
        onDeleteButtonClick = {}
    )
}