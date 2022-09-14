package com.team15app.team15.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.team15app.team15.data.Player
import com.team15app.team15.R
import com.team15app.team15.adapters.PlayerNameAdapter
import com.team15app.team15.databinding.DialogEditPlayerBinding
import java.util.*

class PlayerDialogFragment: DialogFragment() {

    private lateinit var onFinishEditDialogListener: OnFinishEditDialog
    private lateinit var imm: InputMethodManager

    private var _binding: DialogEditPlayerBinding? = null
    private lateinit var binding: DialogEditPlayerBinding

    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        imm = (requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?)!!
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditPlayerBinding.inflate(layoutInflater)
        binding = _binding!!

        var mapOfPlayers = TreeMap<Int, Player>()
        val playerNumber: Int
        val bundle = this.arguments
        if (bundle!!.getSerializable(getString(R.string.default_edit_player_title)) != null)
            mapOfPlayers = bundle.getSerializable(getString(R.string.default_edit_player_title)) as TreeMap<Int, Player>
            playerNumber = bundle.getInt(getString(R.string.outfielder))

        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle(R.string.default_edit_player_title)

        val pageAdapterPlayer = PlayerNameAdapter(childFragmentManager, mapOfPlayers)
        binding.vpPlayers.adapter = pageAdapterPlayer
        //-1 as the index is always one less than the players number, index starts at 0
        binding.vpPlayers.currentItem = playerNumber - 1

        alertDialogBuilder.setView(binding.root)
        alertDialogBuilder.setPositiveButton(R.string.ok) { _, _ ->
            finishActionsBeforeClosing(true)
        }
        alertDialogBuilder.setNegativeButton(R.string.cancel) { _, _ ->
            finishActionsBeforeClosing(false)
        }

        return alertDialogBuilder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun finishActionsBeforeClosing(isChangeAccepted: Boolean){
        imm.hideSoftInputFromWindow(requireView().windowToken,0)
        onFinishEditDialogListener.onFinishEditDialog(isChangeAccepted)
    }

    interface OnFinishEditDialog {
        fun onFinishEditDialog(isChangeAccepted: Boolean)
    }

    fun setOnFinishEditDialogListener(listener: OnFinishEditDialog) {
        onFinishEditDialogListener = listener
    }
}
