package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout

/**
 * Allows user to select a date range to analyze in the Metrics screen.
 *
 * @param modifier
 * @param centerText The text to appear in the center of the component
 * @param startButtonFunction The function to execute when the button in the
 * start position is pressed
 * @param startButtonVisible Is the button in the start position visible?
 * @param endButtonFunction The function to execute when the button in the end
 * position is pressed
 * @param endButtonVisible Is the button in the end position visible?
 */
@Composable
fun TimeRangeSelector(
    modifier: Modifier = Modifier,
    centerText: String,
    startButtonFunction: () -> Unit = {},
    startButtonVisible: Boolean = true,
    endButtonFunction: () -> Unit = {},
    endButtonVisible: Boolean = true
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth(),
    ) {
        val (startButton, text, endButton) = createRefs()
        if (startButtonVisible) {
            IconButton(
                modifier = Modifier.constrainAs(startButton) {
                    start.linkTo(parent.start)
                    centerVerticallyTo(parent)
                },
                onClick = startButtonFunction
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "Date range start arrow"
                )
            }
        }
        Text(
            modifier = Modifier.constrainAs(text) {
                centerTo(parent)
            },
            text = centerText,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
        if (endButtonVisible) {
            IconButton(
                modifier = Modifier.constrainAs(endButton) {
                    end.linkTo(parent.end)
                    centerVerticallyTo(parent)
                },
                onClick = endButtonFunction
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    contentDescription = "Date range end arrow"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimeRangeSelectorPreview() {
    TimeRangeSelector(
        centerText = "Today"
    )
}

@Preview(showBackground = true)
@Composable
fun TimeRangeSelectorStartButtonVisible() {
    TimeRangeSelector(
        centerText = "Today",
        endButtonVisible = false
    )
}

@Preview(showBackground = true)
@Composable
fun TimeRangeSelectorEndButtonVisible() {
    TimeRangeSelector(
        centerText = "Today",
        startButtonVisible = false
    )
}
