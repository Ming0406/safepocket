package com.kotlin.safepocket.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import io.github.hanjoongcho.commons.extensions.baseConfig

import com.kotlin.safepocket.R
import com.kotlin.safepocket.databinding.ActivitySecuritySelectionBinding
import com.kotlin.safepocket.fragment.SecuritySelectionFragment
import com.kotlin.safepocket.helper.findFragmentById
import com.kotlin.safepocket.helper.replaceFragment
import kotlinx.android.synthetic.main.activity_security_selection.*
import kotlinx.android.synthetic.main.fragment_securities.*

class SecuritySelectionActivity : SimpleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil
                .setContentView<ActivitySecuritySelectionBinding>(this,
                        R.layout.activity_security_selection)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = getString(R.string.security_selection_title)
        }

        attachCategoryGridFragment()
        supportPostponeEnterTransition()

        toggleToolBar.setOnClickListener({
            toolbar.visibility = View.VISIBLE
            searchViewContainer.visibility = View.GONE
            val focusView = this.currentFocus
            if (focusView != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                supportActionBar?.run {
                    subtitle = searchView.text
                }
            }
        })

        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filteringItems(p0.toString())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.security_selection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                toolbar.visibility = View.GONE
                searchViewContainer.visibility = View.VISIBLE
                searchView.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT)
                scrollTop()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@SecuritySelectionActivity).apply {
            setMessage(getString(R.string.security_selection_back_press_confirm))
            setPositiveButton(getString(R.string.ok), ({ _, _ ->
                ActivityCompat.finishAffinity(this@SecuritySelectionActivity)
            }))
            setNegativeButton(getString(R.string.cancel), null)
        }
        builder.create().show()
    }
    
    private fun scrollTop() {
        val fragment = findFragmentById(R.id.category_container) ?: SecuritySelectionFragment()
        if (fragment is SecuritySelectionFragment) {
            fragment.securities?.smoothScrollToPosition(0)
        }
    }
    
    private fun filteringItems(keyword: String) {
        val fragment = findFragmentById(R.id.category_container) ?: SecuritySelectionFragment()
        if (fragment is SecuritySelectionFragment) {
            fragment.filteringItems(keyword)
        }
    }

    private fun attachCategoryGridFragment() {
        replaceFragment(R.id.category_container,
                findFragmentById(R.id.category_container) ?: SecuritySelectionFragment())
    }

    companion object {
        const val TAG = "input test"
        fun start(activity: Activity) {
            ActivityCompat.startActivity(activity,
                    Intent(activity, SecuritySelectionActivity::class.java),
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle())
        }
    }
}