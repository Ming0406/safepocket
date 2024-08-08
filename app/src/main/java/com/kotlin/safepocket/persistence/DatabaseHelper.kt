package com.kotlin.safepocket.persistence

import android.content.Context
import com.kotlin.safepocket.helper.SafePocketHelper
import com.kotlin.safepocket.models.Security
import com.kotlin.safepocket.utils.AesUtils
import io.realm.*
import java.util.*
import kotlin.collections.ArrayList

class DatabaseHelper private constructor(
        val context: Context
) {

    private var realmConfiguration: RealmConfiguration? = null

    private val realmInstance: Realm
        get() {
            if (realmConfiguration == null) {
                realmConfiguration = RealmConfiguration.Builder()
                        .name(DIARY_DB_NAME)
                        .schemaVersion(2)
                        .migration(DatabaseMigration(context))
                        .modules(Realm.getDefaultModule())
                        .build()
            }
            return Realm.getInstance(realmConfiguration)
        }

    fun initDatabase() {
        if (countSecurity() < 1) {
            SafePocketHelper.getSampleSecurities(context).map {
                insertSecurity(it)
            }
        }
    }

    fun countSecurity() = realmInstance.where(Security::class.java).count().toInt()

    fun insertSecurity(security: Security) {

        realmInstance.beginTransaction()
        var sequence = 1
        realmInstance.where(Security::class.java)?.max(Security.PRIMARY_KEY)?.let { max ->
            sequence = max.toInt() + 1
        }
        security.sequence = sequence
        security.password = AesUtils.encryptPassword(context, security.password)
        security.creditCard?.run {
            serial = AesUtils.encryptPassword(context, serial)
            expireDate = AesUtils.encryptPassword(context, expireDate)
            cardValidationCode = AesUtils.encryptPassword(context, cardValidationCode)
        }
        realmInstance.insert(security)
        realmInstance.commitTransaction()
    }

    fun selectSecurityAll(): ArrayList<Security> {
        val realmResults = realmInstance.where(Security::class.java).findAllSorted(SECURITY_TITLE, Sort.ASCENDING)
        val list = ArrayList<Security>()
        list.addAll(realmResults.subList(0, realmResults.size))
        return list
    }

    fun updateSecurity(security: Security) {
        realmInstance.executeTransaction(Realm.Transaction { realm ->
            security.password = AesUtils.encryptPassword(context, security.password)
            security.creditCard?.run {
                serial = AesUtils.encryptPassword(context, serial)
                expireDate = AesUtils.encryptPassword(context, expireDate)
                cardValidationCode = AesUtils.encryptPassword(context, cardValidationCode)
            }
            realm.insertOrUpdate(security)
        })
    }

    fun selectSecurityBy(sequence: Int): Security {
//        val security: Security = realmInstance.where(Security::class.java).equalTo(Security.PRIMARY_KEY, sequence).findFirst()
//        account.password = AesUtils.decryptPassword(context, account.password)
        return realmInstance.where(Security::class.java).equalTo(Security.PRIMARY_KEY, sequence).findFirst()
    }

    fun selectSecuritiesBy(searchKey: String): ArrayList<Security>  {
        val realmResults = realmInstance.where(Security::class.java).beginGroup().contains(SECURITY_TITLE, searchKey, Case.INSENSITIVE).or().contains(SECURITY_SUMMARY, searchKey, Case.INSENSITIVE).endGroup().findAllSorted(SECURITY_TITLE, Sort.ASCENDING)
        val list = ArrayList<Security>()
        list.addAll(realmResults.subList(0, realmResults.size))
        return list
    }

    fun deleteSecurityBy(sequence: Int) {
        beginTransaction()
        realmInstance.where(Security::class.java).equalTo(Security.PRIMARY_KEY, sequence).findFirst()?.let {
            it.deleteFromRealm()
        }
        commitTransaction()
    }

    fun beginTransaction() {
        realmInstance.beginTransaction()
    }

    fun commitTransaction() {
        realmInstance.commitTransaction()
    }

    companion object {

        const val SECURITY_TITLE = "title"
        const val SECURITY_SUMMARY = "summary"
        const val DIARY_DB_NAME = "safe-pocket.realm"

        private var _instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return _instance ?: synchronized(DatabaseHelper::class) {
                DatabaseHelper(context).also { _instance = it }
            }
        }
    }
}