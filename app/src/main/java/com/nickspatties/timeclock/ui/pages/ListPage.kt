package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.ui.TimeClockViewModel
import com.nickspatties.timeclock.util.decorateMillisLikeStopwatch
import com.nickspatties.timeclock.util.decorateMillisToDateString

@Composable
fun ListPage(viewModel: TimeClockViewModel) {
    val groupedEvents = viewModel.groupedEvents.observeAsState()
    Scaffold(modifier = Modifier.padding(horizontal = 10.dp)) {
        if (groupedEvents.value.isNullOrEmpty()) {
            NothingHereText()
        } else {
            TimeClockList(
                groupedEvents = groupedEvents.value!!,
                editingId = viewModel.editingEventId,
                onDeleteButtonClick = viewModel::deleteEvent,
                onCancelButtonClick = viewModel::changeEditId,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeClockList (
    groupedEvents: Map<String, List<TimeClockEvent>>,
    editingId: Long = -1,
    onCancelButtonClick: (Long) -> Unit,
    onDeleteButtonClick: (TimeClockEvent) -> Unit,
    onListItemClick: (Long) -> Unit
) {
    LazyColumn (
        modifier = Modifier.padding(5.dp, 50.dp)
    ) {
        groupedEvents.forEach { (dateString, events) ->
            stickyHeader {
                DateHeader(dateString = dateString)
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

@Composable
fun DateHeader(dateString: String) {
    val backgroundColor = MaterialTheme.colors.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = dateString,
            color = contentColorFor(backgroundColor),
            fontWeight = FontWeight.Bold
        )
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
            if (event.isRunning) {
                Text(
                    text = "In progress...",
                    style = MaterialTheme.typography.caption
                )
            } else {
                Text(
                    text = "Elapsed time: ${decorateMillisLikeStopwatch(elapsedTime)}",
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
fun TimeClockListItemEditor(
    event: TimeClockEvent,
    onCancelButtonClick: () -> Unit,
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
            Text(text = "startTime ${decorateMillisToDateString(event.startTime)}", style = MaterialTheme.typography.body1)
            Text(text = "endTime: ${decorateMillisToDateString(event.endTime)}", style = MaterialTheme.typography.body1)
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onCancelButtonClick
                ) {
                    Text(text = "Cancel", style = MaterialTheme.typography.body1)
                }
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
        onCancelButtonClick = {},
        onDeleteButtonClick = {}
    )
}