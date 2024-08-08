package com.kotlin.safepocket.models

import io.realm.RealmObject

open class Account(
        var id: String = ""
) : RealmObject()