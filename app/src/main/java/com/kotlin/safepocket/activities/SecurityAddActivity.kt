package com.kotlin.safepocket.activities

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.kotlin.safepocket.R
import com.kotlin.safepocket.adapters.SecurityCategoryAdapter
import com.kotlin.safepocket.databinding.ActivitySecurityAddBinding
import com.kotlin.safepocket.helper.SafePocketHelper
import com.kotlin.safepocket.helper.database
import com.kotlin.safepocket.models.Category
import com.kotlin.safepocket.models.Security
import com.kotlin.safepocket.utils.PasswordStrengthUtils
import kotlinx.android.synthetic.main.activity_security_add.*

class SecurityAddActivity : SimpleActivity() {

    private var mBinding: ActivitySecurityAddBinding? = null
    private var mTempStrengthLevel = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_add)

        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_security_add)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            title = getString(R.string.security_add_title)
            setDisplayHomeAsUpEnabled(true)
        }

        bindEvent()
        initCategorySpinner()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> this@SecurityAddActivity.onBackPressed()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindEvent() {

        mBinding?.let { binding ->
            binding.save.setOnClickListener { _ ->
                val security: Security? = SafePocketHelper.getSecurityFromLayout(binding, binding.securityCategory.selectedItem as Category, mTempStrengthLevel)
                security?.let {
                    this@SecurityAddActivity.database().insertSecurity(it)
                    this@SecurityAddActivity.onBackPressed()
                }
            }

            binding.securityPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val level = PasswordStrengthUtils.getScore(s.toString())
                    if (level != mTempStrengthLevel) {
                        mTempStrengthLevel = level
                        SafePocketHelper.setPasswordStrengthLevel(
                                this@SecurityAddActivity,
                                mTempStrengthLevel,
                                binding.included.level1,
                                binding.included.level2,
                                binding.included.level3,
                                binding.included.level4,
                                binding.included.level5
                        )
                    }
                }
            })
        }
    }

    private fun changeCategoryContainer() {

        mBinding?.run {
            val item: Category = securityCategory.selectedItem as Category
            when (item.index) {
                0 -> {
                    accountContainer.visibility = View.VISIBLE
                    creditCardContainer.visibility = View.GONE
                }
                1 -> {
                    accountContainer.visibility = View.GONE
                    creditCardContainer.visibility = View.VISIBLE
                }
                else -> {
                    accountContainer.visibility = View.GONE
                    creditCardContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun initCategorySpinner() {

        val adapter: ArrayAdapter<Category> = SecurityCategoryAdapter(this@SecurityAddActivity, R.layout.item_category, SafePocketHelper.getCategories(this@SecurityAddActivity))
        mBinding?.run {
            securityCategory.adapter = adapter
            securityCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    changeCategoryContainer()
                }
            }
        }
    }
}

