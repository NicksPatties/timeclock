package com.nickspatties.timeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.nickspatties.timeclock.ui.pages.AnalysisPage
import com.nickspatties.timeclock.ui.pages.ClockPage
import com.nickspatties.timeclock.ui.pages.ListPage
import com.nickspatties.timeclock.ui.theme.TimeClockTheme
import com.nickspatties.timeclock.util.Chronometer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // create a chronometer that will survive state changes
        val chronometer = Chronometer()
        setContent {
            TimeClockApp(chronometer = chronometer)
        }
    }
}

/**
 * intialPage - index of the initial page to test. 0 for clock page,
 * 1 for ListPage, 2 for AnalysisPage
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TimeClockApp(initialPage: Int = 0, chronometer: Chronometer) {
    TimeClockTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            val pagerState = rememberPagerState(
                pageCount = 3,
                initialPage = initialPage // temporary, but just for testing
            )

            HorizontalPager(
                state = pagerState
            ) { page ->
                PageSelector(page, chronometer)
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        // place x at half of the screen size minus half the width of
                        // the horizontal pager indicator
                        val xPos = (constraints.maxWidth / 2) - (placeable.width / 2)

                        // place y at the bottom of the screen, minus some padding
                        val yPos = constraints.maxHeight - 100
                        placeable.placeRelative(xPos, yPos)
                    }
                }
            )
        }
    }
}

@Composable
fun PageSelector(pageId: Int, chrono: Chronometer) {
    when(pageId) {
        0 -> ClockPage(chrono)
        1 -> ListPage()
        2 -> AnalysisPage()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun FirstPagePreview() {
//    TimeClockApp()
//}
//
//@Preview(showBackground = true)
//@Composable
//fun SecondPagePreview() {
//    TimeClockApp(1)
//}
//
//@Preview(showBackground = true)
//@Composable
//fun ThirdPagePreview() {
//    TimeClockApp(2)
//}