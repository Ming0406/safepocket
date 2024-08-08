package com.kotlin.safepocket.helper

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

fun FragmentActivity.replaceFragment(@IdRes id: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(id, fragment).commit()
}

fun FragmentActivity.findFragmentByTag(tag: String): Fragment? = supportFragmentManager.findFragmentByTag(tag)

fun FragmentActivity.findFragmentById(tag: Int): Fragment? = supportFragmentManager.findFragmentById(tag)
