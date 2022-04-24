package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.util.generateColorFromString

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
    title: String,
    subtitle: String,
    accentColor: Color? = null,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .height(TextFieldDefaults.MinHeight)
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
    }
}

@Preview(showBackground = true)
@Composable
fun HappyCase() {
    val title = "Programming"
    val accentColor = generateColorFromString(title)
    val subtitle = "01:23:45"
    TimeClockListItem(
        title = title,
        subtitle = subtitle,
        accentColor = accentColor
    )
}

@Preview(showBackground = true)
@Composable
fun TimerRunning() {
    val title = "Programming"
    val accentColor = generateColorFromString(title)
    val subtitle = "Is Running..."
    TimeClockListItem(
        title = title,
        subtitle = subtitle,
        accentColor = accentColor
    )
}

@Preview(showBackground = true)
@Composable
fun LongTitleName() {
    val title =
        "Writing my very long thesis on how to make text fields truncate properly."
    val accentColor = generateColorFromString(title)
    val subtitle = "01:23:45"
    TimeClockListItem(
        title = title,
        subtitle = subtitle,
        accentColor = accentColor
    )
}

@Preview(showBackground = true)
@Composable
fun NoAccentColor() {
    val title = "There is no accent color."
    val subtitle = "How neat is that?"
    TimeClockListItem(
        title = title,
        subtitle = subtitle
    )
}