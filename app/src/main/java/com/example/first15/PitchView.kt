package com.example.first15

import android.content.Context
import android.graphics.*
import android.graphics.Paint.Style
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.View
import java.util.*
import android.view.MotionEvent
import android.widget.EditText


class PitchView : View  {

    private val line13m = 13.0
    private val line30m = 30.0
    private val line50m = 50.0
    private val line70m = 70.0
    private val line87m = 87.0
    private val pitchWidthInMetres = 90.0
    private val pitchLengthInMetres = 145.0
    private val borderX = 40
    private val borderY = 40
    private var paintPitchPerimeter: Paint = Paint()
    private var paintPitchText: Paint = Paint()
    private var paintTranslucent: Paint = Paint()
    private var paintTextRect: Paint = Paint()

    private var rectPitchPerimeter = Rect(
        borderX,
        borderY,
        this.resources.displayMetrics.widthPixels - borderX,
        this.resources.displayMetrics.heightPixels - borderY
    )

    private val pitchWidthInPixels = rectPitchPerimeter.right - rectPitchPerimeter.left
    private var pixelsPerMetre = pitchWidthInPixels / pitchWidthInMetres
    private val screenResolutionWidthInPixels = this.resources.displayMetrics.widthPixels.toFloat()
    private val halfScreenResolutionWidthInPixels = screenResolutionWidthInPixels / 2
    private val textOffsetY = 150

    private var bitmapJerseyGoalkeeper: Bitmap? = null
    private var bitmapJerseyOutfield: Bitmap? = null

    lateinit var mapOfPlayers: TreeMap<Int, Player>

    private lateinit var etPlayerNumber: EditText
    private lateinit var etPlayerName: EditText

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
        paintPitchText.textAlign = Paint.Align.CENTER

        paintTranslucent = Paint()
        paintTranslucent.color = Color.TRANSPARENT
        paintTranslucent.style = Paint.Style.STROKE
        paintTranslucent.isAntiAlias = true

        paintTextRect = Paint()
        paintTextRect.style = Style.FILL
        paintTextRect.color = ContextCompat.getColor(context, R.color.colorBorders)
        paintTextRect.alpha = 50

        bitmapJerseyGoalkeeper = BitmapFactory.decodeResource(this.resources, R.drawable.jersey_default)
        bitmapJerseyOutfield = BitmapFactory.decodeResource(this.resources, R.drawable.jersey_default)

        mapOfPlayers = TreeMap()

