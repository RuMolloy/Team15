package com.team15app.team15.dialogs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.team15app.team15.Player
import com.team15app.team15.R
import java.util.*

class PlayerFragment(): Fragment(){

    lateinit var players: TreeMap<Int, Player>
    lateinit var etPlayerName: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Creates the view controlled by the fragment
        val view = inflater.inflate(R.layout.fragment_player_details, container, false)
        // Retrieve and display the player data from the Bundle
        val args = arguments
        val player = args!!.getSerializable("") as Player

        val etPlayerNumber = view.findViewById<EditText>(R.id.et_edit_player_number)
        etPlayerNumber.setText(player.getNumber())
        etPlayerNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var playerInUse = isPlayerNumberInUse(player, etPlayerNumber.text.toString())
                if(playerInUse.isNotEmpty()){
                    etPlayerNumber.error = resources.getString(R.string.error_player_number_in_use) + " " + playerInUse
                }
                else{
                    etPlayerNumber.error = null
                    if(etPlayerNumber.text.toString().isEmpty()) etPlayerNumber.hint = player.getDefaultNumber()
                    else player.setEditNumber(etPlayerNumber.text.toString())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        etPlayerName = view.findViewById(R.id.et_edit_player_name)
        if(player.isDefaultName()) etPlayerName.hint = player.getDefaultName()
        else etPlayerName.setText(player.getName())
        etPlayerName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if(etPlayerName.text.toString().isEmpty()) etPlayerName.hint = player.getDefaultName()
                else player.setEditName(etPlayerName.text.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        return view
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
