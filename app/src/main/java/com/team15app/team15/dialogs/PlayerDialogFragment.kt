package com.team15app.team15.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.team15app.team15.data.Player
import com.team15app.team15.R
import com.team15app.team15.adapters.PlayerNameAdapter
import java.util.*

class PlayerDialogFragment(): DialogFragment() {

    lateinit var customView: View
    private lateinit var onFinishEditDialogListener: OnFinishEditDialog
    private lateinit var imm: InputMethodManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        imm = (context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?)!!
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        return customView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var mapOfPlayers = TreeMap<Int, Player>()
        var playerNumber = 0
        val bundle = this.arguments
        if (bundle!!.getSerializable(getString(R.string.default_edit_player_title)) != null)
            mapOfPlayers = bundle.getSerializable(getString(R.string.default_edit_player_title)) as TreeMap<Int, Player>
            playerNumber = bundle.getInt(getString(R.string.outfielder))

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(R.string.default_edit_player_title)

        // Edited: Overriding onCreateView is not necessary in your case
        val inflater = LayoutInflater.from(context)
        customView = inflater.inflate(R.layout.dialog_edit_player, null)

        val viewPagerPlayer = customView.findViewById<ViewPager>(R.id.vp_players)
        val pageAdapterPlayer = PlayerNameAdapter(childFragmentManager, mapOfPlayers)
        viewPagerPlayer.adapter = pageAdapterPlayer
        viewPagerPlayer.currentItem = playerNumber - 1 //-1 as the index is always one less than the players number, index starts at 0

        alertDialogBuilder.setView(customView)
        alertDialogBuilder.setPositiveButton(R.string.ok) { _, _ ->
            finishActionsBeforeClosing(true)
        }
        alertDialogBuilder.setNegativeButton(R.string.cancel) { _, _ ->
            finishActionsBeforeClosing(false)
        }

        return alertDialogBuilder.create()
    }

    fun finishActionsBeforeClosing(isChangeAccepted: Boolean){
        imm.hideSoftInputFromWindow(view!!.windowToken,0)
        onFinishEditDialogListener.onFinishEditDialog(isChangeAccepted)
    }

    interface OnFinishEditDialog {
        fun onFinishEditDialog(isChangeAccepted: Boolean)
    }

    fun setOnFinishEditDialogListener(listener: OnFinishEditDialog) {
        onFinishEditDialogListener = listener
    }
}
