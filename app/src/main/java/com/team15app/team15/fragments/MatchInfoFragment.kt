package com.team15app.team15.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.team15app.team15.R
import com.team15app.team15.databinding.FragmentMatchInfoBinding

class MatchInfoFragment: Fragment() {

    private var _binding: FragmentMatchInfoBinding? = null
    private lateinit var binding: FragmentMatchInfoBinding

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Creates the view controlled by the fragment
        _binding = FragmentMatchInfoBinding.inflate(layoutInflater, container, false)
        binding = _binding!!

        // Retrieve and display from the Bundle
        setTeamNameA(arguments?.getString(getString(R.string.default_team_name_a)))
        setGoalkeeperJersey(arguments?.getString(getString(R.string.goalkeeper)))
        setOutfielderJersey(arguments?.getString(getString(R.string.outfielder)))
        setTeamNameB(arguments?.getString(getString(R.string.default_team_name_b)))
        setMatchInfo(arguments?.getString(getString(R.string.default_match_info)))

        return binding.root
    }

    private fun setTeamNameA(teamNameA: String?) {
        if(teamNameA.isNullOrEmpty() || teamNameA == getString(R.string.default_team_name_a)){
            binding.etTeamA.setHint(R.string.default_team_name_a)
        }
        else binding.etTeamA.setText(teamNameA)
    }

    private fun setGoalkeeperJersey(goalkeeperTag: String?) {
        binding.ivGoalkeeper.setImageResource(resources.getIdentifier(goalkeeperTag,
            "drawable",
            requireContext().packageName))
        binding.ivGoalkeeper.tag = goalkeeperTag
        binding.ivGoalkeeper.setOnClickListener{
            onJerseyPressed(getString(R.string.goalkeeper))
        }
    }

    private fun setOutfielderJersey(outfielderTag: String?) {
        binding.ivOutfielder.setImageResource(resources.getIdentifier(outfielderTag,
            "drawable",
            requireContext().packageName))
        binding.ivOutfielder.tag = outfielderTag
        binding.ivOutfielder.setOnClickListener{
            onJerseyPressed(getString(R.string.outfielder))
        }
    }

    private fun setTeamNameB(teamNameB: String?) {
        if(teamNameB.isNullOrEmpty() || teamNameB == "vs. "+getString(R.string.default_team_name_b))
            binding.etTeamB.setHint(R.string.default_team_name_b
            )
        else binding.etTeamB.setText(teamNameB.substringAfter("vs. "))
    }

    private fun setMatchInfo(matchInfo: String?) {
        binding.rlMatchInfoWrite.etMatchInfoNameWrite.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.rlMatchInfoWrite.etMatchInfoNameWrite.setRawInputType(InputType.TYPE_CLASS_TEXT)
        if(matchInfo.isNullOrEmpty() || matchInfo == getString(R.string.default_match_info))
            binding.rlMatchInfoWrite.etMatchInfoNameWrite.setHint(R.string.default_match_info)
        else binding.rlMatchInfoWrite.etMatchInfoNameWrite.setText(matchInfo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateJersey(tabNumber: Int, resourceName: String){
        if (tabNumber == 1) {
            binding.ivGoalkeeper.setImageResource(
                resources.getIdentifier(resourceName,
                "drawable",
                requireContext().packageName)
            )
            binding.ivGoalkeeper.tag = resourceName
        } else {
            binding.ivOutfielder.setImageResource(
                resources.getIdentifier(resourceName,
                    "drawable",
                    requireContext().packageName)
            )
            binding.ivOutfielder.tag = resourceName
        }
    }

    fun getBundle(): Bundle {
        val bundle = Bundle().apply {
            putString(resources.getString(R.string.default_team_name_a),
                binding.etTeamA.text.toString())
            putString(resources.getString(R.string.default_team_name_b),
                binding.etTeamB.text.toString())
            putString(resources.getString(R.string.default_match_info),
                binding.rlMatchInfoWrite.etMatchInfoNameWrite.text.toString())
            putString(resources.getString(R.string.goalkeeper),
                binding.ivGoalkeeper.tag.toString())
            putString(resources.getString(R.string.outfielder),
                binding.ivOutfielder.tag.toString())
        }

        return bundle
    }

    companion object {
        // Method for creating new instances of the fragment
        fun newInstance(args: Bundle?): MatchInfoFragment {
            val fragment = MatchInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    internal interface MatchInfoFragmentListener {
        fun onJerseyPressed(source: String)
    }

    //you have to call this method when user pressed to button
    private fun onJerseyPressed(source: String) {
        val listener: MatchInfoFragmentListener? = parentFragment as MatchInfoFragmentListener?
        listener?.onJerseyPressed(source)
    }
}
