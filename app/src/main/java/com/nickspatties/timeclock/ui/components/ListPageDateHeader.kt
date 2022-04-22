package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ListPageDateHeader(
    modifier: Modifier = Modifier,
    dateString: String
) {
    val backgroundColor = MaterialTheme.colors.primary
    Box(
        modifier = modifier
            .height(TextFieldDefaults.MinHeight)
            .fillMaxWidth()
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = dateString,
            color = contentColorFor(backgroundColor),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListPageDateHeaderPreview() {
    val dateString = "April 20, 2022"
    ListPageDateHeader(dateString = dateString)
}