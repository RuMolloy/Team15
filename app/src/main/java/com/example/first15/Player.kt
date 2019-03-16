package com.example.first15

import android.graphics.Point
import android.graphics.Rect

class Player(position: String, number: Int){

    private var pos: String = position
    private var num: Int = number
    private var pointBitmap: Point? = null
    private var pointText: Point? = null
    private var rect: Rect? = null

    private fun setPosition(position: String) {
        pos = position
    }

    public fun getPosition(): String{
        return pos
    }

    private fun setNumber(number: Int) {
        num = number
    }

    private fun getNumber(): Int{
        return num
    }

    public fun getNumberAndPosition(): String{
        return "$num. $pos"
    }

    public fun setBitmapPoint(bitmapPoint: Point){
        pointBitmap = bitmapPoint
    }

    public fun getBitmapPoint(): Point?{
        return pointBitmap
    }

    public fun setTextPoint(textPoint: Point){
        pointText = textPoint
    }

    public fun getTextPoint(): Point?{
        return pointText
    }

    public fun setRect(rectangle: Rect){
        rect = rectangle
    }

    public fun getRect(): Rect?{
        return rect
    }
}
