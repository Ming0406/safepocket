package com.kotlin.safepocket.utils

import com.nulabinc.zxcvbn.Zxcvbn
import me.gosimple.nbvcxz.Nbvcxz
import me.gosimple.nbvcxz.resources.Configuration
import me.gosimple.nbvcxz.resources.ConfigurationBuilder
import me.gosimple.nbvcxz.resources.Dictionary
import me.gosimple.nbvcxz.scoring.TimeEstimate

object PasswordStrengthUtils {

    const val OFFLINE_SIMPLE_LOCK = "OFFLINE_SIMPLE_LOCK"
    const val ONLINE_THROTTLED = "ONLINE_THROTTLED"
    const val ONLINE_UNTHROTTLED = "ONLINE_UNTHROTTLED"
    const val OFFLINE_BCRYPT_14 = "OFFLINE_BCRYPT_14"
    const val OFFLINE_BCRYPT_12 = "OFFLINE_BCRYPT_12"
    const val OFFLINE_BCRYPT_10 = "OFFLINE_BCRYPT_10"
    const val OFFLINE_BCRYPT_5 = "OFFLINE_BCRYPT_5"
    const val OFFLINE_SHA512 = "OFFLINE_SHA512"
    const val OFFLINE_SHA1 = "OFFLINE_SHA1"
    const val OFFLINE_MD5 = "OFFLINE_MD5"

    private val mConfiguration: Configuration by lazy {
        val dictionaryList = mutableListOf<Dictionary>()
        ConfigurationBuilder().
                setGuessTypes(mapOf(
                        OFFLINE_SIMPLE_LOCK to 1L,
                        ONLINE_THROTTLED to 2L,
                        ONLINE_UNTHROTTLED to 100L,
                        OFFLINE_BCRYPT_14 to 125L,
                        OFFLINE_BCRYPT_12 to 500L,
                        OFFLINE_BCRYPT_10 to 2000L,
                        OFFLINE_BCRYPT_5 to 64000L,
                        OFFLINE_SHA512 to 5000000000L,
                        OFFLINE_SHA1 to 37336000000L,
                        OFFLINE_MD5 to 115840000000L
                ))
                .setDictionaries(dictionaryList)
                .createConfiguration()
    }

    private val nbvcxz: Nbvcxz by lazy {
        Nbvcxz(mConfiguration)
    }

    private val zxcvbn: Zxcvbn by lazy {
        Zxcvbn()
    }

    fun getScore(requestPassword: String): Int = zxcvbn.measure(requestPassword).score + 1
}

