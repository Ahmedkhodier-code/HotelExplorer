package com.khodier.hotelexplorer

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.khodier.hotelexplorer.core.designsystem.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HotelBookingFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext


    @Test
    fun testBookingFlow() {
        val reserveNowText = context.getString(R.string.reserve_now)
        val confirmAndReserveText = context.getString(R.string.confirm_and_reserve)
        val bookingConfirmedText = context.getString(R.string.booking_confirmed)
        val backToHotelsText = context.getString(R.string.back_to_hotels)

        // 1. Wait for hotels to load and click on "Grand Nile Hotel"
        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodes(hasText("Grand Nile", substring = true)).fetchSemanticsNodes().isNotEmpty()
        }
        // Use onFirst() to avoid ambiguity if contentDescription matches text
        composeTestRule.onAllNodes(hasText("Grand Nile", substring = true)).onFirst().performClick()

        // 2. Verify Details screen and click Reserve
        composeTestRule.onNodeWithText(reserveNowText).performClick()

        // 3. Verify Booking screen and confirm
        composeTestRule.onNodeWithText(confirmAndReserveText).performScrollTo().performClick()

        // 4. Verify Success screen
        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodes(hasText(bookingConfirmedText)).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(backToHotelsText).performClick()
    }
}

