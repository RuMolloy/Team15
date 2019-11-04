package com.team15app.team15.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.team15app.team15.County
import com.team15app.team15.R
import com.team15app.team15.TeamEnum
import com.team15app.team15.listeners.OnTeamClickListener
import java.util.ArrayList

class CustomPagerAdapter(private val context: Context,
                         private val myOnTeamClickListener: OnTeamClickListener,
                         private val listOfClubs: List<County>) : PagerAdapter() {

    private lateinit var clubAdapter: ClubAdapter
    private lateinit var countyAdapter: CountyAdapter
    private lateinit var rvTeams: RecyclerView

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val customPagerEnum = TeamEnum.values()[position]
        val inflater = LayoutInflater.from(context)

        val layout = inflater.inflate(customPagerEnum.tabLayout, collection, false) as ViewGroup
        var viewManager = LinearLayoutManager(context)

        rvTeams = layout.findViewById<RecyclerView>(R.id.rv_team_names).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // countyAdapter will be specified in the updateTabContents method
            // as we change the adapter depending on viewing counties or clubs

            // adds a line between each recyclerview item
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        updateTabContents(position)

        collection.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun getCount(): Int {
        return TeamEnum.values().size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val customPagerEnum = TeamEnum.values()[position]
        return context.getString(customPagerEnum.tabName)
    }

    fun updateTabContents(tabPosition: Int){
        val customPagerEnum = TeamEnum.values()[tabPosition]
        if(customPagerEnum == TeamEnum.COUNTY){
            countyAdapter = CountyAdapter(getTeams(tabPosition), false, myOnTeamClickListener)
            rvTeams.adapter = countyAdapter
        }
        else{
            clubAdapter = ClubAdapter(listOfClubs, myOnTeamClickListener)
            rvTeams.adapter = clubAdapter
        }
    }

    private fun getTeams(position: Int): ArrayList<String>{
        val customPagerEnum = TeamEnum.values()[position]
        var list: List<String>
        list = when {
            customPagerEnum.name == TeamEnum.COUNTY.name -> context.resources.getStringArray(R.array.team_names_counties).toList()
            else -> context.resources.getStringArray(R.array.team_names_clubs).toList()
        }
        val listOfTeams: ArrayList<String> = ArrayList(list)
        listOfTeams.sort()

        return listOfTeams
    }
}