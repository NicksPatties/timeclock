package com.nickspatties.timeclock.ui.viewmodel

import com.google.common.truth.Truth.assertThat
import com.nickspatties.timeclock.util.MILLIS_PER_SECOND
import org.junit.Test

class ClockPageViewModelTest {

    @Test
    fun getCountDownSeconds_doesNotReturnNegativeNumber() {
        val pastEndTime = System.currentTimeMillis() - MILLIS_PER_SECOND
        val actualSeconds = getCountDownSeconds(pastEndTime)
        assertThat(actualSeconds).isEqualTo(0)
    }
}