        for(item in resources.getStringArray(R.array.team_positions).indices){
            val playerPosition = resources.getStringArray(R.array.team_positions)[item]
            val player = Player(playerPosition, (item+1).toString())
            setPlayerPositionParameters(player)
            mapOfPlayers[item] = player
        }
    }

    public override fun onDraw(canvas: Canvas) {
        drawJersey(canvas)
    }

    fun setJerseyBitmaps(jerseyGoalkeeper: Int, jerseyOutfield: Int){
        bitmapJerseyGoalkeeper = BitmapFactory.decodeResource(context.resources, jerseyGoalkeeper)
        bitmapJerseyOutfield = BitmapFactory.decodeResource(context.resources, jerseyOutfield)
    }

    private fun drawJersey(canvas: Canvas){
        for(Item in mapOfPlayers) {
            val player = Item.value

            canvas.drawBitmap(getJersey(player), player.getBitmapPoint()!!.x.toFloat(), player.getBitmapPoint()!!.y.toFloat(), Paint())
            canvas.drawRect(player.getBitmapRect(), paintTranslucent)

            canvas.drawRect(player.getTextRect(), paintTextRect)
            canvas.drawText(player.getNumberAndName(), player.getTextPoint()!!.x.toFloat(), player.getTextPoint()!!.y.toFloat(), paintPitchText)
        }
    }

    private fun getJersey(player: Player): Bitmap?{
        return if(player.getDefaultName() == resources.getStringArray(R.array.team_positions)[0]) bitmapJerseyGoalkeeper else bitmapJerseyOutfield
    }

    private fun setPlayerPositionParameters(player: Player){
        val centreX = halfScreenResolutionWidthInPixels - bitmapJerseyOutfield!!.width/2
        val centreY = rectPitchPerimeter.bottom/2 - bitmapJerseyOutfield!!.height/2
        val pitchCentre = Point(centreX.toInt(), centreY)
        val pitchLeft = (centreX - centreX/1.6).toInt()
        val pitchRight = (centreX + centreX/1.6).toInt()
        val offset = bitmapJerseyOutfield!!.width/2

        var p = Point(pitchLeft, getXmLine(line13m))
        var r = Point(p.x+offset, getYmLine(line13m))
        val nameDefault: String = player.getDefaultName()

        when (nameDefault) {
            resources.getStringArray(R.array.team_positions)[0] -> {
                p = Point(pitchCentre.x, (getXmLine(0.0) - bitmapJerseyOutfield!!.height/4))
                r = Point(p.x+offset, (getYmLine(0.0) - bitmapJerseyOutfield!!.height/4))
            }
            resources.getStringArray(R.array.team_positions)[1] -> {
                p = Point(pitchLeft, getXmLine(line13m))
                r = Point(p.x+offset, getYmLine(line13m))
            }
            resources.getStringArray(R.array.team_positions)[2] -> {
                p = Point(pitchCentre.x, getXmLine(line13m))
                r = Point(p.x+offset, getYmLine(line13m))
            }
            resources.getStringArray(R.array.team_positions)[3] -> {
                p = Point(pitchRight, getXmLine(line13m))
                r = Point(p.x+offset, getYmLine(line13m))
            }
            resources.getStringArray(R.array.team_positions)[4] -> {
                p = Point(pitchLeft, getXmLine(line30m))
                r = Point(p.x+offset, getYmLine(line30m))
            }
            resources.getStringArray(R.array.team_positions)[5] -> {
                p = Point(pitchCentre.x, getXmLine(line30m))
                r = Point(p.x+offset, getYmLine(line30m))
            }
            resources.getStringArray(R.array.team_positions)[6] -> {
                p = Point(pitchRight, getXmLine(line30m))
                r = Point(p.x+offset, getYmLine(line30m))
            }
            resources.getStringArray(R.array.team_positions)[7] -> {
                p = Point(pitchCentre.x-100, getXmLine(line50m))
                r = Point(p.x+offset, getYmLine(line50m))
            }
            resources.getStringArray(R.array.team_positions)[8] -> {
                p = Point(pitchCentre.x+100, getXmLine(line50m))
                r = Point(p.x+offset, getYmLine(line50m))
            }
            resources.getStringArray(R.array.team_positions)[9] -> {
                p = Point(pitchLeft, getXmLine(line70m))
                r = Point(p.x+offset, getYmLine(line70m))
            }
            resources.getStringArray(R.array.team_positions)[10] -> {
                p = Point(pitchCentre.x, getXmLine(line70m))
                r = Point(p.x+offset, getYmLine(line70m))
            }
            resources.getStringArray(R.array.team_positions)[11] -> {
                p = Point(pitchRight, getXmLine(line70m))
                r = Point(p.x+offset, getYmLine(line70m))
            }
            resources.getStringArray(R.array.team_positions)[12] -> {
                p = Point(pitchLeft, getXmLine(line87m))
                r = Point(p.x+offset, getYmLine(line87m))
            }
            resources.getStringArray(R.array.team_positions)[13] -> {
                p = Point(pitchCentre.x, getXmLine(line87m))
                r = Point(p.x+offset, getYmLine(line87m))
            }
            resources.getStringArray(R.array.team_positions)[14] -> {
                p = Point(pitchRight, getXmLine(line87m))
                r = Point(p.x+offset, getYmLine(line87m))
            }
        }

        player.setBitmapPoint(p)
        player.setTextPoint(r)

        val rectBitmap = Rect(player.getBitmapPoint()!!.x, player.getBitmapPoint()!!.y, player.getBitmapPoint()!!.x + bitmapJerseyOutfield!!.width, player.getBitmapPoint()!!.y + bitmapJerseyOutfield!!.height)
        player.setBitmapRect(rectBitmap)
        setTextRect(player)
    }

    private fun getXmLine(xLineInMetres: Double): Int {
        return (rectPitchPerimeter.top + xLineInMetres * pixelsPerMetre).toInt()
    }

    private fun getYmLine(xLineInMetres :Double): Int{
        return getXmLine(xLineInMetres) + textOffsetY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x.toInt()
        val eventY = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val player = isMouseEventOnThePlayer(eventX, eventY)
                if(player != null){
                    val view = View.inflate(context, R.layout.dialog_edit_player, null)
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(R.string.default_edit_player_title)
                    builder.setView(view)
                    builder.apply {
                        setPositiveButton(R.string.ok) { _, _ ->
                            if(!etPlayerNumber.text.toString().isEmpty()) player.setNumber(etPlayerNumber.text.toString())
                            player.setCustomName(etPlayerName.text.toString())
                            setTextRect(player)

                            invalidate() //this will call the onDraw() method so the player's name gets updated
                        }
                        setNegativeButton(R.string.cancel) { _, _ ->
                            // User cancelled the dialog
                        }
                    }
                    builder.show()

                    etPlayerNumber = view.findViewById(R.id.et_edit_player_number)
                    etPlayerNumber.setText(player.getNumber())

                    etPlayerName = view.findViewById(R.id.et_edit_player_name)
                    if(player.isDefaultName()) etPlayerName.hint = player.getName()
                    else etPlayerName.setText(player.getName())
                    etPlayerName.requestFocus()
                }
            }
        }
        return true
    }

    private fun setTextRect(player: Player){
        val padding = 10
        val textWidth = (paintPitchText.measureText(player.getNumberAndName()) / 2).toInt() + padding
        val textSize = paintPitchText.textSize.toInt()
        val rectText = Rect(player.getTextPoint()!!.x - textWidth, player.getTextPoint()!!.y - textSize, player.getTextPoint()!!.x + textWidth, player.getTextPoint()!!.y + padding)

        player.setTextRect(rectText)
    }

    private fun isMouseEventOnThePlayer(eventX: Int, eventY: Int): Player? {
        for(Item in mapOfPlayers) {
            val player = Item.value
            if(player.getBitmapRect()!!.contains(eventX, eventY) ||
                    player.getTextRect()!!.contains(eventX, eventY)){
                return player
            }
        }
        return null
    }
}
