package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nickspatties.timeclock.ui.theme.Purple500

@Composable
fun TaskPill (
    taskName: String,
    taskColor: Color,
    backgroundColor: Color
) {
    Surface(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
                shape = MaterialTheme.shapes.small
            )
            .padding(1.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .width(16.dp)
                    .height(16.dp)
                    .background(taskColor)

            )
            Text(
                text = taskName,
                style = MaterialTheme.typography.caption
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TaskPillPreview() {
    TaskPill(
        taskName = "Programming is cool",
        taskColor = Purple500,
        backgroundColor = MaterialTheme.colors.surface
    )
}