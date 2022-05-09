package com.nickspatties.timeclock.ui.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.ui.components.AnalysisPageListItemContent
import com.nickspatties.timeclock.ui.components.TimeClockListItem
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.generateColorFromString

@Composable
fun AnalysisPage(
    analysisPageRows: List<Triple<String, Long, Long>>?
) {
    Scaffold {
        val openId = remember { mutableStateOf(-1L) }

        if (analysisPageRows != null && analysisPageRows.isNotEmpty()) {
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
                                    if (isClosed)
                                        openId.value = id
                                    else
                                        openId.value = -1
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

@Composable
fun PieChart(
    analysisPageRows: List<Triple<String, Long, Long>>,
    currId: Long = -1L
) {
    var totalTime = 0L
    analysisPageRows.forEach {
        totalTime += it.second
    }
    Canvas(
        modifier = Modifier.fillMaxHeight().fillMaxWidth()
    ) {
        // define bounds of circle
        var startAngle = 0f
        analysisPageRows.forEach { row ->
            val color = generateColorFromString(row.first)
            val percentage = row.second / totalTime.toFloat() * 360f
            val alpha = if (currId == row.third || currId == -1L) 1.0f else 0.5f
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = percentage,
                useCenter = true,
                alpha = alpha
            )
            startAngle += percentage
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