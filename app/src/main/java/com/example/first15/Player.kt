package com.example.first15

import android.graphics.Point
import android.graphics.Rect

class Player(name: String, number: String){

    private var nameDefault: String = name
    private var nameCustom: String = name
    private var number: String = number
    private var pointBitmap: Point? = null
    private var pointText: Point? = null
    private var rect: Rect? = null

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

    fun setBitmapPoint(bitmapPoint: Point){
        pointBitmap = bitmapPoint
    }

    fun getBitmapPoint(): Point?{
        return pointBitmap
    }

    fun setTextPoint(textPoint: Point){
        pointText = textPoint
    }

    fun getTextPoint(): Point?{
        return pointText
    }

    fun setRect(rectangle: Rect){
        rect = rectangle
    }

    fun getRect(): Rect?{
        return rect
    }
}
