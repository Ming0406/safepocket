package com.kotlin.safepocket.activities

import android.test.ActivityInstrumentationTestCase2
import android.test.UiThreadTest
import android.view.View
import com.kotlin.safepocket.R
import org.junit.Before
import org.junit.Test

/**
 * test security selection activity elements
 */
@Suppress("DEPRECATION")
class SecuritySelectionActivityUITest: ActivityInstrumentationTestCase2<SecuritySelectionActivity>(
    "com.kotlin.safepocket", SecuritySelectionActivity::class.java) {

    var addButton: View? = null
    var searchButton: View? = null
    @Before
    override fun setUp() {
        addButton = activity.findViewById(R.id.add)
        searchButton = activity.findViewById(R.id.search)
    }

    @Test
    fun onCreate() {
        print("test onCreate running")
    }

    @UiThreadTest
    fun testAddButton() {
        val res = checkNotNull(addButton).performClick()

        // press back for search button test
        activity.onBackPressed()
        assertEquals(true, res)
    }

    @UiThreadTest
    fun testSearchButton() {
        val res = checkNotNull(searchButton).performClick()
        assertEquals(true, res)
    }






}