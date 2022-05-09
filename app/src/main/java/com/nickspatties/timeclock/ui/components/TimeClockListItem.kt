package com.nickspatties.timeclock.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The individual item in a list. Serves as a container for different list
 * items in the app.
 *
 * @param isClosed determine if the list item is open or closed
 * @param accentColor The color that appears on the left side of the item
 * @param onClick The function to execute when a list item is clicked
 * @param closedContent Composable which shows the closed content of the list item
 * @param openContent Composable that shows the content of the list item when a
 *   user clicks an item
 */
@Composable
fun TimeClockListItem(
    isClosed: Boolean = true,
    accentColor: Color? = null,
    onClick: () -> Unit = {},
    closedContent: @Composable () -> Unit = {
        ListPageListItemClosedContent("Title", "Subtitle")
    },
    openContent: @Composable () -> Unit = {},
    openContentHeight: Dp = 180.dp
) {
    val itemHeight by animateDpAsState(
        targetValue = if (isClosed) TextFieldDefaults.MinHeight else openContentHeight
    )
    Box(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .height(itemHeight)
    ) {
        Row() {
            if (accentColor != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(5.dp)
                        .background(accentColor),
                )
            }
            Crossfade(targetState = isClosed) { closed ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    if (closed) {
                        closedContent()
                    } else {
                        openContent()
                    }
                }
            }
        }
    }
}
