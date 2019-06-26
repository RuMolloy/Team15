package com.team15app.team15

import android.graphics.Point
import android.graphics.Rect

class Player(name: String, number: String){

    private var nameDefault: String = name
    private var nameCustom: String = name
    private var number: String = number
    private var pointJersey: Point? = null
    private var pointNumberAndName: Point? = null
    private var rectJersey: Rect? = null
    private var rectNumberAndName: Rect? = null

    fun setCustomName(name: String) {
        this.nameCustom = name
    }

    fun getName(): String{
        return if(nameCustom.isNullOrEmpty()) nameDefault
        else nameCustom
    }

    fun getDefaultName(): String{
        return nameDefault
    }

    fun isDefaultName(): Boolean{
        return (nameCustom == nameDefault) || nameCustom.isNullOrEmpty()
    }

    fun setNumber(number: String) {
        this.number = number
    }

    fun getNumber(): String{
        return number
    }

    fun getNumberAndName(): String{
        return number + ". " + getName()
    }

    fun setJerseyPoint(pointJersey: Point){
        this.pointJersey = pointJersey
    }

    fun getJerseyPoint(): Point?{
        return pointJersey
    }

    fun setNumberAndNamePoint(pointNumberAndName: Point){
        this.pointNumberAndName = pointNumberAndName
    }

    fun getNumberAndNamePoint(): Point?{
        return pointNumberAndName
    }

    fun setJerseyRect(rectJersey: Rect){
        this.rectJersey = rectJersey
    }

    fun getJerseyRect(): Rect?{
        return rectJersey
    }

    fun setNumberAndNameRect(rectNumberAndName: Rect){
        this.rectNumberAndName = rectNumberAndName
    }

    fun getNumberAndNameRect(): Rect?{
        return rectNumberAndName
    }
}
