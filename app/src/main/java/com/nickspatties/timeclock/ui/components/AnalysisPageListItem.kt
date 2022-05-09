package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.util.convertHoursMinutesSecondsToMillis
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.generateColorFromString

// TODO: may benefit from constraint layout
@Composable
fun AnalysisPageListItemClosedContent(
    taskName: String,
    totalHours: String
) {
    Row(
        modifier = Modifier.fillMaxHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(0.75f),
            text = taskName,
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = totalHours,
            maxLines = 1,
            style = MaterialTheme.typography.h6,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ClosedAnalysisPageListItem() {
    val titleName = "programming"
    val accentColor = generateColorFromString(titleName)
    val totalTime = decorateMillisWithDecimalHours(
        convertHoursMinutesSecondsToMillis(999)
    )
    val totalTimeString =
        if (totalTime.toFloat() == 1f) "$totalTime hr" else "$totalTime hrs"
    val closedContent = @Composable {
        AnalysisPageListItemClosedContent(titleName, totalTimeString)
    }
    TimeClockListItem(
        accentColor = accentColor,
        onClick = {},
        closedContent = closedContent,
        openContent = {}
    )
}
