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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.data.TimeClockEvent
import com.nickspatties.timeclock.ui.components.*
import com.nickspatties.timeclock.ui.viewmodel.AnalysisPageViewModelState
import com.nickspatties.timeclock.util.MILLIS_PER_HOUR
import com.nickspatties.timeclock.util.decorateMillisWithDecimalHours
import com.nickspatties.timeclock.util.generateColorFromString

@Composable
fun AnalysisPage(
    viewModelState: AnalysisPageViewModelState
) {
    val analysisPageRows = viewModelState.analysisRows
    val openId = viewModelState.selectedAnalysisRowId
    val changeRowId = viewModelState::changeSelectedAnalysisRowId

    Scaffold {
        Column(
            modifier = Modifier.padding(it)
        ) {
            TimeRangeSelector(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                centerText = viewModelState.rangeName,
                startButtonFunction = viewModelState::onDateRangeStartButtonClick,
                startButtonVisible = viewModelState.isDateRangeStartButtonVisible(),
                endButtonFunction = viewModelState::onDateRangeEndButtonClick,
                endButtonVisible = viewModelState.isDateRangeEndButtonVisible()
            )
            if (analysisPageRows.isNotEmpty()) {
                var totalMillis = 0L
                analysisPageRows.forEach {
                    totalMillis += it.millis
                }
                val segmentData = mutableListOf<Triple<Color, Float, Long>>()
                analysisPageRows.forEach {
                    val color = it.color
                    val percentage = it.getPercentage(totalMillis) / 100f
                    val id = it.id
                    segmentData.add(
                        Triple(
                            color, percentage, id
                        )
                    )
                }
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
                            text = decorateMillisWithDecimalHours(viewModelState.selectedMillis),
                            style = MaterialTheme.typography.h3
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            style = MaterialTheme.typography.subtitle1,
                            text = stringResource(R.string.hours)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    analysisPageRows.forEach { row ->
                        val name = row.name
                        val id = row.id
                        val percentage = row.getPercentage(totalMillis)
                        item {
                            val percentageString = stringResource(R.string.percentage, percentage)
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
            } else {
                NothingHereText()
            }
        }
    }
}

@Preview
@Composable
fun AnalysisPageTest() {
    AnalysisPage(
        viewModelState = AnalysisPageViewModelState(
            events = listOf(
                TimeClockEvent(
                    startTime = 0L,
                    endTime = MILLIS_PER_HOUR,
                    name = "programming"
                )
            )
        )
    )
}
