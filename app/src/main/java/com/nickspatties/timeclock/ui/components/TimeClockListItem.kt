package com.nickspatties.timeclock.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.util.decorateMillisLikeStopwatch
import com.nickspatties.timeclock.util.decorateMillisToDateString
import com.nickspatties.timeclock.util.generateColorFromString

/**
 * The individual item in a list of TimeClock items.
 *
 * @param isClosed determine if the list item is open or closed
 * @param accentColor The color that appears on the left side of the item
 * @param onClick The function to execute when a list item is clicked
 * @param closedContent Composable which shows the closed content of the list item
 * @param openContent Composable that shows the content of the list item when a
 *   user clicks an item
 */
@Composable
fun TimeClockListItem(
    isClosed: Boolean = true,
    accentColor: Color? = null,
    onClick: () -> Unit = {},
    closedContent: @Composable () -> Unit = {
        ClosedContent("Title", "Subtitle")
    },
    openContent: @Composable () -> Unit = {}
) {
    val itemHeight by animateDpAsState(
        targetValue = if (isClosed) TextFieldDefaults.MinHeight else 120.dp
    )
    Box(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .height(itemHeight)
    ) {
        Row() {
            if (accentColor != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(5.dp)
                        .background(accentColor),
                )
            }
            Crossfade(targetState = isClosed) { closed ->
                if (closed) {
                    closedContent()
                } else {
                    openContent()
                }
            }
        }
    }
}

@Composable
fun ClosedContent(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.caption
        )
    }
}

/**
 * Displays the open content of the list item
 *
 * @param eventName The name of the event displayed on the top
 * @param startTime The start time in milliseconds of the event
 * @param endTime The end time in milliseconds of the event
 * @param onCancelButtonClick callback function when the cancel button is clicked
 * @param onDeleteButtonClick callback function when delete button is clicked
 */
@Composable
fun OpenContent(
    eventName: String,
    startTime: Long,
    endTime: Long,
    onCancelButtonClick: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {}
) {
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = eventName, style = MaterialTheme.typography.body1)
        Text(text = "startTime ${decorateMillisToDateString(startTime)}", style = MaterialTheme.typography.body1)
        Text(text = "endTime: ${decorateMillisToDateString(endTime)}", style = MaterialTheme.typography.body1)
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

@Preview(showBackground = true)
@Composable
fun ClosedListItem() {
    val titleName = "Programming"
    val accentColor = generateColorFromString(titleName)
    val startTime = 0L
    val endTime = 10000L
    val elapsedTime = endTime - startTime
    val subtitleName = decorateMillisLikeStopwatch(elapsedTime)
    val closedContent = @Composable {
        ClosedContent(title = titleName, subtitle = subtitleName)
    }
    TimeClockListItem(
        accentColor = accentColor,
        onClick = {},
        closedContent = closedContent,
        openContent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun OpenListItem() {
    val titleName = "Programming"
    val accentColor = generateColorFromString(titleName)
    val startTime = 0L
    val endTime = 10000L
    val openContent = @Composable {
        OpenContent(
            eventName = titleName,
            startTime = startTime,
            endTime = endTime
        )
    }
    TimeClockListItem(
        isClosed = false,
        accentColor = accentColor,
        onClick = {},
        closedContent = {},
        openContent = openContent
    )
}
