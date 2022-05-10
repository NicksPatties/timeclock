package com.nickspatties.timeclock.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.util.convertHoursMinutesSecondsToMillis
import com.nickspatties.timeclock.util.generateColorFromString

@Composable
fun PieChart(
    analysisPageRows: List<Triple<String, Long, Long>>,
    currId: Long = -1L
) {
    // get total millis for chart
    var totalTime = 0L
    analysisPageRows.forEach {
        totalTime += it.second
    }
    val stroke = with(LocalDensity.current) { Stroke(15.dp.toPx()) }

    // instantly change the state from START to END, applying the transitions
    // in the animations below
    val currentState = remember {
        MutableTransitionState(AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val transition = updateTransition(currentState, label = "")
    val animationDelay = 400
    val animationDuration = 500
    val customTween : TweenSpec<Float> = tween(
        delayMillis = animationDelay,
        durationMillis = animationDuration,
        easing = FastOutSlowInEasing
    )

    // proportion of the pie that is filled in
    val pieSegment by transition.animateFloat(
        transitionSpec = { customTween },
        label = ""
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            0f
        } else {
            360f
        }
    }
    // where the pie starts being drawn
    val shift by transition.animateFloat(
        transitionSpec = { customTween },
        label = ""
    ) { progress ->
        if (progress == AnimatedCircleProgress.START) {
            -45f
        } else {
            0f
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        // define bounds of circle
        val innerRadius = (size.minDimension) / 2
        val halfSize = size / 2.0f
        val topLeft = Offset(
            halfSize.width - innerRadius,
            halfSize.height - innerRadius
        )
        val size = Size(innerRadius * 2, innerRadius * 2)
        var startAngle = shift - 90f
        analysisPageRows.forEach { row ->
            val color = generateColorFromString(row.first)
            val percentage = row.second / totalTime.toFloat() * pieSegment
            val alpha = if (currId == row.third || currId == -1L) 1.0f else 0.5f
            drawArc(
                style = stroke,
                color = color,
                startAngle = startAngle,
                sweepAngle = percentage,
                size = size,
                topLeft = topLeft,
                useCenter = false,
                alpha = alpha
            )
            startAngle += percentage
        }
    }
}

private enum class AnimatedCircleProgress { START, END }

@Preview(showBackground = true)
@Composable
fun PieChartPreview() {
    val rows = mutableListOf<Triple<String, Long, Long>>()
    rows.add(
        Triple("Programming", convertHoursMinutesSecondsToMillis(1), 0)
    )
    rows.add(
        Triple("Reading", convertHoursMinutesSecondsToMillis(1), 1)
    )
    rows.add(
        Triple("Homework", convertHoursMinutesSecondsToMillis(1), 2)
    )
    val boxSize = 500.dp
    Box(
        modifier = Modifier
            .height(boxSize)
            .width(boxSize)
            .padding(16.dp)
    ) {
        PieChart(rows)
    }
}