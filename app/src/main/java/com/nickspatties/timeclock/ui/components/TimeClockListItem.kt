package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.decorateMillisLikeStopwatch

@Composable
fun TimeClockListItem(
    event: TimeClockEvent,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .height(TextFieldDefaults.MinHeight)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = event.name, style = MaterialTheme.typography.body1)
            val elapsedTime = event.endTime - event.startTime
            if (event.isRunning) {
                Text(
                    text = "In progress...",
                    style = MaterialTheme.typography.caption
                )
            } else {
                Text(
                    text = decorateMillisLikeStopwatch(elapsedTime),
                    style = MaterialTheme.typography.caption
                )
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