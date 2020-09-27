package com.team15app.team15.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.team15app.team15.dialogs.MatchInfoFragment
import com.team15app.team15.dialogs.PlayerJerseyFragment

class MatchInfoAdapter(fragmentManager: FragmentManager,
                       private val numTabs: Int,
                       private val argsMatchInfo: Bundle?) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return MatchInfoFragment.newInstance(argsMatchInfo)
            1 -> return PlayerJerseyFragment()
            2 -> return PlayerJerseyFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int {
        return numTabs
    }
}