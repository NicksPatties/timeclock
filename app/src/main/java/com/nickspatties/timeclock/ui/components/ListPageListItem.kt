package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.util.convertHoursMinutesSecondsToMillis
import com.nickspatties.timeclock.util.decorateMillisLikeStopwatch
import com.nickspatties.timeclock.util.decorateMillisToTimeString
import com.nickspatties.timeclock.util.generateColorFromString

/**
 * Displays the name of a task, and the amount of time spent on that task.
 */
@Composable
fun ListPageListItemClosedContent(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
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
 * Displays the full name, start, end time, and control buttons.
 *
 * @param eventName The name of the event displayed on the top
 * @param startTime The start time in milliseconds of the event
 * @param endTime The end time in milliseconds of the event
 * @param onCancelButtonClick callback function when the cancel button is clicked
 * @param onDeleteButtonClick callback function when delete button is clicked
 */
@Composable
fun ListPageListItemOpenContent(
    eventName: String,
    startTime: Long,
    endTime: Long,
    onCancelButtonClick: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {}
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = eventName, style = MaterialTheme.typography.body1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ClockComponent(
                timeString = decorateMillisToTimeString(startTime),
                subtitle = "Start time"
            )
            ClockComponent(
                timeString = decorateMillisToTimeString(endTime),
                subtitle = "End time"
            )
        }
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onCancelButtonClick
            ) {
                Text(text = "Cancel", style = MaterialTheme.typography.body1)
            }
            OutlinedButton(
                onClick = onDeleteButtonClick
            ) {
                Text(text = "Delete", style = MaterialTheme.typography.body1)
            }
        }
    }
}

/**
 * Large string on top of smaller string, centered
 */
@Composable
fun ClockComponent(
    timeString: String,
    subtitle: String
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = timeString, style = MaterialTheme.typography.h4)
        Text(text = subtitle, style = MaterialTheme.typography.subtitle1)
    }
}

@Preview(showBackground = true)
@Composable
fun ClosedListItem() {
    val titleName =
        "A long task name that is easy to test to make sure everything works as expected."
    val accentColor = generateColorFromString(titleName)
    val startTime = 0L
    val endTime = 10000L
    val elapsedTime = endTime - startTime
    val subtitleName = decorateMillisLikeStopwatch(elapsedTime)
    val closedContent = @Composable {
        ListPageListItemClosedContent(title = titleName, subtitle = subtitleName)
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
    val titleName =
        "A long task name that is easy to test to make sure everything works as expected."
    val accentColor = generateColorFromString(titleName)
    val startTime = 0L
    val endTime = convertHoursMinutesSecondsToMillis(1) + startTime
    val openContent = @Composable {
        ListPageListItemOpenContent(
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

@Preview(showBackground = true)
@Composable
fun OpenListItemShortTitle() {
    val titleName = "Programming"
    val accentColor = generateColorFromString(titleName)
    val startTime = 0L
    val endTime = convertHoursMinutesSecondsToMillis(1) + startTime
    val openContent = @Composable {
        ListPageListItemOpenContent(
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

@Preview(showBackground = true)
@Composable
fun ClockItemPreview() {
    ClockComponent(
        timeString = "5:25 PM",
        subtitle = "End time"
    )
}
