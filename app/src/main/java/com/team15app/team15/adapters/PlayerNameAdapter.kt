package com.team15app.team15.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
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