package com.kotlin.safepocket.extensions

import android.app.Activity
import android.content.Intent
import io.github.hanjoongcho.commons.extensions.baseConfig
import io.github.hanjoongcho.commons.helpers.TransitionHelper
import com.kotlin.safepocket.activities.PatternLockActivity

fun Activity.pausePatternLock() {
    baseConfig.aafPatternLockPauseMillis = System.currentTimeMillis()
}

fun Activity.resumePatternLock() {
    val pauseMillis = baseConfig.aafPatternLockPauseMillis
    if (pauseMillis != 0L) {
        if (System.currentTimeMillis() - pauseMillis > 1000) {
            val intent = Intent(this, PatternLockActivity::class.java).apply {
                putExtra(PatternLockActivity.MODE, PatternLockActivity.UNLOCK_RESUME)
            }
            TransitionHelper.startActivityWithTransition(this, intent)
        }
    }
}