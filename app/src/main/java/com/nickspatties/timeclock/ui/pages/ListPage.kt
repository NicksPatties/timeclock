package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.ui.components.ListPageDateHeader
import com.nickspatties.timeclock.ui.components.TimeClockListItem
import com.nickspatties.timeclock.ui.components.TimeClockListItemEditor
import com.nickspatties.timeclock.util.MockTimeClockEventsGroupedByDate
import com.nickspatties.timeclock.util.decorateMillisLikeStopwatch
import com.nickspatties.timeclock.util.generateColorFromString

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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val size = 24.sp
        val color = MaterialTheme.colors.onSurface.copy(alpha = 0.33f)
        Text(
            text = "Looks like there's nothing here.",
            fontSize = size,
            color = color
        )
        Text(
            text = "Record some events to fill this list!",
            fontSize = size,
            color = color
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
    LazyColumn(
        modifier = Modifier.clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
    ) {
        groupedEvents.forEach { (dateString, events) ->
            stickyHeader {
                ListPageDateHeader(dateString = dateString)
            }
            items(events) { item ->
                when {
                    editingId == item.id -> {
                        TimeClockListItemEditor(
                            event = item,
                            onCancelButtonClick = { onCancelButtonClick(-1) },
                            onDeleteButtonClick = { onDeleteButtonClick(item) }
                        )
                    }
                    else -> {
                        val accentColor = generateColorFromString(item.name)
                        val titleName = item.name
                        val elapsedTime = item.endTime - item.startTime
                        val subtitleName =
                            if (item.isRunning) "Running..."
                            else decorateMillisLikeStopwatch(elapsedTime)

                        TimeClockListItem(
                            title = titleName,
                            subtitle = subtitleName,
                            accentColor = accentColor,
                            onClick = {
                                if(!item.isRunning) onListItemClick(item.id)
                            }
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

