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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.nickspatties.timeclock.util.convertHoursMinutesSecondsToMillis
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.generateColorFromString

@Composable
fun AnalysisPageListItemClosedContent(
    taskName: String,
    totalHours: String
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),

    ) {
        val (name, time) = createRefs()
        Text(
            modifier = Modifier.constrainAs(name) {
                start.linkTo(parent.start)
                end.linkTo(time.start)
                centerVerticallyTo(parent)
                width = Dimension.fillToConstraints
            },
            text = taskName,
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.constrainAs(time) {
                start.linkTo(name.end)
                end.linkTo(parent.end)
                centerVerticallyTo(parent)
            },
            text = totalHours,
            maxLines = 1,
            style = MaterialTheme.typography.h6,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ClosedAnalysisPageListItem() {
    val titleName = "Mega hyper long ass name that takes"
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
