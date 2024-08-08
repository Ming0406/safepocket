package com.kotlin.safepocket.activities

import com.kotlin.safepocket.R
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * test intro activity elements
 */
@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
@LargeTest
class IntroActivityUITest {
    @get:Rule
    val activityRule = ActivityTestRule(IntroActivity::class.java)

    @Test
    fun testIntroActivityCompanyName() {
        onView(withId(R.id.appName)).check(matches(withText("Safe Pocket")))
    }
}
