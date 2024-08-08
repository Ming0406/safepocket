package com.kotlin.safepocket.persistence

import android.content.Context
import com.kotlin.safepocket.utils.AesUtils
import io.realm.DynamicRealm
import io.realm.RealmMigration

class DatabaseMigration constructor(
        val context: Context
) : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {

        val schema = realm.schema
        var version = oldVersion.toInt()

        if (version == 1) {
            val diarySchema = schema.get("CreditCard")
            diarySchema
                    .addField("cardValidationCode", String::class.java)
                    .transform {
                        it.set("cardValidationCode", "")
                    }
                .transform {
                    val cipherSerial = AesUtils.encryptPassword(context, it.getString("serial"))
                    it.set("serial", cipherSerial)

                }
                .transform {
                    val expireDate = AesUtils.encryptPassword(context, it.getString("expireDate"))
                    it.set("expireDate", expireDate)
                }
            version++
        }
    }
}