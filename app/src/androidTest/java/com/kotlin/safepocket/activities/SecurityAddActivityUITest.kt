package com.kotlin.safepocket.activities

import android.test.ActivityInstrumentationTestCase2
import android.test.UiThreadTest
import android.view.View
import android.widget.TextView
import com.kotlin.safepocket.R
import junit.framework.Assert
import org.junit.Before
import org.junit.Test

/**
 * test security add activity elements
 */
@Suppress("DEPRECATION")
class SecurityAddActivityUITest: ActivityInstrumentationTestCase2<SecurityAddActivity>(
    "com.kotlin.safepocket", SecurityAddActivity::class.java) {

    var addButton: View? = null
    var managementText: TextView? = null
    var passwordText: TextView? = null

    @Before
    override fun setUp() {
        addButton = activity.findViewById(R.id.save)
        managementText = activity.findViewById(R.id.security_title)
        passwordText = activity.findViewById(R.id.security_password)
    }

    @Test
    fun onCreate() {
        print("test onCreate running")
    }

    @UiThreadTest
    fun testSaveButton() {
        val res = checkNotNull(addButton).performClick()
        Assert.assertEquals(true, res)
    }

    @UiThreadTest
    fun testManagementNameText() {
        val expectedStr = "management"
        checkNotNull(managementText).setText(expectedStr)
        assertEquals(expectedStr, checkNotNull(managementText).text.toString())
    }

    @UiThreadTest
    fun testPasswordText() {
        val expectedStr = "password"
        checkNotNull(passwordText).setText(expectedStr)
        assertEquals(expectedStr, checkNotNull(managementText).text.toString())
    }


}