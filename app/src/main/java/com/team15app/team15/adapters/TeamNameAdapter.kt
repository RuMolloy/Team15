package com.team15app.team15.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.team15app.team15.listeners.OnTeamClickListener
import com.team15app.team15.R

class TeamNameAdapter(private var myDataset: ArrayList<String>,
                      private val isDeleteSupportedOnLongPress: Boolean,
                      private val myOnTeamClickListener: OnTeamClickListener) : RecyclerView.Adapter<TeamNameAdapter.MyViewHolder>(){

    lateinit var context: Context
    var showCheckBox = false
    var listOfSelectedItems = ArrayList<String>()
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView = view.findViewById<TextView>(R.id.tv_dialog_title)!!
        val checkBox = view.findViewById<CheckBox>(R.id.cbx_delete_team)!!
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        context = parent.context
        // create a new view
        val view = LayoutInflater.from(context)
            .inflate(R.layout.team_name_row, parent, false) as View
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val item = myDataset[position]
        holder.textView.text = item
        holder.checkBox.isChecked = listOfSelectedItems.contains(item)
            when {
                showCheckBox && isCheckboxSupported(item) -> holder.checkBox.visibility = View.VISIBLE
                else -> holder.checkBox.visibility = View.INVISIBLE
            }
        holder.itemView.setOnClickListener {
            when {
                showCheckBox -> {
                    when {
                        isCheckboxSupported(item) -> {
                            holder.checkBox.isChecked = !holder.checkBox.isChecked
                            updateSelectedItems(holder, item)
                        }
                        else -> {
                            listOfSelectedItems.clear()
                            notifyDataSetChanged()
                            showCheckBox = false
                            myOnTeamClickListener.onTeamLongClick(showCheckBox)
                        }
                    }
                }
                else -> myOnTeamClickListener.onTeamClick(item)
            }
        }
        if(isDeleteSupportedOnLongPress && isCheckboxSupported(item)){
            holder.itemView.setOnLongClickListener {
                showCheckBox = !showCheckBox
                if(showCheckBox){
                    holder.checkBox.isChecked = showCheckBox
                    updateSelectedItems(holder, item)
                }
                else{
                    listOfSelectedItems.clear()
                }
                notifyDataSetChanged()
                myOnTeamClickListener.onTeamLongClick(showCheckBox)
                true
            }
        }
        holder.checkBox.setOnClickListener {
            updateSelectedItems(holder, item)
        }
    }

    private fun isCheckboxSupported(item: String): Boolean{
        // checkbox is unsupported if the list of items contains the items below
        return !(item == context.getString(R.string.create_new_team)
                || item == context.getString(R.string.duplicate_a_team))
    }

    private fun updateSelectedItems(holder: MyViewHolder, item: String){
        when {
            holder.checkBox.isChecked -> if(!listOfSelectedItems.contains(item)){
                listOfSelectedItems.add(item)
            }
            else -> if(listOfSelectedItems.contains(item)){
                listOfSelectedItems.remove(item)
            }
        }
    }

    fun getSelectedItems(): ArrayList<String>{
        return listOfSelectedItems
    }

    fun addItem(item : String){
        if(!myDataset.contains(item)){
            myDataset.add(0, item)
        }
        if(!listOfSelectedItems.contains(item)){
            listOfSelectedItems.add(0, item)
        }
    }

    fun removeItem(item : String){
        if(myDataset.contains(item)){
            myDataset.remove(item)
        }
        if(listOfSelectedItems.contains(item)){
            listOfSelectedItems.remove(item)
        }
    }

    fun resetCheckbox(){
        showCheckBox = false
        myOnTeamClickListener.onTeamLongClick(showCheckBox)
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}