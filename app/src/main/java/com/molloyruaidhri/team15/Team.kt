package com.molloyruaidhri.team15

import android.graphics.drawable.Drawable

class Team(name: String, crest: Drawable, jerseyGoalkeeper: Int, jerseyOutfield: Int){

    private var name: String = name
    private var crest: Drawable = crest
    private var jerseyGoalkeeper: Int = jerseyGoalkeeper
    private var jerseyOutfield: Int = jerseyOutfield

    fun getName(): String{
        return name
    }

    fun getCrest(): Drawable{
        return crest
    }

    fun getJerseyGoalkeeper(): Int{
        return jerseyGoalkeeper
    }

    fun getJerseyOutfield(): Int{
        return jerseyOutfield
    }
}