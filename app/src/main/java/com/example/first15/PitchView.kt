package com.example.first15

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Style
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.View
import android.view.MotionEvent

class PitchView : View  {

    private val line13m = 13.0
    private val line30m = 30.0
    private val line50m = 50.0
    private val line70m = 70.0
    private val line87m = 87.0
    private val line105m = 105.0
    private val pitchWidthInMetres = 90.0
    private val pitchLengthInMetres = 145.0
    private val borderX = 40
    private val borderY = 40
    private var paintPitchPerimeter: Paint = Paint()
    private var paintPitchText: Paint = Paint()

    private var rectPitchPerimeter = Rect(
        borderX,
        borderY,
        this.resources.displayMetrics.widthPixels - borderX,
        this.resources.displayMetrics.heightPixels - borderY
    )

    private val pitchWidthInPixels = rectPitchPerimeter.right - rectPitchPerimeter.left
    private var pixelsPerMetre = pitchWidthInPixels / pitchWidthInMetres
    private var metresPerPixel = pitchWidthInMetres / pitchWidthInPixels
    private val screenResolutionWidthInPixels = this.resources.displayMetrics.widthPixels.toFloat()
    private val screenResolutionHeightInPixels = this.resources.displayMetrics.heightPixels.toFloat()
    private val halfScreenResolutionWidthInPixels = screenResolutionWidthInPixels / 2
    private val halfScreenResolutionHeightInPixels = screenResolutionHeightInPixels / 2

    private var bitmapJersey: Bitmap? = null

    private lateinit var dialogSelectPlayer: AlertDialog

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        initUIResources()
    }

    private fun initUIResources() {
        paintPitchPerimeter = Paint()
        paintPitchPerimeter.isAntiAlias = true
        paintPitchPerimeter.style = Paint.Style.STROKE
        paintPitchPerimeter.strokeWidth = 3f
        paintPitchPerimeter.color = ContextCompat.getColor(context, R.color.colorBorders)

        paintPitchText = Paint()
        paintPitchText.style = Style.FILL
        paintPitchText.color = ContextCompat.getColor(context, R.color.colorPitchLinesAndText)
        paintPitchText.strokeWidth = 3f
        paintPitchText.textSize = 30f

        bitmapJersey = BitmapFactory.decodeResource(this.resources, R.drawable.jersey_default)
    }

    public override fun onDraw(canvas: Canvas) {
        drawJersey(canvas)
    }

    private fun drawJersey(canvas: Canvas){
        val centerX = halfScreenResolutionWidthInPixels - bitmapJersey!!.width/2
        val centerY = rectPitchPerimeter.bottom/2 - bitmapJersey!!.height/2
        val p = Point(centerX.toInt(), centerY)

        val left = (centerX - centerX/1.6).toFloat()
        val right = (centerX + centerX/1.6).toFloat()

        paintPitchText.textAlign = Paint.Align.CENTER

        canvas.drawBitmap(bitmapJersey, p.x.toFloat(), getXmLine(0.0) - bitmapJersey!!.height/4, Paint())
        canvas.drawText("1. Goalkeeper", p.x.toFloat()+bitmapJersey!!.width/2, ((getXmLine(0.0) - bitmapJersey!!.height/4) + 150), paintPitchText)

        canvas.drawBitmap(bitmapJersey, left, getXmLine(line13m), Paint())
        canvas.drawText("2. Left corner back", left+bitmapJersey!!.width/2, getXmLine(line13m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, p.x.toFloat(), getXmLine(line13m), Paint())
        canvas.drawText("3. Full back", p.x.toFloat()+bitmapJersey!!.width/2, getXmLine(line13m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, right, getXmLine(line13m), Paint())
        canvas.drawText("4. Right corner back", right+bitmapJersey!!.width/2, getXmLine(line13m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, left, getXmLine(line30m), Paint())
        canvas.drawText("5. Left half back", left+bitmapJersey!!.width/2, getXmLine(line30m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, p.x.toFloat(), getXmLine(line30m), Paint())
        canvas.drawText("6. Center back", p.x.toFloat()+bitmapJersey!!.width/2, getXmLine(line30m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, right, getXmLine(line30m), Paint())
        canvas.drawText("7. Right half back", right+bitmapJersey!!.width/2, getXmLine(line30m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, p.x.toFloat()-100, getXmLine(line50m), Paint())
        canvas.drawText("8. Midfield A", p.x.toFloat()-100+bitmapJersey!!.width/2, getXmLine(line50m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, p.x.toFloat()+100, getXmLine(line50m), Paint())
        canvas.drawText("9. Midfield B", p.x.toFloat()+100+bitmapJersey!!.width/2, getXmLine(line50m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, left, getXmLine(line70m), Paint())
        canvas.drawText("10. Left half forward", left+bitmapJersey!!.width/2, getXmLine(line70m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, p.x.toFloat(), getXmLine(line70m), Paint())
        canvas.drawText("11. Center forward", p.x.toFloat()+bitmapJersey!!.width/2, getXmLine(line70m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, right, getXmLine(line70m), Paint())
        canvas.drawText("12. Right half forward", right+bitmapJersey!!.width/2, getXmLine(line70m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, left, getXmLine(line87m), Paint())
        canvas.drawText("13. Left corner forward", left+bitmapJersey!!.width/2, getXmLine(line87m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, p.x.toFloat(), getXmLine(line87m), Paint())
        canvas.drawText("14. Full forward", p.x.toFloat()+bitmapJersey!!.width/2, getXmLine(line87m)+150, paintPitchText)

        canvas.drawBitmap(bitmapJersey, right, getXmLine(line87m), Paint())
        canvas.drawText("15. Right corner forward", right+bitmapJersey!!.width/2, getXmLine(line87m)+150, paintPitchText)

    }

    private fun getXmLine(xLineInMetres: Double): Float {
        return (rectPitchPerimeter.top + xLineInMetres * pixelsPerMetre).toFloat()
    }
}
