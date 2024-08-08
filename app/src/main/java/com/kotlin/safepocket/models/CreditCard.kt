package com.kotlin.safepocket.models

import io.realm.RealmObject

open class CreditCard(
        var serial: String = "",
        var expireDate: String = "",
        var cardValidationCode: String = "",
        var expireDateMillis: Long = 0
) : RealmObject()