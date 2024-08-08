package com.kotlin.safepocket.activities

import android.view.ViewGroup
import io.github.hanjoongcho.commons.activities.BaseCustomizationActivity
import com.kotlin.safepocket.R
import com.kotlin.safepocket.extensions.pausePatternLock
import com.kotlin.safepocket.extensions.resumePatternLock
import com.kotlin.safepocket.helper.APP_BACKGROUND_ALPHA

class CustomizationActivity : BaseCustomizationActivity() {
    override fun onPause() {
        super.onPause()
        pausePatternLock()
    }

    override fun onResume() {
        isBackgroundColorFromPrimaryColor = true
        super.onResume()
        resumePatternLock()
    }

    override fun getMainViewGroup(): ViewGroup? = findViewById(R.id.main_holder)
    override fun getBackgroundAlpha(): Int = APP_BACKGROUND_ALPHA
}