package com.kotlin.safepocket.extensions

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.EditText
import com.simplemobiletools.commons.views.MyTextView
import com.kotlin.safepocket.R
import com.kotlin.safepocket.helper.Config
import com.kotlin.safepocket.helper.FONT_SIZE_EXTRA_LARGE
import com.kotlin.safepocket.helper.FONT_SIZE_LARGE
import com.kotlin.safepocket.helper.FONT_SIZE_SMALL

fun Context.getTextSize() =
        when (config.fontSize) {
            FONT_SIZE_SMALL -> resources.getDimension(R.dimen.smaller_text_size)
            FONT_SIZE_LARGE -> resources.getDimension(R.dimen.big_text_size)
            FONT_SIZE_EXTRA_LARGE -> resources.getDimension(R.dimen.extra_big_text_size)
            else -> resources.getDimension(R.dimen.bigger_text_size)
        }

val Context.config: Config get() = Config.newInstance(applicationContext)

fun Context.initTextSize(viewGroup: ViewGroup, context: Context) {
    val cnt = viewGroup.childCount
    (0 until cnt)
            .map { viewGroup.getChildAt(it) }
            .forEach {
                when (it) {
                    is MyTextView ->  {
                        when (it.id != R.id.about_copyright) {
                            true -> it.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize())
                            false -> {}
                        }
                    }
                    is EditText ->  it.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize())
                    is ViewGroup -> initTextSize(it, context)
                }
            }
}

