package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.ui.components.AnalysisPageListItemContent
import com.nickspatties.timeclock.ui.components.PieChart
import com.nickspatties.timeclock.ui.components.TimeClockListItem
import com.nickspatties.timeclock.ui.components.TimeRangeSelector
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.generateColorFromString

@Composable
fun AnalysisPage(
    analysisPageRows: List<Triple<String, Long, Long>>?,
    openId: Long = -1,
    changeRowId: (Long) -> Unit = {}
) {
    Scaffold {
        if (analysisPageRows != null && analysisPageRows.isNotEmpty()) {
            // all hours
            var totalMillis = 0L
            analysisPageRows.forEach {
                totalMillis += it.second
            }
            val segmentData = mutableListOf<Triple<Color, Float, Long>>()
            analysisPageRows.forEach {
                val color = generateColorFromString(it.first)
                val percentage = it.second / totalMillis.toFloat()
                val id = it.third
                segmentData.add(Triple(
                    color, percentage, id
                ))
            }
            val totalHours = decorateMillisWithDecimalHours(totalMillis)
            val hoursDisplay = remember { mutableStateOf(totalHours) }
            Column {
                TimeRangeSelector(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    centerText = "Today"
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
                            text = hoursDisplay.value,
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
                    analysisPageRows.forEach { triple ->
                        val name = triple.first
                        val duration = decorateMillisWithDecimalHours(triple.second)
                        val id = triple.third
                        val percentage = triple.second / totalMillis.toFloat() * 100f
                        val percentageString = "%.2f".format(percentage) + "%"
                        item {
                            val isClosed = openId != id
                            TimeClockListItem(
                                isClosed = isClosed,
                                accentColor = generateColorFromString(name),
                                onClick = {
                                    if (isClosed) {
                                        changeRowId(id)
                                        hoursDisplay.value = duration
                                    } else {
                                        changeRowId(-1)
                                        hoursDisplay.value = totalHours
                                    }
                                },
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
    val pairsList = mutableListOf<Triple<String, Long, Long>>()
    var items = 50
    var id = 0L
    while (items > 0) {
        pairsList.add(
            Triple("Item $items", items * 100000L, id)
        )
        id++
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
