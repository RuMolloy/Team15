package com.team15app.team15

import android.graphics.Point
import android.graphics.Rect

class Player(name: String, number: String){

    private var nameDefault: String = name
    private var nameCustom: String = name
    private var number: String = number
    private var pointJerseyDefault: Point? = null
    private var pointJerseyCustom: Point? = null
    private var pointName: Point? = null
    private var rectJerseyDefault: Rect? = null
    private var rectJerseyCustom: Rect? = null
    private var rectName: Rect? = null
    private var isSelected: Boolean = false
    private var isOverlapping: Boolean = false

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

    fun setJerseyPointDefault(pointJersey: Point?){
        this.pointJerseyDefault = pointJersey
    }

    fun setJerseyPointCustom(pointJersey: Point?){
        this.pointJerseyCustom = pointJersey
    }

    fun getJerseyPointDefault(): Point?{
        return pointJerseyDefault
    }

    fun getJerseyPoint(): Point?{
        if(pointJerseyCustom == pointJerseyDefault){
            return pointJerseyDefault
        }
        return pointJerseyCustom
    }

    fun setNamePoint(pointName: Point?){
        this.pointName = pointName
    }

    fun getNamePoint(): Point?{
        return pointName
    }

    fun setJerseyRectDefault(rectJersey: Rect?){
        this.rectJerseyDefault = rectJersey
    }

    fun setJerseyRectCustom(rectJersey: Rect?){
        this.rectJerseyCustom = rectJersey
    }

    fun getJerseyRectDefault(): Rect?{
        return rectJerseyDefault
    }

    fun getJerseyRect(): Rect?{
        if(rectJerseyCustom == rectJerseyDefault){
            return rectJerseyDefault
        }
        return rectJerseyCustom
    }

    fun setNameRect(rectNumberAndName: Rect?){
        this.rectName = rectNumberAndName
    }

    fun getNameRect(): Rect?{
        return rectName
    }

    fun setIsSelected(isSelected: Boolean){
        this.isSelected = isSelected
    }

    fun isSelected(): Boolean{
        return isSelected
    }

    fun setIsOverlapping(isOverlapping: Boolean){
        this.isOverlapping = isOverlapping
    }

    fun isOverlapping(): Boolean{
        return isOverlapping
    }
}
