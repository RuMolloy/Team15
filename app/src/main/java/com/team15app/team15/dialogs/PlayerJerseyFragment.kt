package com.team15app.team15.dialogs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.team15app.team15.Player
import com.team15app.team15.R
import com.team15app.team15.adapters.PlayerJerseyPagerAdapter
import java.util.*

class PlayerJerseyFragment(): ViewPager.OnPageChangeListener, Fragment(),
    PlayerJerseyFragmentListener {

    private lateinit var viewPagerJerseys: ViewPager
    private lateinit var rgJerseys: RadioGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Creates the view controlled by the fragment
        val view = inflater.inflate(R.layout.fragment_player_jersey, container, false)
        viewPagerJerseys = view.findViewById(R.id.vp_jerseys)
        rgJerseys = view.findViewById(R.id.rg_jerseys)
        // Retrieve and display the player data from the Bundle
        val args = arguments

        val pageAdapter = PlayerJerseyPagerAdapter(activity!!.applicationContext, this)
        val viewPagerJersey = view.findViewById<ViewPager>(R.id.vp_jerseys)
        viewPagerJersey.adapter = pageAdapter
        viewPagerJersey.addOnPageChangeListener(this)

        return view
    }

    companion object {
        // Method for creating new instances of the fragment
        fun newInstance(players: TreeMap<Int, Player>, player: Player?): PlayerJerseyFragment {
            // Store the player data in a Bundle object
            val args = Bundle()
            args.putSerializable("", player)

            // Create a new PlayerFragment and set the Bundle as the arguments
            // to be retrieved and displayed when the view is created
            val fragment = PlayerJerseyFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onPageSelected(p0: Int) {
        if(p0 >= viewPagerJerseys.adapter!!.count){
            rgJerseys.check(rgJerseys.getChildAt(0).id)
        }
        else{
            rgJerseys.check(rgJerseys.getChildAt(p0).id)
        }
    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

    }

    override fun onJerseySelected(drawable: String) {
        onJerseyTemp(drawable)
    }

    internal interface Temp {
        fun onJerseyTemp(drawable: String)
    }

    //you have to call this method when user pressed to button
    private fun onJerseyTemp(drawable: String) {
        val listener: Temp? = parentFragment as Temp?
        listener?.onJerseyTemp(drawable)
    }
}

interface PlayerJerseyFragmentListener {
    fun onJerseySelected(drawable: String)
}
