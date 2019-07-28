package com.team15app.team15

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import java.util.ArrayList

class CustomPagerAdapter(private val context: Context, private val myOnTeamClickListener: OnTeamClickListener) : PagerAdapter() {

    private lateinit var viewAdapter: MyAdapter
    private lateinit var rvTeams: RecyclerView

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val customPagerEnum = TeamEnum.values()[position]
        val inflater = LayoutInflater.from(context)

        val listOfTeams = getTeams(position)
        val layout = inflater.inflate(customPagerEnum.tabLayout, collection, false) as ViewGroup
        var viewManager = LinearLayoutManager(context)
        viewAdapter = MyAdapter(listOfTeams,false,myOnTeamClickListener)
        rvTeams = layout.findViewById<RecyclerView>(R.id.rv_team_names).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

            // adds a line between each recyclerview item
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

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
        viewAdapter.updateItems(getTeams(tabPosition))
        viewAdapter.notifyDataSetChanged()
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