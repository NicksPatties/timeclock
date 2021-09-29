package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun ListPage() {
    val timeClockListItems = mutableListOf<String>()
    for (i in 0..100) {
        timeClockListItems.add("Task $i yeah yeah")
    }

    Scaffold(modifier = Modifier.padding(horizontal = 10.dp)) {
        LazyColumn {
            items (timeClockListItems) { item ->
                TimeClockListItem(item)
            }
        }
    }
}

@Composable
fun TimeClockListItem(taskName: String) {
    val duration = "01:00:00"
    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(8.dp)
    ) {
        Column {
            Text(text = taskName, style = MaterialTheme.typography.body1)
            Text(text = duration, style = MaterialTheme.typography.caption)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestTimeClockListItem() {
    TimeClockListItem(
        taskName = "the task name that is difficult to do"
    )
}