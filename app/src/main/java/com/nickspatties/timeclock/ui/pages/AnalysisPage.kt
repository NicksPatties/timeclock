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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.ui.components.AnalysisPageListItemContent
import com.nickspatties.timeclock.ui.components.PieChart
import com.nickspatties.timeclock.ui.components.TimeClockListItem
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.generateColorFromString

@Composable
fun AnalysisPage(
    analysisPageRows: List<Triple<String, Long, Long>>?
) {
    Scaffold {
        if (analysisPageRows != null && analysisPageRows.isNotEmpty()) {
            // data
            val openId = remember { mutableStateOf(-1L) }
            // all hours
            var totalMillis = 0L
            analysisPageRows.forEach {
                totalMillis += it.second
            }
            val totalHours = decorateMillisWithDecimalHours(totalMillis)
            val hoursDisplay = remember { mutableStateOf(totalHours) }
            // split this view into two boxes
            Column {
                Box( // chart container?
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .padding(16.dp)
                ) {
                    PieChart(
                        analysisPageRows = analysisPageRows,
                        currId = openId.value
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
                            text = hoursDisplay.value.toString(),
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
                        val totalTimeString =
                            if (duration.toFloat() == 1f) "$duration hr" else "$duration hrs"
                        item {
                            val isClosed = openId.value != id
                            TimeClockListItem(
                                isClosed = isClosed,
                                accentColor = generateColorFromString(name),
                                onClick = {
                                    if (isClosed) {
                                        openId.value = id
                                        hoursDisplay.value = duration
                                    } else {
                                        openId.value = -1
                                        hoursDisplay.value = totalHours
                                    }
                                },
                                closedContent = {
                                    AnalysisPageListItemContent(name, totalTimeString)
                                },
                                openContent = {
                                    AnalysisPageListItemContent(
                                        taskName = name,
                                        totalHours = totalTimeString,
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