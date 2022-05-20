package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.ui.AnalysisRow
import com.nickspatties.timeclock.ui.components.AnalysisPageListItemContent
import com.nickspatties.timeclock.ui.components.PieChart
import com.nickspatties.timeclock.ui.components.TimeClockListItem
import com.nickspatties.timeclock.ui.components.TimeRangeSelector
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.generateColorFromString

@Composable
fun AnalysisPage(
    currentSelectionString: String = "",
    onSelectionStartButtonClick: () -> Unit = {},
    onSelectionEndButtonClick: () -> Unit = {},
    selectionStartButtonVisible: Boolean = false,
    selectionEndButtonVisible: Boolean = false,
    analysisPageRows: List<AnalysisRow>?,
    totalSelectedHours: String = "",
    openId: Long = -1,
    changeRowId: (Long) -> Unit = {}
) {
    Scaffold {
        if (analysisPageRows != null) {
            // all hours
            var totalMillis = 0L
            analysisPageRows.forEach {
                totalMillis += it.millis
            }
            val segmentData = mutableListOf<Triple<Color, Float, Long>>()
            analysisPageRows.forEach {
                val color = it.color
                val percentage = it.getPercentage(totalMillis)
                val id = it.id
                segmentData.add(Triple(
                    color, percentage, id
                ))
            }
            Column {
                TimeRangeSelector(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    centerText = currentSelectionString,
                    startButtonFunction = onSelectionStartButtonClick,
                    startButtonVisible = selectionStartButtonVisible,
                    endButtonFunction = onSelectionEndButtonClick,
                    endButtonVisible = selectionEndButtonVisible
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.6f)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 32.dp,
                            bottom = 32.dp
                        )
                ) {
                    PieChart(
                        segmentData = segmentData,
                        currId = openId
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // total hours recorded
                        Text(
                            text = totalSelectedHours,
                            style = MaterialTheme.typography.h3
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            style = MaterialTheme.typography.subtitle1,
                            text = "hours"
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    analysisPageRows.forEach { row ->
                        val name = row.name
                        val duration = decorateMillisWithDecimalHours(row.millis)
                        val id = row.id
                        val percentage = row.millis / totalMillis.toFloat() * 100f
                        val percentageString = "%.2f".format(percentage) + "%"
                        item {
                            val isClosed = openId != id
                            TimeClockListItem(
                                isClosed = isClosed,
                                accentColor = generateColorFromString(name),
                                onClick = { changeRowId(id) },
                                closedContent = {
                                    AnalysisPageListItemContent(name, percentageString)
                                },
                                openContent = {
                                    AnalysisPageListItemContent(
                                        taskName = name,
                                        totalHours = percentageString,
                                        isClosed = false
                                    )
                                },
                                openContentHeight = 100.dp
                            )
                        }
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
    val pairsList = mutableListOf<AnalysisRow>()
    var items = 50
    var id = 0L
    while (items > 0) {
        pairsList.add(
            AnalysisRow("Item $items", items * 100000L, id)
        )
        id++
        items--
    }
    AnalysisPage(analysisPageRows = pairsList)
}

@Preview(showBackground = true)
@Composable
fun AnalysisPagePairsAreNull() {
    AnalysisPage(analysisPageRows = null)
}

@Preview(showBackground = true)
@Composable
fun AnalysisPageNoPairs() {
    AnalysisPage(analysisPageRows = listOf())
}
