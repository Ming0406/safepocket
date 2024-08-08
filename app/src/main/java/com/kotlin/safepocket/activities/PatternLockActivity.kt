package com.kotlin.safepocket.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.andrognito.patternlockview.utils.ResourceUtils
import com.andrognito.rxpatternlockview.RxPatternLockView
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent
import io.github.hanjoongcho.commons.extensions.baseConfig
import io.github.hanjoongcho.commons.helpers.TransitionHelper
import com.kotlin.safepocket.R
import com.kotlin.safepocket.helper.database
import com.kotlin.safepocket.utils.AesUtils
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_pattern_lock.*

class PatternLockActivity : AppCompatActivity() {

    private var mMode: Int? = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_pattern_lock)

        // iconView init
        val iconView: ImageView = findViewById(R.id.profile_image)
        val authorView: TextView = findViewById(R.id.author_info)
        var authorTextVisiableFlag: Int = 1
        iconView.setOnClickListener {
            if (authorTextVisiableFlag == 1) {
                authorView.visibility = View.VISIBLE
                authorTextVisiableFlag = 0
            }
            else {
                authorView.visibility = View.GONE
                authorTextVisiableFlag = 1
            }
        }

        mMode = intent.getIntExtra(MODE, -1)
        when (mMode) {
            UNLOCK, UNLOCK_RESUME -> guide_message.text = getString(R.string.insert_pattern)
            SETTING_LOCK -> guide_message.text = getString(R.string.insert_pattern)
            VERIFY -> guide_message.text = getString(R.string.repeat_pattern)
        }

        with(patterLockView) {
            dotCount = 3
            dotNormalSize = ResourceUtils.getDimensionInPx(this@PatternLockActivity, R.dimen.pattern_lock_dot_size).toInt()
            dotSelectedSize = ResourceUtils.getDimensionInPx(this@PatternLockActivity, R.dimen.pattern_lock_dot_selected_size).toInt()
            pathWidth = ResourceUtils.getDimensionInPx(this@PatternLockActivity, R.dimen.pattern_lock_path_width).toInt()
            isAspectRatioEnabled = true
            aspectRatio = PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS
            setViewMode(PatternLockView.PatternViewMode.CORRECT)
            dotAnimationDuration = 150
            pathEndAnimationDuration = 100
            correctStateColor = ResourceUtils.getColor(this@PatternLockActivity, R.color.white)
            isInStealthMode = false
            isTactileFeedbackEnabled = true
            isInputEnabled = true
            addPatternLockListener(mPatternLockViewListener)
        }

        RxPatternLockView.patternComplete(patterLockView).subscribe({ patternLockCompleteEvent ->
            Log.d(javaClass.name, "Complete: " + patternLockCompleteEvent.pattern.toString())
            val savedPattern = baseConfig.aafPatternLockSaved
            when (mMode) {
                UNLOCK -> {
                    if (savedPattern == patternLockCompleteEvent.pattern.toString()) {
                        TransitionHelper.startActivityWithTransition(this@PatternLockActivity, SecuritySelectionActivity::class.java)
                        finish()
                    } else {
                        patterLockView.clearPattern()
                        val unlockBuilder: AlertDialog.Builder = AlertDialog.Builder(this@PatternLockActivity).apply {
                            setMessage(getString(R.string.wrong_pattern))
                            setPositiveButton("OK", ({ _, _ ->
                                ActivityCompat.finishAffinity(this@PatternLockActivity)
                            }))
                            setCancelable(false)
                        }
                        unlockBuilder.create().show()
                    }
                }
                SETTING_LOCK -> {
                    val intent = Intent(this, PatternLockActivity::class.java)
                    intent.putExtra(PatternLockActivity.MODE, PatternLockActivity.VERIFY)
                    intent.putExtra(PatternLockActivity.REQUEST_PATTERN, patternLockCompleteEvent.pattern.toString())
                    TransitionHelper.startActivityWithTransition(this@PatternLockActivity, intent)
                    finish()
                }
                VERIFY -> {
                    if (intent.getStringExtra(PatternLockActivity.REQUEST_PATTERN) == patternLockCompleteEvent.pattern.toString()) {
                        val previousPattern = baseConfig.aafPatternLockSaved
                        baseConfig.aafPatternLockSaved = patternLockCompleteEvent.pattern.toString()
                        if (this@PatternLockActivity.database().countSecurity() < 1) {
                            TransitionHelper.startActivityWithTransition(this@PatternLockActivity, SecuritySelectionActivity::class.java)
                            finish()
                        } else {
                            progressBar.visibility = View.VISIBLE
                            guide_message.text = getString(R.string.pattern_lock_progress_message)
                            patterLockView.clearPattern()
                            Thread(Runnable {
                                val listSecurity = this@PatternLockActivity.database().selectSecurityAll()
                                this@PatternLockActivity.database().beginTransaction()
                                listSecurity.map {

                                    val previousCipherPassword = it.password
                                    val previousPlainPassword = AesUtils.decryptPassword(this@PatternLockActivity, previousCipherPassword, previousPattern)
                                    val currentCipherPassword = AesUtils.encryptPassword(this@PatternLockActivity, previousPlainPassword)
                                    it.password = currentCipherPassword

                                    it.creditCard?.let { cc ->
                                        val previousCipherSerial = cc.serial
                                        val previousCipherExpireDate = cc.expireDate
                                        val previousCipherCardValidationCode = cc.cardValidationCode

                                        val previousPlainSerial = AesUtils.decryptPassword(this@PatternLockActivity, previousCipherSerial, previousPattern)
                                        val previousPlainExpireDate = AesUtils.decryptPassword(this@PatternLockActivity, previousCipherExpireDate, previousPattern)
                                        val previousPlainCardValidationCode = AesUtils.decryptPassword(this@PatternLockActivity, previousCipherCardValidationCode, previousPattern)

                                        val currentCipherSerial = AesUtils.encryptPassword(this@PatternLockActivity, previousPlainSerial)
                                        val currentCipherExpireDate = AesUtils.encryptPassword(this@PatternLockActivity, previousPlainExpireDate)
                                        val currentCipherCardValidationCode = AesUtils.encryptPassword(this@PatternLockActivity, previousPlainCardValidationCode)

                                        cc.serial = currentCipherSerial
                                        cc.expireDate = currentCipherExpireDate
                                        cc.cardValidationCode = currentCipherCardValidationCode
                                    }
                                }
                                this@PatternLockActivity.database().commitTransaction()
                                Handler(Looper.getMainLooper()).post({
                                    progressBar.visibility = View.INVISIBLE
                                    finish()
                                    overridePendingTransition(0, 0)
                                })
                            }).start()
                        }
                    } else {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@PatternLockActivity)
                        builder.setMessage(getString(R.string.wrong_pattern))
                        builder.setPositiveButton("OK", ({ _, _ ->
                            intent.putExtra(PatternLockActivity.MODE, PatternLockActivity.SETTING_LOCK)
                            TransitionHelper.startActivityWithTransition(this@PatternLockActivity, intent)
                            finish()
                        }))
                        builder.create().show()
                    }
                }
                UNLOCK_RESUME -> {
                    if (savedPattern == patternLockCompleteEvent.pattern.toString()) {
                        finish()
                        overridePendingTransition(0, 0)
                    } else {
                        patterLockView.clearPattern()
                        val unlockBuilder: AlertDialog.Builder = AlertDialog.Builder(this@PatternLockActivity).apply {
                            setMessage(getString(R.string.wrong_pattern))
                            setPositiveButton("OK", ({ _, _ ->
                                ActivityCompat.finishAffinity(this@PatternLockActivity)
                            }))
                            setCancelable(false)
                        }
                        unlockBuilder.create().show()
                    }
                }
            }
        })

        RxPatternLockView.patternChanges(patterLockView)
                .subscribe(object : Consumer<PatternLockCompoundEvent> {
                    @Throws(Exception::class)
                    override fun accept(event: PatternLockCompoundEvent) {
                        when (event.eventType) {
                            PatternLockCompoundEvent.EventType.PATTERN_STARTED -> Log.d(javaClass.name, "Pattern drawing started")
                            PatternLockCompoundEvent.EventType.PATTERN_PROGRESS -> Log.d(javaClass.name, "Pattern progress: " + PatternLockUtils.patternToString(patterLockView, event.pattern))
                            PatternLockCompoundEvent.EventType.PATTERN_COMPLETE -> Log.d(javaClass.name, "Pattern complete: " + PatternLockUtils.patternToString(patterLockView, event.pattern))
                            PatternLockCompoundEvent.EventType.PATTERN_CLEARED ->  Log.d(javaClass.name, "Pattern has been cleared")
                        }
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        main_holder.setBackgroundColor(ColorUtils.setAlphaComponent(baseConfig.primaryColor, 255))
    }

    override fun onPause() {
        super.onPause()
        baseConfig.aafPatternLockPauseMillis = System.currentTimeMillis()
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this@PatternLockActivity)
    }

    private val mPatternLockViewListener = object : PatternLockViewListener {

        override fun onStarted() {
            Log.d(javaClass.name, "Pattern drawing started")
        }

        override fun onProgress(progressPattern: List<PatternLockView.Dot>) {
            Log.d(javaClass.name, "Pattern progress: " + PatternLockUtils.patternToString(patterLockView, progressPattern))
        }

        override fun onComplete(pattern: List<PatternLockView.Dot>) {
            Log.d(javaClass.name, "Pattern complete: " + PatternLockUtils.patternToString(patterLockView, pattern))
        }

        override fun onCleared() {
            Log.d(javaClass.name, "Pattern has been cleared")
        }
    }

    companion object {
        const val MODE = "mMode"
        const val REQUEST_PATTERN = "request_pattern"
        const val UNLOCK = 1
        const val SETTING_LOCK = 2
        const val VERIFY = 3
        const val UNLOCK_RESUME = 4
    }
}
