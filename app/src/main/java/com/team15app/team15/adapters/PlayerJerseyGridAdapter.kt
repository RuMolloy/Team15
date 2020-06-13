package com.team15app.team15.adapters

import java.util.ArrayList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.team15app.team15.dialogs.PlayerJerseyFragmentListener
import com.team15app.team15.R

class GridViewAdapter(context: Context,
                      layoutResourceId: Int,
                      data: ArrayList<Int>,
                      private val playerJerseyFragmentListener: PlayerJerseyFragmentListener
) : ArrayAdapter<Int>(context, layoutResourceId, data)
{
    private var resourceId = layoutResourceId
    private var selectedPos = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var itemView: View?  = convertView
        var holder: ViewHolder

        if (itemView == null)
        {
            itemView = LayoutInflater.from(parent.context).inflate(resourceId, parent, false)

            holder = ViewHolder()
            holder.imgItem = itemView.findViewById(R.id.imgItem)
            itemView.tag = holder
        }
        else
        {
            holder = itemView.tag as ViewHolder
        }

        if(selectedPos == position){
            holder.imgItem.background = context.getDrawable(R.drawable.iv_border)
        }
        else{
            holder.imgItem.background = null
        }
        holder.imgItem.setImageDrawable(context.getDrawable(getItem(position)))
        holder.imgItem.setOnClickListener {
            selectedPos = position
            notifyDataSetChanged()
            playerJerseyFragmentListener.onJerseySelected(context.resources.getResourceEntryName(getItem(position)))
        }

        return itemView
    }

    class ViewHolder
    {
        lateinit var imgItem : ImageView
    }

}