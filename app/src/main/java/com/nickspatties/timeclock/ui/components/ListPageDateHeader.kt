package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ListPageDateHeader(dateString: String) {
    val backgroundColor = MaterialTheme.colors.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = dateString,
            color = contentColorFor(backgroundColor),
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListPageDateHeaderPreview() {
    val dateString = "April 20, 2022"
    ListPageDateHeader(dateString = dateString)
}
