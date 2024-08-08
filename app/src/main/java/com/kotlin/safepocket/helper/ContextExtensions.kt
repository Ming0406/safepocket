package com.kotlin.safepocket.helper

import android.content.Context
import com.kotlin.safepocket.persistence.DatabaseHelper

fun Context.database() = DatabaseHelper.getInstance(this)
