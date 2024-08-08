package com.kotlin.safepocket.activities

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import com.kotlin.safepocket.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * test PatternLock Activity elements
 */
@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
@LargeTest
class PatternLockActivityUITest {
    @get:Rule
    val activityRule = ActivityTestRule(PatternLockActivity::class.java)

    @Test
    fun testIntroActivityCompanyName() {
        // wait for PatternLockActivity start
        Thread.sleep(2000)

        // find profile_image and perform click
        onView(withId(R.id.profile_image)).perform(click())
//
        // find author_name and check results
        val authorNameExpected: String = R.string.author_name.toString()
        onView(withId(R.id.author_info)).check(matches(withText(authorNameExpected)))
    }
}