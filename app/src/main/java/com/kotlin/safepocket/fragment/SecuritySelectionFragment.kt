package com.kotlin.safepocket.fragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.simplemobiletools.commons.extensions.onGlobalLayout
import com.simplemobiletools.commons.views.MyRecyclerView
import io.github.hanjoongcho.commons.helpers.TransitionHelper
import com.kotlin.safepocket.R
import com.kotlin.safepocket.activities.SecurityAddActivity
import com.kotlin.safepocket.activities.SecurityDetailActivity
import com.kotlin.safepocket.adapters.SecurityAdapter
import com.kotlin.safepocket.extensions.config
import com.kotlin.safepocket.helper.beforeDrawing
import com.kotlin.safepocket.helper.database
import com.kotlin.safepocket.models.Security
import kotlinx.android.synthetic.main.fragment_securities.*

class SecuritySelectionFragment : Fragment(), SecurityAdapter.ItemOperationsListener {
    private var mKeyword: String? = ""
    private var storedItems = mutableListOf<Security>()

    private val adapter: SecurityAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        activity?.let { activity ->
            SecurityAdapter(activity,
                    this@SecuritySelectionFragment,
                    AdapterView.OnItemClickListener { _, v, position, _ ->
                        adapter?.getItem(position)?.let {
                            startAccountDetailActivityWithTransition(activity,
                                    v.findViewById(R.id.category_icon), it)
                        }
                    })
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_securities, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        setUpGrid(securities)
        add.setOnClickListener {
            activity?.let {
                TransitionHelper.startActivityWithTransition(it, SecurityAddActivity::class.java)    
            }
        }

       
        
//        items_swipe_refresh.setOnRefreshListener { refreshItems() }
    }

    override fun onResume() {
        loadingProgress.visibility = View.VISIBLE
        super.onResume()
        activity?.let { activity ->
            Thread {
                if (activity.database().countSecurity() == 0) {
                    Handler(Looper.getMainLooper()).post {
                        securities.visibility = View.INVISIBLE
                        loadingProgressMessage.visibility = View.VISIBLE
                    }
                    activity.database().initDatabase()
                }
                Handler(Looper.getMainLooper()).post {
                    filteringItems(mKeyword ?: "")
                    securities.visibility = View.VISIBLE
                    loadingProgress.visibility = View.INVISIBLE
                    loadingProgressMessage.visibility = View.INVISIBLE

                    items_fastscroller.updatePrimaryColor()
                    items_fastscroller.updateBubbleColors()
                    items_fastscroller.allowBubbleDisplay = context!!.config.showInfoBubble
                }
            }.start()
        }
    }

    override fun refreshItems() {
//        openPath(currentPath)
//        items_swipe_refresh?.isRefreshing = false
    }
    
    fun filteringItems(keyword: String) {
        adapter?.let {
            mKeyword = keyword
            storedItems.clear()
            storedItems.addAll(it.selectAccounts(keyword))
            adapter?.notifyDataSetChanged()

            items_fastscroller.setViews(securities, null) {
                val item = storedItems.getOrNull(it)
                items_fastscroller.updateBubbleText(item?.getBubbleText() ?: "")
            }
            securities.onGlobalLayout {
                items_fastscroller.setScrollTo(securities.computeVerticalScrollOffset())
            }
        }
    }
    
    @SuppressLint("NewApi")
    private fun setUpGrid(securitiesView: MyRecyclerView) {
        with(securitiesView) {
//            addItemDecoration(OffsetDecoration(context.resources.getDimensionPixelSize(R.dimen.spacing_nano)))
            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            AppCompatResources.getDrawable(context, R.drawable.divider_transparent)?.let {
                dividerItemDecoration.setDrawable(it)
            }
            securitiesView.addItemDecoration(dividerItemDecoration) 
            adapter = this@SecuritySelectionFragment.adapter
            activity?.let {
                beforeDrawing { it.supportStartPostponedEnterTransition() }    
            }

//            items_fastscroller.allowBubbleDisplay = context!!.config.showInfoBubble
//            items_fastscroller.setViews(securitiesView, null) {
//                val item = storedItems.getOrNull(it)
//                items_fastscroller.updateBubbleText(item?.getBubbleText() ?: "")
//            }
//            securities.onGlobalLayout {
//                items_fastscroller.setScrollTo(securities.computeVerticalScrollOffset())
//            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startAccountDetailActivityWithTransition(activity: Activity, view: View,
                                                         security: Security) {

        val animationBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                *TransitionHelper.createSafeTransitionParticipants(activity, false)
        ).toBundle()

        // Start the activity with the participants, animating from one to the other.
        val startIntent = SecurityDetailActivity.getStartIntent(activity, security)
        startIntent.putExtra("sequence", security.sequence)
        ActivityCompat.startActivity(activity, startIntent, animationBundle)
    }
}