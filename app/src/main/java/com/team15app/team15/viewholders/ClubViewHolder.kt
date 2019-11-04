package com.team15app.team15.viewholders

import android.view.View
import android.widget.TextView
import com.team15app.team15.Club
import com.team15app.team15.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class ClubViewHolder(itemView: View) : ChildViewHolder(itemView) {

    private var name: TextView = itemView.findViewById(R.id.tv_team_name)

    fun setClubName(club: Club){
        name.text = club.name
    }
}