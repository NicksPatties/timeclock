package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.ui.components.ClosedContent
import com.nickspatties.timeclock.ui.components.TimeClockListItem
import com.nickspatties.timeclock.util.convertHoursMinutesSecondsToMillis
import com.nickspatties.timeclock.util.generateColorFromString

@Composable
fun AnalysisPage(
    nameDurationPairs: List<Pair<String, Long>>?
) {
    Scaffold {
        if (nameDurationPairs != null && nameDurationPairs.isNotEmpty()) {
            LazyColumn {
                nameDurationPairs.forEach { pair ->
                    val name = pair.first
                    val duration = pair.second.toString()
                    item {
                        TimeClockListItem(
                            accentColor = generateColorFromString(name),
                            closedContent = {
                                ClosedContent(name, duration)
                            }
                        )
                    }
                }
            }
        } else {
            Text("No items")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalysisPagePreview() {
    val mockNameDurationPairs = listOf(
        Pair("Programming", convertHoursMinutesSecondsToMillis(1))
    )
    AnalysisPage(mockNameDurationPairs)
}

@Preview(showBackground = true)
@Composable
fun AnalysisPagePairsAreNull() {
    AnalysisPage(null)
}

@Preview(showBackground = true)
@Composable
fun AnalysisPageNoPairs() {
    AnalysisPage(listOf())
}