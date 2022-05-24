package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import com.nickspatties.timeclock.R
import com.nickspatties.timeclock.data.TimeClockEventDao

class TimeClockViewModel (
    database: TimeClockEventDao,
    application: Application
): AndroidViewModel(application) {

    var timeClockEvents = database.getAllEvents()

    // task names used for autofill dropdown
    val autofillTaskNames = Transformations.map(timeClockEvents) { events ->
        events.map {
            it.name
        }.toSet()
    }

    val clockPage = ClockPageViewModel(
        database,
        application
    )

    val listPage = ListPageViewModel(
        database,
        timeClockEvents,
        application
    )

    val analysisPage = AnalysisPageViewModel(
        timeClockEvents,
        application
    )
}

sealed class Screen(
    @StringRes val routeResourceId: Int,
    @StringRes val labelResourceId: Int,
    @DrawableRes val iconResourceId: Int
) {
    object Clock :
        Screen(R.string.route_clock, R.string.label_clock, R.drawable.ic_baseline_clock_24)

    object List :
        Screen(R.string.route_list, R.string.label_list, R.drawable.ic_baseline_list_24)

    object Metrics :
        Screen(R.string.route_metrics, R.string.label_metrics, R.drawable.ic_baseline_pie_chart_24)
}
