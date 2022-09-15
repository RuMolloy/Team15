package com.team15app.team15.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team15app.team15.R
import com.team15app.team15.adapters.MatchInfoAdapter
import com.team15app.team15.databinding.DialogEditMatchInfoBinding
import com.team15app.team15.fragments.MatchInfoFragment
import com.team15app.team15.fragments.PlayerJerseyFragment

class MatchInfoDialogFragment : DialogFragment(),
    MatchInfoFragment.MatchInfoFragmentListener,
    PlayerJerseyFragment.PlayerJerseyFragmentListener
{
    private lateinit var onFinishEditDialogListener: OnFinishEditDialog

    private var _binding: DialogEditMatchInfoBinding? = null
    private lateinit var binding: DialogEditMatchInfoBinding

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditMatchInfoBinding.inflate(layoutInflater)
        binding = _binding!!

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(R.string.set_match_details)

        val matchInfoAdapter = MatchInfoAdapter(childFragmentManager,
            binding.tlMatchInfo.tabCount,
            arguments)
        binding.vpTabs.adapter = matchInfoAdapter
        binding.vpTabs.offscreenPageLimit = 3
        binding.vpTabs.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tlMatchInfo))
        binding.tlMatchInfo.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                binding.vpTabs.currentItem = binding.tlMatchInfo.selectedTabPosition
            }
        })

        alertDialogBuilder.setView(binding.root)
        alertDialogBuilder.setPositiveButton(R.string.ok) { _, _ ->
            onFinishEditDialogListener.onFinishEditMatchInfoDialog(getMatchInfoBundle())
        }
        alertDialogBuilder.setNegativeButton(R.string.cancel) { _, _ ->

        }

        return alertDialogBuilder.create()
    }

    override fun onJerseyPressed(source: String) {
        val tabNum = if (source == getString(R.string.goalkeeper)) 1 else 2
        binding.tlMatchInfo.getTabAt(tabNum)!!.select()
    }

    override fun onJerseySelected(drawable: String) {
        for(frag in childFragmentManager.fragments){
            if (frag is MatchInfoFragment){
                frag.updateJersey(binding.tlMatchInfo.selectedTabPosition, drawable)
            }
            break
        }
        binding.tlMatchInfo.getTabAt(0)!!.select();
    }

    interface OnFinishEditDialog {
        fun onFinishEditMatchInfoDialog(bundle: Bundle)
    }

    fun setOnFinishEditDialogListener(listener: OnFinishEditDialog) {
        onFinishEditDialogListener = listener
    }

    private fun getMatchInfoBundle(): Bundle{
        var bundle = Bundle()
        for(frag in childFragmentManager.fragments){
            if (frag is MatchInfoFragment){
                bundle = frag.getBundle()
                break
            }
        }
        return bundle
    }
}
