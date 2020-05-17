package com.team15app.team15.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.team15app.team15.JerseyEnum
import com.team15app.team15.dialogs.PlayerJerseyFragmentListener
import com.team15app.team15.R

class PlayerJerseyPagerAdapter(private val context: Context,
                               private val playerJerseyFragmentListener: PlayerJerseyFragmentListener
) : PagerAdapter() {

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {

        val jerseyEnum = JerseyEnum.values()[position]
        val inflater = LayoutInflater.from(context)

        val layout = inflater.inflate(jerseyEnum.layout, collection, false) as ViewGroup
        val row: GridView = layout.findViewById(R.id.gridView)

        val gvAdapter = GridViewAdapter(context,
            R.layout.team_jersey_grid_item,
            jerseyEnum.jerseys,
            playerJerseyFragmentListener)

        row.adapter = gvAdapter

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