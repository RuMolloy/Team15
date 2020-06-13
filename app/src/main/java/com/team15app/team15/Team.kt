package com.team15app.team15

class Team(private var name: String,
           private var crest: Int,
           private var jerseyGoalkeeper: String,
           private var jerseyOutfield: String){


    fun setName(name: String){
        this.name = name
    }

    fun getName(): String{
        return name
    }

    fun getCrest(): Int{
        return crest
    }

    fun setJerseyGoalkeeper(jersey: String){
        jerseyGoalkeeper = jersey
    }

    fun getJerseyGoalkeeper(): String{
        return jerseyGoalkeeper
    }

    fun setJerseyOutfield(jersey: String){
        jerseyOutfield = jersey
    }

    fun getJerseyOutfield(): String{
        return jerseyOutfield
    }
}