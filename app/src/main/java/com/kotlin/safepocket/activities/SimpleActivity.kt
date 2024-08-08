package com.kotlin.safepocket.activities

import android.view.ViewGroup
import io.github.hanjoongcho.commons.activities.BaseSimpleActivity
import io.github.hanjoongcho.commons.extensions.updateAppViews
import io.github.hanjoongcho.commons.extensions.updateTextColors
import com.kotlin.safepocket.R
import com.kotlin.safepocket.extensions.initTextSize
import com.kotlin.safepocket.extensions.pausePatternLock
import com.kotlin.safepocket.extensions.resumePatternLock
import com.kotlin.safepocket.helper.APP_BACKGROUND_ALPHA

open class SimpleActivity : BaseSimpleActivity() {
    override fun onPause() {
        super.onPause()
        pausePatternLock()
    }

    override fun onResume() {
        isBackgroundColorFromPrimaryColor = true
        super.onResume()
        resumePatternLock()
        initTextSize(findViewById(android.R.id.content), this@SimpleActivity);
        getMainViewGroup()?.let {
            updateTextColors(it)
            updateAppViews(it)    
        }
    }

    override fun getMainViewGroup(): ViewGroup? = findViewById(R.id.main_holder)
    override fun getBackgroundAlpha(): Int = APP_BACKGROUND_ALPHA
}