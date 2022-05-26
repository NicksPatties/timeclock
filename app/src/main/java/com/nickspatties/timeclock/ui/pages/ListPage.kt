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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nickspatties.timeclock.ui.components.*
import com.nickspatties.timeclock.ui.viewmodel.ListRow
import com.nickspatties.timeclock.util.MockTimeClockEventsGroupedByDate
import com.nickspatties.timeclock.R

@Composable
fun ListPage(
    groupedRows: Map<String, List<ListRow>>?,
    editingEventId: Long,
    onListItemClick: (Long) -> Unit,
    onDeleteButtonClick: (Long) -> Unit,
    onCancelButtonClick: (Long) -> Unit,
) {
    Scaffold() {
        if (groupedRows.isNullOrEmpty()) {
            NothingHereText()
        } else {
            TimeClockList(
                groupedEvents = groupedRows,
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
            text = stringResource(R.string.list_page_nothing_here),
            fontSize = size,
            color = color
        )
        Text(
            text = stringResource(R.string.list_page_fill_this_list),
            fontSize = size,
            color = color
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimeClockList(
    groupedEvents: Map<String, List<ListRow>>,
    editingId: Long = -1,
    onCancelButtonClick: (Long) -> Unit,
    onDeleteButtonClick: (Long) -> Unit,
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
                val accentColor = item.color
                val titleName = item.title
                val subtitleName =
                    if (item.isRunning) stringResource(R.string.list_page_in_progress)
                    else item.elapsedTimeString
                val closedContent = @Composable {
                    ListPageListItemClosedContent(title = titleName, subtitle = subtitleName)
                }
                val openContent = @Composable {
                    ListPageListItemOpenContent(
                        eventName = titleName,
                        startTime = item.startTime,
                        endTime = item.endTime,
                        onCancelButtonClick = { onCancelButtonClick(-1) },
                        onDeleteButtonClick = { onDeleteButtonClick(item.id) }
                    )
                }
                TimeClockListItem(
                    isClosed = editingId != item.id,
                    accentColor = accentColor,
                    onClick = {
                        if (!item.isRunning) onListItemClick(item.id)
                    },
                    closedContent = closedContent,
                    openContent = openContent
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListPagePreview() {
    ListPage(
        groupedRows = MockTimeClockEventsGroupedByDate,
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
        groupedRows = emptyMap(),
        editingEventId = -1,
        onListItemClick = {},
        onDeleteButtonClick = {},
        onCancelButtonClick = {}
    )
}

