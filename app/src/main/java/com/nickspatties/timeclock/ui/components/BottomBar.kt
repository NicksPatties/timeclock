package com.nickspatties.timeclock.ui.components

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.ui.TimeClockViewModel.Companion.clockPath
import com.nickspatties.timeclock.ui.TimeClockViewModel.Companion.listPath
import com.nickspatties.timeclock.ui.TimeClockViewModel.Companion.metricsPath

@Composable
fun BottomBar(
    currPage: String,
    onBottomNavButtonPressed: (String) -> Unit
) {
    BottomNavigation() {
        BottomNavigationItem(
            selected = currPage == clockPath,
            onClick = { onBottomNavButtonPressed(clockPath) },
            label = {
                Text("Clock")
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_clock_24),
                    contentDescription = null
                )
            }
        )
        BottomNavigationItem(
            selected = currPage == listPath,
            onClick = { onBottomNavButtonPressed(listPath) },
            label = {
                Text("List")
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_list_24),
                    contentDescription = null
                )
            }
        )
        BottomNavigationItem(
            selected = currPage == metricsPath,
            onClick = { onBottomNavButtonPressed(metricsPath) },
            label = {
                Text("Metrics")
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_pie_chart_24),
                    contentDescription = null
                )
            }
        )
    }
}
