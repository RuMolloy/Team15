package com.team15app.team15.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.team15app.team15.R
import com.team15app.team15.adapters.MatchInfoAdapter

class MatchInfoDialogFragment(): DialogFragment(),
    MatchInfoFragment.MatchInfoFragmentListener,
    PlayerJerseyFragment.Temp {

    lateinit var customView: View
    private lateinit var imm: InputMethodManager
    private lateinit var tabLayout: TabLayout
    private lateinit var onFinishEditDialogListener: OnFinishEditDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return customView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = this.arguments

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(R.string.set_match_details)

        val inflater = LayoutInflater.from(context)
        customView = inflater.inflate(R.layout.dialog_edit_match_info, null)
        tabLayout = customView.findViewById(R.id.tl_match_info)
        val viewPagerTabs = customView.findViewById<ViewPager>(R.id.vp_tabs)
        val matchInfoAdapter = MatchInfoAdapter(childFragmentManager, tabLayout.tabCount, bundle)
        viewPagerTabs.adapter = matchInfoAdapter
        viewPagerTabs.offscreenPageLimit = 3
        viewPagerTabs.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewPagerTabs.currentItem = tabLayout.selectedTabPosition
            }
        })

        alertDialogBuilder.setView(customView)
        alertDialogBuilder.setPositiveButton(R.string.ok) { _, _ ->
            onFinishEditDialogListener.onFinishEditDialog(getMatchInfoBundle())
        }
        alertDialogBuilder.setNegativeButton(R.string.cancel) { _, _ ->

        }

        return alertDialogBuilder.create()
    }

    override fun onJerseyPressed(source: String) {
        var tabNum = 0
        tabNum = if (source == getString(R.string.goalkeeper)) 1 else 2
        tabLayout.getTabAt(tabNum)!!.select();
    }

    override fun onJerseyTemp(resourceName: String) {
        for(frag in childFragmentManager.fragments){
            if (frag is MatchInfoFragment){
                frag.updateJersey(tabLayout.selectedTabPosition, resourceName)
            }
            break
        }
        tabLayout.getTabAt(0)!!.select();
    }

    interface OnFinishEditDialog {
        fun onFinishEditDialog(bundle: Bundle)
    }

    fun setOnFinishEditDialogListener(listener: OnFinishEditDialog) {
        onFinishEditDialogListener = listener
    }

    private fun getMatchInfoBundle(): Bundle{
        val bundle = Bundle()
        for(frag in childFragmentManager.fragments){
            if (frag is MatchInfoFragment){
                bundle.putString(resources.getString(R.string.default_team_name_a), frag.etTeamNameA.text.toString())
                bundle.putString(resources.getString(R.string.default_team_name_b), frag.etTeamNameB.text.toString())
                bundle.putString(resources.getString(R.string.default_match_info), frag.etMatchInfo.text.toString())
                bundle.putString(resources.getString(R.string.goalkeeper), frag.ivGoalkeeper.tag.toString())
                bundle.putString(resources.getString(R.string.outfielder), frag.ivOutfielder.tag.toString())
                break
            }
        }
        return bundle
    }
}
