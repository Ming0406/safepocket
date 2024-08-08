package com.kotlin.safepocket.utils

import android.content.Context
import com.tozny.crypto.android.AesCbcWithIntegrity
import com.tozny.crypto.android.AesCbcWithIntegrity.CipherTextIvMac
import com.tozny.crypto.android.AesCbcWithIntegrity.keyString
import io.github.hanjoongcho.commons.extensions.baseConfig
import com.kotlin.safepocket.R

class AesUtils {
    companion object {
        private const val SALT_STRING = "gx5i+G0AdIgGEyRZYWc0OC6sTpKtdKDel0ee5brnUb9cUoE+13T2b9MdEWG0IvfI5W7" +
                "ua3qRdoJak9qCR7sONeeSRddNRObc1aeO8WrGG7uAfUjOUxmEzPjsnhHT1gQr0obqnvh3Gim1mBd0u6qqBgww8VuDBWPAAyhqkbXrB50="

        fun encryptPassword(context: Context, plainText: String): String = when (plainText) {
            "" -> ""
            else -> {
                val savedPattern = context.baseConfig.aafPatternLockSaved
                val key: AesCbcWithIntegrity.SecretKeys = AesCbcWithIntegrity.generateKeyFromPassword(savedPattern, SALT_STRING)

                // The encryption / storage & display:
                val civ = AesCbcWithIntegrity.encrypt(plainText, AesCbcWithIntegrity.keys(keyString(key)))
                civ.toString()
            }
        }

        fun decryptPassword(context: Context, cipherText: String): String = when (cipherText) {
            "" -> ""
            else -> {
                val savedPattern = context.baseConfig.aafPatternLockSaved
                decryptPassword(context, cipherText, savedPattern)
            }
        }

        fun decryptPassword(context: Context, cipherText: String, patternString: String): String = when (cipherText) {
            "" -> ""
            else -> {
                val key: AesCbcWithIntegrity.SecretKeys = AesCbcWithIntegrity.generateKeyFromPassword(patternString, SALT_STRING)
                var plainText:String = ""
                try {
                    val cipherTextIvMac = CipherTextIvMac(cipherText)
                    plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMac, AesCbcWithIntegrity.keys(keyString(key)))
                } catch (e: Exception) {
                    plainText = context.getString(R.string.aes_utils_decrypt_error)
                }
                plainText
            }
        }
    }
}
