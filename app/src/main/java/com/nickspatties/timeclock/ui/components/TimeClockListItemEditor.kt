package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.util.decorateMillisToDateString

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