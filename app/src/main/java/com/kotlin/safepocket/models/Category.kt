package com.kotlin.safepocket.models

import io.realm.RealmObject

open class Category(
        var index: Int = -1,
        var name: String = "",
        var resourceName: String = ""
) : RealmObject()