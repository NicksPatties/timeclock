package com.nickspatties.timeclock.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nickspatties.timeclock.R

@Composable
fun NothingHereText() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val size = 24.sp
        val color = MaterialTheme.colors.onSurface.copy(alpha = 0.33f)
        Text(
            text = stringResource(R.string.list_page_nothing_here),
            fontSize = size,
            color = color
        )
        Text(
            text = stringResource(R.string.list_page_fill_this_list),
            fontSize = size,
            color = color
        )
    }
}