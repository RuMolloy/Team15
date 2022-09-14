package com.team15app.team15.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team15app.team15.adapters.PlayerJerseyPagerAdapter
import com.team15app.team15.databinding.FragmentPlayerJerseyBinding

class PlayerJerseyFragment :
    Fragment(),
    ViewPager.OnPageChangeListener,
    PlayerJerseyFragmentListener
{
    private var _binding: FragmentPlayerJerseyBinding? = null
    private lateinit var binding: FragmentPlayerJerseyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        // Creates the view controlled by the fragment
        _binding = FragmentPlayerJerseyBinding.inflate(inflater, container, false)
        binding = _binding!!

        val pageAdapter = PlayerJerseyPagerAdapter(
            requireActivity().applicationContext,
            this
        )
        binding.vpJerseys.adapter = pageAdapter
        binding.vpJerseys.addOnPageChangeListener(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPageSelected(p0: Int) {
        if(p0 >= binding.vpJerseys.adapter!!.count){
            binding.rgJerseys.check(binding.rgJerseys.getChildAt(0).id)
        }
        else{
            binding.rgJerseys.check(binding.rgJerseys.getChildAt(p0).id)
        }
    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

    }

    override fun onJerseySelected(drawable: String) {
        onJerseyTemp(drawable)
    }

    internal interface PlayerJerseyFragmentListener {
        fun onJerseySelected(drawable: String)
    }

    //you have to call this method when user pressed to button
    private fun onJerseyTemp(drawable: String) {
        val listener: PlayerJerseyFragmentListener? = parentFragment as PlayerJerseyFragmentListener?
        listener?.onJerseySelected(drawable)
    }
}

interface PlayerJerseyFragmentListener {
    fun onJerseySelected(drawable: String)
}
