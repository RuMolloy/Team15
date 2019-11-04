package com.team15app.team15

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import kotlin.collections.ArrayList

class County(private var name: String, private var clubs: ArrayList<Club>) : ExpandableGroup<Club>(name, clubs) {

    fun getName(): String{
        return name
    }

    fun addClub(club: Club){
        clubs.add(club)
    }

    fun getClubs(): ArrayList<Club>{
        return clubs
    }
}