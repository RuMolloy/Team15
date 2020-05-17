package com.team15app.team15.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.team15app.team15.Player
import com.team15app.team15.dialogs.PlayerFragment
import java.util.*

class PlayerNameAdapter(fragmentManager: FragmentManager, private val players: TreeMap<Int, Player>) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return PlayerFragment.newInstance(players, players[position])
    }

    override fun getCount(): Int {
        return players.size
    }
}