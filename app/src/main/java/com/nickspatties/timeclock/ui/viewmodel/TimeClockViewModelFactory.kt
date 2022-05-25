package com.nickspatties.timeclock.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nickspatties.timeclock.data.TimeClockEventDao
import com.nickspatties.timeclock.ui.viewmodel.TimeClockViewModel

class TimeClockViewModelFactory(
    private val dataSource: TimeClockEventDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeClockViewModel::class.java)) {
            return TimeClockViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}