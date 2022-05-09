package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nickspatties.timeclock.ui.components.AnalysisPageListItemClosedContent
import com.nickspatties.timeclock.ui.components.TimeClockListItem
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
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
                    val duration = decorateMillisWithDecimalHours(pair.second)
                    val totalTimeString =
                        if (duration.toFloat() == 1f) "$duration hr" else "$duration hrs"
                    item {
                        TimeClockListItem(
                            accentColor = generateColorFromString(name),
                            closedContent = {
                                AnalysisPageListItemClosedContent(name, totalTimeString)
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
    val pairsList = mutableListOf<Pair<String, Long>>()
    var items = 50
    while (items > 0) {
        pairsList.add(
            Pair("Item $items", items * 100000L)
        )
        items--
    }
    AnalysisPage(pairsList)
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