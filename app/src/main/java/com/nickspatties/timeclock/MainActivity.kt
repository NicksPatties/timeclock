package com.nickspatties.timeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.nickspatties.timeclock.ui.theme.TimeClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeClockApp()
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TimeClockApp(initialPage: Int = 0) {
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
                PageSelector(page)
            }

            HorizontalPagerIndicator(
                pagerState = pagerState
            )
        }
    }
}

@Composable
fun PageSelector(pageId: Int) {
    when(pageId) {
        0 -> SamplePage("One")
        1 -> SamplePage("Two")
        2 -> SamplePage("Three")
    }
}

@Composable
fun SamplePage(name: String) {
    Scaffold() {
        Greeting(name = name)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun FirstPagePreview() {
    TimeClockApp()
}

@Preview(showBackground = true)
@Composable
fun SecondPagePreview() {
    TimeClockApp(1)
}

@Preview(showBackground = true)
@Composable
fun ThirdPagePreview() {
    TimeClockApp(2)
}