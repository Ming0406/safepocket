package com.kotlin.safepocket.helper

import android.support.multidex.MultiDexApplication
import io.realm.Realm

class SafePocketApplication : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}
