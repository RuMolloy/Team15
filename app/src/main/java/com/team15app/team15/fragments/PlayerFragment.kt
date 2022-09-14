package com.team15app.team15.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team15app.team15.data.Player
import com.team15app.team15.R
import com.team15app.team15.databinding.FragmentPlayerDetailsBinding
import java.util.*

class PlayerFragment : Fragment(){

    private var _binding: FragmentPlayerDetailsBinding? = null
    private lateinit var binding: FragmentPlayerDetailsBinding

    private lateinit var players: TreeMap<Int, Player>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Creates the view controlled by the fragment
        _binding = FragmentPlayerDetailsBinding.inflate(inflater, container, false)
        binding = _binding!!

        // Retrieve and display the player data from the Bundle
        val player = requireArguments().getSerializable("") as Player

        binding.etEditPlayerNumber.setText(player.getNumber())
        binding.etEditPlayerNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val playerInUse = isPlayerNumberInUse(
                    player,
                    binding.etEditPlayerNumber.text.toString()
                )
                if(playerInUse.isNotEmpty()){
                    binding.etEditPlayerNumber.error =
                        resources.getString(R.string.error_player_number_in_use) + " " + playerInUse
                }
                else{
                    binding.etEditPlayerNumber.error = null
                    if(binding.etEditPlayerNumber.text.toString().isEmpty()){
                        binding.etEditPlayerNumber.hint = player.getDefaultNumber()
                    }
                    else player.setEditNumber(binding.etEditPlayerNumber.text.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        if(player.isDefaultName()) binding.etEditPlayerName.hint = player.getDefaultName()
        else binding.etEditPlayerName.setText(player.getName())
        binding.etEditPlayerName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(binding.etEditPlayerName.text.toString().isEmpty()){
                    binding.etEditPlayerName.hint = player.getDefaultName()
                }
                else player.setEditName(binding.etEditPlayerName.text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        // Method for creating new instances of the fragment
        fun newInstance(players: TreeMap<Int, Player>, player: Player?): PlayerFragment {
            // Store the player data in a Bundle object
            val args = Bundle()
            args.putSerializable("", player)

            // Create a new PlayerFragment and set the Bundle as the arguments
            // to be retrieved and displayed when the view is created
            val fragment = PlayerFragment()
            fragment.players = players
            fragment.arguments = args
            return fragment
        }
    }

    private fun isPlayerNumberInUse(selectedPlayer: Player, number: String): String{
        for(player in players.values) {
            if(player != selectedPlayer && player.getNumber() == number){
                return player.getName()
            }
        }
        return ""
    }
}
