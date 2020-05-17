package com.team15app.team15.dialogs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.team15app.team15.R

class MatchInfoFragment(): Fragment(){

    lateinit var  etTeamNameA: EditText
    lateinit var  etTeamNameB: EditText
    lateinit var  etMatchInfo: EditText

    lateinit var ivGoalkeeper: ImageView
    lateinit var ivOutfielder: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Creates the view controlled by the fragment
        val view = inflater.inflate(R.layout.fragment_match_info, container, false)
        // Retrieve and display from the Bundle
        val args = arguments
        val tvTeamNameA = args!!.getString(getString(R.string.default_team_name_a))
        val tvTeamNameB = args.getString(getString(R.string.default_team_name_b))
        val tvMatchInfo = args.getString(getString(R.string.default_match_info))
        val dGoalKeeper = args.getInt(getString(R.string.goalkeeper))
        val dOutfielder = args.getInt(getString(R.string.outfielder))

        val llMatchInfoWrite = view.findViewById<LinearLayout>(R.id.ll_match_info_write)
        etMatchInfo = llMatchInfoWrite.findViewById<EditText>(R.id.et_match_info_name_write)
        etMatchInfo.imeOptions = EditorInfo.IME_ACTION_DONE;
        etMatchInfo.setRawInputType(InputType.TYPE_CLASS_TEXT);
        etTeamNameA = llMatchInfoWrite.findViewById<EditText>(R.id.et_team_a)
        etTeamNameB = llMatchInfoWrite.findViewById<EditText>(R.id.et_team_b)

        if(tvTeamNameA.isNullOrEmpty() || tvTeamNameA == getString(R.string.default_team_name_a)) etTeamNameA.setHint(
            R.string.default_team_name_a
        )
        else etTeamNameA.setText(tvTeamNameA)

        ivGoalkeeper = llMatchInfoWrite.findViewById<ImageView>(R.id.iv_goalkeeper)
        ivGoalkeeper.setImageResource(dGoalKeeper)
        ivGoalkeeper.tag = dGoalKeeper.toString()
        ivGoalkeeper.setOnClickListener{
            onJerseyPressed(getString(R.string.goalkeeper))
        }
        ivOutfielder = llMatchInfoWrite.findViewById<ImageView>(R.id.iv_outfielder)
        ivOutfielder.setImageResource(dOutfielder)
        ivOutfielder.tag = dOutfielder.toString()
        ivOutfielder.setOnClickListener{
            onJerseyPressed(getString(R.string.outfielder))
        }

        if(tvTeamNameB.isNullOrEmpty() || tvTeamNameB == "vs. "+getString(R.string.default_team_name_b)) etTeamNameB.setHint(
            R.string.default_team_name_b
        )
        else etTeamNameB.setText(tvTeamNameB.substringAfter("vs. "))

        if(tvMatchInfo.isNullOrEmpty() || tvMatchInfo == getString(R.string.default_match_info)) etMatchInfo.setHint(
            R.string.default_match_info
        )
        else etMatchInfo.setText(tvMatchInfo)

        return view
    }

    fun updateJersey(tabNumber: Int, drawable: Int){
        if (tabNumber == 1) {
            ivGoalkeeper.setImageResource(drawable)
            ivGoalkeeper.tag = drawable.toString()
        } else {
            ivOutfielder.setImageResource(drawable)
            ivOutfielder.tag = drawable.toString()
        }
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
