package com.team15app.team15.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.team15app.team15.JerseyEnum
import com.team15app.team15.R
import com.team15app.team15.listeners.OnTeamClickListener

class JerseyPagerAdapter(private val context: Context,
                         private val myOnTeamClickListener: OnTeamClickListener,
                         private val isGoalkeeper: Boolean) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {

        val jerseyEnum = JerseyEnum.values()[position]
        val inflater = LayoutInflater.from(context)

        val layout = inflater.inflate(jerseyEnum.layout, collection, false) as ViewGroup
        val row: GridView = layout.findViewById(R.id.gridView)

        val gridviewAdapter = GridViewAdapter(context,
            R.layout.team_jersey_grid_item,
            jerseyEnum.jerseys,
            myOnTeamClickListener,
            isGoalkeeper)

        row.adapter = gridviewAdapter

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
        return JerseyEnum.values().size
    }
}