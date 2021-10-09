package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.ui.TimeClockViewModel

@Composable
fun ListPage(viewModel: TimeClockViewModel) {
    Scaffold(modifier = Modifier.padding(horizontal = 10.dp)) {
        if (viewModel.timeClockEvents.size > 0) {
            TimeClockList(viewModel.timeClockEvents)
        } else {
            NothingHereText()
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
fun TimeClockList(events: List<TimeClockEvent>) {
    val reversedEvents = events.reversed()
    LazyColumn {
        items (reversedEvents) { item ->
            TimeClockListItem(item)
        }
    }
}

@Composable
fun TimeClockListItem(event: TimeClockEvent) {
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(8.dp)
    ) {
        Column {
            Text(text = event.name, style = MaterialTheme.typography.body1)
            Row {
                Text(text = "startTime: ${event.startTime}", style = MaterialTheme.typography.caption)
                Text(text = "endTime: ${event.endTime}", style = MaterialTheme.typography.caption)
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
    TimeClockListItem(testEvent)
}