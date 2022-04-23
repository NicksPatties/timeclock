package com.nickspatties.timeclock.util

import androidx.compose.ui.graphics.Color
import com.nickspatties.timeclock.ui.theme.*

/**
 * Takes a string, and then picks a color based on that string
 */
fun generateColorFromString(s: String): Color {
    val firstLetter = s.slice(IntRange(0,0)).lowercase()
    val length = s.length
    val inFirstThirdOfAlphabet = Regex("[a-j]").matches(firstLetter)
    val inSecondThirdOfAlphabet = Regex("[k-s]").matches(firstLetter)
    return when {
        length >= 15 -> {
            when {
                inFirstThirdOfAlphabet -> Purple700
                inSecondThirdOfAlphabet -> Teal700
                else -> Green700
            }
        }
        length >= 10 -> {
            when {
                inFirstThirdOfAlphabet -> Purple500
                inSecondThirdOfAlphabet -> Teal500
                else -> Green500
            }

        }
        length >= 5  -> {
            when {
                inFirstThirdOfAlphabet -> Purple300
                inSecondThirdOfAlphabet -> Teal300
                else -> Green300
            }
        }
        else -> {
            when {
                inFirstThirdOfAlphabet -> Purple100
                inSecondThirdOfAlphabet -> Teal100
                else -> Green100
            }
        }
    }
}