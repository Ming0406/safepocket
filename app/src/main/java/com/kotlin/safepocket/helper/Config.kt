package com.kotlin.safepocket.helper

import android.content.Context
import io.github.hanjoongcho.commons.helpers.BaseConfig

class Config(context: Context) : BaseConfig(context) {
    companion object {
        fun newInstance(context: Context) = Config(context)
    }
    
    var fontSize: Int
        get() = prefs.getInt(FONT_SIZE, FONT_SIZE_MEDIUM)
        set(size) = prefs.edit().putInt(FONT_SIZE, size).apply()
}
