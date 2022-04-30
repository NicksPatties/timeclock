package com.nickspatties.timeclock.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.util.decorateMillisToDateString

/**
 * The individual item in a list of TimeClock items.
 *
 * @param title The text that appears at the top of the line
 * @param subtitle The text that appears at the bottom line
 * @param accentColor The color that appears on the left side of the item
 * @param onClick The function to execute when a list item is clicked
 */
@Composable
fun TimeClockListItem(
    accentColor: Color? = null,
    onClick: () -> Unit = {},
    closedContent: @Composable () -> Unit = {
        ClosedContent()
    },
    openContent: @Composable () -> Unit = {
        OpenContent()
    }
) {
    var isClosed by remember { mutableStateOf(true) }
    val itemHeight by animateDpAsState(
        targetValue = if (isClosed) TextFieldDefaults.MinHeight else 120.dp
    )
    Box(
        modifier = Modifier
            .clickable {
                onClick()
                isClosed = !isClosed
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
                if (closed) closedContent() else openContent()
            }
        }
    }
}

@Composable
fun ClosedContent(
    title: String = "Title string",
    subtitle: String = "Subtitle string"
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

@Composable
fun OpenContent() {
    val eventName = "Some event name"
    val startTime = 0L
    val endTime = 10000000L
    val onCancelButtonClick = {}
    val onDeleteButtonClick = {}
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
fun HappyCase() {
    val title = "Programming"
    TimeClockListItem()
}
