package com.team15app.team15

import android.graphics.Point
import android.graphics.Rect
import java.io.Serializable

class Player(name: String, number: String): Serializable {

    private var nameDefault: String = name
    private var nameCustom: String = name
    private var nameEdit: String = ""

    private var numberDefault: String = number
    private var numberCustom: String = number
    private var numberEdit: String = number

    private var pointJerseyDefault: Point? = null
    private var pointJerseyCustom: Point? = null

    private var rectNameDefault: Rect? = null
    private var rectNameCustom: Rect = Rect()

    private var pointNameDefault: Point? = null
    private var pointNameCustom: Point? = null

    private var rectJerseyDefault: Rect = Rect()
    private var rectJerseyCustom: Rect = Rect()

    private var isSelected: Boolean = false
    private var isOverlapping: Boolean = false

    fun setCustomName(name: String) {
        this.nameCustom = name
    }

    fun setEditName(name: String){
        this.nameEdit = name
    }

    fun getName(): String{
        return if(nameCustom.isNullOrEmpty()) nameDefault
        else nameCustom
    }

    fun getDefaultName(): String{
        return nameDefault
    }

    fun getEditName(): String{
        return nameEdit
    }

    fun isDefaultName(): Boolean{
        return (nameCustom == nameDefault) || nameCustom.isNullOrEmpty()
    }

    fun setDefaultNumber(numberDefault: String) {
        this.numberDefault = numberDefault
    }

    fun getDefaultNumber(): String{
        return numberDefault
    }

    fun isDefaultNumber(): Boolean{
        return (numberCustom == numberDefault) || numberCustom.isNullOrEmpty()
    }

    fun setCustomNumber(numberCustom: String) {
        this.numberCustom = numberCustom
    }

    fun setEditNumber(numberEdit: String){
        this.numberEdit = numberEdit
    }

    fun getNumber(): String{
        return numberCustom
    }

    fun getEditNumber(): String{
        return numberEdit
    }

    fun getNumberAndName(): String{
        return numberCustom + ". " + getName()
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

    fun setNamePointCustom(pointNameCustom: Point?){
        this.pointNameCustom = pointNameCustom
    }

    fun setNamePointDefault(pointNameDefault: Point?){
        this.pointNameDefault = pointNameDefault
    }

    fun getNamePointCustom(): Point?{
        return pointNameCustom
    }

    fun getNamePointDefault(): Point?{
        return pointNameDefault
    }

    fun setJerseyRectDefault(rectJersey: Rect){
        this.rectJerseyDefault = rectJersey
    }

    fun setJerseyRectCustom(rectJersey: Rect){
        this.rectJerseyCustom = rectJersey
    }

    fun getJerseyRectDefault(): Rect?{
        return rectJerseyDefault
    }

    fun getJerseyRect(): Rect{
        if(rectJerseyCustom == rectJerseyDefault){
            return rectJerseyDefault
        }
        return rectJerseyCustom
    }

    fun setNameRectCustom(rectNumberAndName: Rect){
        this.rectNameCustom = rectNumberAndName
    }

    fun getNameRectCustom(): Rect{
        return rectNameCustom
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
