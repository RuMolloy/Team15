package com.team15app.team15.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.team15app.team15.Club
import com.team15app.team15.County
import com.team15app.team15.listeners.OnTeamClickListener
import com.team15app.team15.R
import com.team15app.team15.viewholders.ClubViewHolder
import com.team15app.team15.viewholders.CountyViewHolder
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class ClubAdapter(groups: List<County>, private val myOnTeamClickListener: OnTeamClickListener) : ExpandableRecyclerViewAdapter<CountyViewHolder, ClubViewHolder>(groups) {

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): CountyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_view_item_county, parent, false)
        return CountyViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_view_item_club, parent, false)
        return ClubViewHolder(view)
    }

    override fun onBindChildViewHolder(holder: ClubViewHolder, flatPosition: Int, group: ExpandableGroup<*>, childIndex: Int) {
        val club = group.items[childIndex] as Club
        holder.setClubName(club)
        holder.itemView.setOnClickListener {
            myOnTeamClickListener.onTeamClick(club.name + "_" + (group as County).getName())
        }
    }

    override fun onBindGroupViewHolder(holder: CountyViewHolder, flatPosition: Int, group: ExpandableGroup<*>) {
        holder.setCounty(group as County)
    }

}