package com.team15app.team15

class Team(private var name: String,
           private var crest: Int,
           private var jerseyGoalkeeper: Int,
           private var jerseyOutfield: Int){

    fun getName(): String{
        return name
    }

    fun getCrest(): Int{
        return crest
    }

    fun getJerseyGoalkeeper(): Int{
        return jerseyGoalkeeper
    }

    fun getJerseyOutfield(): Int{
        return jerseyOutfield
    }
}