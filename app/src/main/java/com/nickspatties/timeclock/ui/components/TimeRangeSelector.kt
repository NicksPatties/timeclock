package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TimeRangeSelector(
    modifier: Modifier = Modifier,
    centerText: String,
    startButtonFunction: () -> Unit = {},
    startButtonVisible: Boolean = true,
    endButtonFunction: () -> Unit = {},
    endButtonVisible: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = startButtonFunction
        ) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                contentDescription = "Date range start arrow"
            )
        }
        Text(
            text = centerText,
            style = MaterialTheme.typography.h6
        )
        IconButton(
            onClick = endButtonFunction
        ) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                contentDescription = "Date range end arrow"
            )
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