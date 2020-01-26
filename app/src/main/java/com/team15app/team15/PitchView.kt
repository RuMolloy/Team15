package com.team15app.team15

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*
import android.view.MotionEvent
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.team15app.team15.adapters.JerseyPagerAdapter
import com.team15app.team15.listeners.OnTeamClickListener
import android.R.attr.bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable




class PitchView : View, ViewPager.OnPageChangeListener {
    private val line13m = 13.0
    private val pitchWidthInMetres = 90.0
    private val pitchLengthInMetres = 145.0
    private val borderX = 0
    private val borderY = 40
    private var paintPitchHashtag: Paint = Paint()
    private var paintPitchText: Paint = Paint()
    private var paintJerseyNumberText: Paint = Paint()
    private var paintJerseyNumberStroke: Paint = Paint()
    private var paintTranslucent: Paint = Paint()
    private var paintPlayerNumberAndNameRect: Paint = Paint()

    private var rectPitchPerimeter = Rect(
        borderX,
        borderY,
        this.resources.displayMetrics.widthPixels - borderX,
        0
    )

    private val pitchWidthInPixels = rectPitchPerimeter.right - rectPitchPerimeter.left
    private var pixelsPerMetre = pitchWidthInPixels / pitchWidthInMetres
    private val screenResolutionWidthInPixels = this.resources.displayMetrics.widthPixels.toFloat()
    private val halfScreenResolutionWidthInPixels = screenResolutionWidthInPixels / 2
    private val textOffsetY = 150

    private var bitmapJerseyGoalkeeper: Bitmap? = null
    private var bitmapJerseyOutfield: Bitmap? = null

    lateinit var mapOfPlayers: TreeMap<Int, Player>

    private lateinit var rbtngJersey: RadioGroup
    private lateinit var viewPagerJersey: ViewPager

    private lateinit var etPlayerNumber: EditText
    private lateinit var etPlayerName: EditText
    private var viewPagerIndex = 0

    private var isDrawingPitchDebugLines = false

    private lateinit var myOnTeamClickListener: OnTeamClickListener

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
        paintPitchText = Paint()
        paintPitchText.color = ContextCompat.getColor(context, R.color.colorPitchLinesAndText)
        paintPitchText.textSize = resources.getDimension(R.dimen.font_player_name)
        paintPitchText.textAlign = Paint.Align.CENTER

        paintJerseyNumberText = Paint()
        paintJerseyNumberText.color = ContextCompat.getColor(context, R.color.colorPitchLinesAndText)
        paintJerseyNumberText.textSize = resources.getDimension(R.dimen.font_player_number)
        paintJerseyNumberText.textAlign = Paint.Align.CENTER
        paintJerseyNumberText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintJerseyNumberText.isAntiAlias = true

        paintJerseyNumberStroke = Paint()
        paintJerseyNumberStroke.color = ContextCompat.getColor(context, R.color.colorPlayerNameBackground)
        paintJerseyNumberStroke.style = Paint.Style.STROKE
        paintJerseyNumberStroke.strokeWidth = 2f
        paintJerseyNumberStroke.textSize = resources.getDimension(R.dimen.font_player_number)
        paintJerseyNumberStroke.textAlign = Paint.Align.CENTER
        paintJerseyNumberStroke.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintJerseyNumberStroke.isAntiAlias = true

        paintPitchHashtag = Paint()
        paintPitchHashtag.color = ContextCompat.getColor(context, R.color.colorPitchLinesAndText)
        paintPitchHashtag.textSize = resources.getDimension(R.dimen.font_hashtag)
        paintPitchHashtag.textAlign = Paint.Align.CENTER
        paintPitchHashtag.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paintPitchHashtag.alpha = 125

        paintTranslucent = Paint()
        paintTranslucent.color = Color.TRANSPARENT
        paintTranslucent.isAntiAlias = true

        paintPlayerNumberAndNameRect = Paint()
        paintPlayerNumberAndNameRect.color = ContextCompat.getColor(context, R.color.colorPlayerNameBackground)
        paintPlayerNumberAndNameRect.alpha = 50

        bitmapJerseyGoalkeeper = BitmapFactory.decodeResource(this.resources, R.drawable.jersey_default)
        bitmapJerseyOutfield = BitmapFactory.decodeResource(this.resources, R.drawable.jersey_default)

        mapOfPlayers = TreeMap()

        for(item in resources.getStringArray(R.array.team_positions).indices){
            val playerPosition = resources.getStringArray(R.array.team_positions)[item]
            val player = Player(playerPosition, (item+1).toString())
            setPlayerLocationParameters(player)
            mapOfPlayers[item] = player
        }
    }

    public override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectPitchPerimeter.bottom = h - borderY

        for(item in resources.getStringArray(R.array.team_positions).indices){
            val playerPosition = resources.getStringArray(R.array.team_positions)[item]
            val player = Player(playerPosition, (item+1).toString())
            setPlayerLocationParameters(player)
            mapOfPlayers[item] = player
        }
    }

    public override fun onDraw(canvas: Canvas) {
        //drawPitchBackground(canvas)
        drawHashtag(canvas)
        drawJersey(canvas)
    }

    private fun drawHashtag(canvas: Canvas){
        val centreX = halfScreenResolutionWidthInPixels - bitmapJerseyOutfield!!.width/2
        val pitchLeft = (centreX - centreX/1.75).toInt()
        val pitchRight = (centreX + centreX/1.6).toInt()
        val offsetPlayerJersey = (resources.getDimension(R.dimen.player_jersey_offset).toInt())

        canvas.drawText(resources.getString(R.string.app_handle),
            pitchLeft.toFloat() + bitmapJerseyOutfield!!.width/2,
            (getLineMinusJerseyOffset(0) + offsetPlayerJersey).toFloat() + bitmapJerseyOutfield!!.height/2 + paintPitchHashtag.textSize.toInt(),
            paintPitchHashtag)

        canvas.drawText(resources.getString(R.string.app_hashtag),
            pitchRight.toFloat() + bitmapJerseyOutfield!!.width/2,
            (getLineMinusJerseyOffset(0) + offsetPlayerJersey).toFloat() + bitmapJerseyOutfield!!.height/2 + paintPitchHashtag.textSize.toInt(),
            paintPitchHashtag)
    }

    fun setJerseyBitmaps(jerseyGoalkeeper: Int, jerseyOutfield: Int){
        setJerseyGoalkeeperBitmap(jerseyGoalkeeper)
        setJerseyOutfieldBitmap(jerseyOutfield)
    }

    fun setJerseyGoalkeeperBitmap(jerseyGoalkeeper: Int){
        bitmapJerseyGoalkeeper = BitmapFactory.decodeResource(context.resources, jerseyGoalkeeper)
    }

    fun setJerseyOutfieldBitmap(jerseyOutfield: Int){
        bitmapJerseyOutfield = BitmapFactory.decodeResource(context.resources, jerseyOutfield)
    }

    private fun drawJersey(canvas: Canvas){
        for(Item in mapOfPlayers) {
            val player = Item.value

            canvas.drawBitmap(getJersey(player), player.getJerseyPoint()!!.x.toFloat(), player.getJerseyPoint()!!.y.toFloat(), Paint())
            canvas.drawRect(player.getJerseyRect(), paintTranslucent)

            canvas.drawRect(player.getNameRect(), paintPlayerNumberAndNameRect)
            canvas.drawText(player.getName(), player.getNamePoint()!!.x.toFloat(), player.getNamePoint()!!.y.toFloat(), paintPitchText)
            canvas.drawText(player.getNumber(), player.getJerseyPoint()!!.x.toFloat() + bitmapJerseyOutfield!!.width/2, player.getJerseyPoint()!!.y.toFloat()+ bitmapJerseyOutfield!!.height/2, paintJerseyNumberText)
            canvas.drawText(player.getNumber(), player.getJerseyPoint()!!.x.toFloat() + bitmapJerseyOutfield!!.width/2, player.getJerseyPoint()!!.y.toFloat()+ bitmapJerseyOutfield!!.height/2, paintJerseyNumberStroke)

            if(isDrawingPitchDebugLines){
                //canvas.drawRect(rectPitchPerimeter, paintPlayerNumberAndNameRect)
                canvas.drawLine(rectPitchPerimeter.left.toFloat(), getLine(0).toFloat(), rectPitchPerimeter.right.toFloat(), getLine(0).toFloat(), paintPitchText)
                canvas.drawLine(rectPitchPerimeter.left.toFloat(), getLine(1).toFloat(), rectPitchPerimeter.right.toFloat(), getLine(1).toFloat(), paintPitchText)
                canvas.drawLine(rectPitchPerimeter.left.toFloat(), getLine(2).toFloat(), rectPitchPerimeter.right.toFloat(), getLine(2).toFloat(), paintPitchText)
                canvas.drawLine(rectPitchPerimeter.left.toFloat(), getLine(3).toFloat(), rectPitchPerimeter.right.toFloat(), getLine(3).toFloat(), paintPitchText)
                canvas.drawLine(rectPitchPerimeter.left.toFloat(), getLine(4).toFloat(), rectPitchPerimeter.right.toFloat(), getLine(4).toFloat(), paintPitchText)
                canvas.drawLine(rectPitchPerimeter.left.toFloat(), getLine(5).toFloat(), rectPitchPerimeter.right.toFloat(), getLine(5).toFloat(), paintPitchText)
                canvas.drawLine(rectPitchPerimeter.left.toFloat(), getLine(6).toFloat(), rectPitchPerimeter.right.toFloat(), getLine(6).toFloat(), paintPitchText)
            }
        }
    }

    private fun getJersey(player: Player): Bitmap?{
        return if(player.getDefaultName() == resources.getStringArray(R.array.team_positions)[0]) bitmapJerseyGoalkeeper else bitmapJerseyOutfield
    }

    private fun setPlayerLocationParameters(player: Player){
        val centreX = halfScreenResolutionWidthInPixels - bitmapJerseyOutfield!!.width/2
        val centreY = rectPitchPerimeter.bottom/2 - bitmapJerseyOutfield!!.height/2
        val pitchCentre = Point(centreX.toInt(), centreY)
        val pitchLeft = (centreX - centreX/1.5).toInt()
        val pitchRight = (centreX + centreX/1.5).toInt()
        val offsetPlayerText = bitmapJerseyOutfield!!.width/2
        val offsetPlayerJersey = (resources.getDimension(R.dimen.player_jersey_offset).toInt())

        val midfielderLeft = (centreX - centreX/3.0).toInt()
        val midfielderRight = (centreX + centreX/3.0).toInt()

        var p = Point(pitchLeft, getXmLine(line13m))
        var r = Point(p.x+offsetPlayerText, getYmLine(line13m))

        when (player.getDefaultName()) {
            //Goalkeeper
            resources.getStringArray(R.array.team_positions)[0] -> {
                p = Point(pitchCentre.x, getLineMinusJerseyOffset(0) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(0) + offsetPlayerJersey)
            }
            //Full back line
            resources.getStringArray(R.array.team_positions)[1] -> {
                p = Point(pitchLeft, getLineMinusJerseyOffset(1) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(1) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[2] -> {
                p = Point(pitchCentre.x, getLineMinusJerseyOffset(1) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(1) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[3] -> {
                p = Point(pitchRight, getLineMinusJerseyOffset(1) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(1) + offsetPlayerJersey)
            }
            //Half back line
            resources.getStringArray(R.array.team_positions)[4] -> {
                p = Point(pitchLeft, getLineMinusJerseyOffset(2) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(2) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[5] -> {
                p = Point(pitchCentre.x, getLineMinusJerseyOffset(2) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(2) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[6] -> {
                p = Point(pitchRight, getLineMinusJerseyOffset(2) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(2) + offsetPlayerJersey)
            }
            //Midfielder
            resources.getStringArray(R.array.team_positions)[7] -> {
                p = Point(midfielderLeft, getLineMinusJerseyOffset(3) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(3) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[8] -> {
                p = Point(midfielderRight, getLineMinusJerseyOffset(3) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(3) + offsetPlayerJersey)
            }
            //Half forward line
            resources.getStringArray(R.array.team_positions)[9] -> {
                p = Point(pitchLeft, getLineMinusJerseyOffset(4) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(4) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[10] -> {
                p = Point(pitchCentre.x, getLineMinusJerseyOffset(4) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(4) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[11] -> {
                p = Point(pitchRight, getLineMinusJerseyOffset(4) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(4) + offsetPlayerJersey)
            }
            //Full forward line
            resources.getStringArray(R.array.team_positions)[12] -> {
                p = Point(pitchLeft, getLineMinusJerseyOffset(5) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(5) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[13] -> {
                p = Point(pitchCentre.x, getLineMinusJerseyOffset(5) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(5) + offsetPlayerJersey)
            }
            resources.getStringArray(R.array.team_positions)[14] -> {
                p = Point(pitchRight, getLineMinusJerseyOffset(5) + offsetPlayerJersey)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(5) + offsetPlayerJersey)
            }
        }

        player.setJerseyPoint(p)
        player.setNamePoint(r)

        val rectJersey = Rect(player.getJerseyPoint()!!.x,
            player.getJerseyPoint()!!.y,
            player.getJerseyPoint()!!.x + bitmapJerseyOutfield!!.width,
            player.getJerseyPoint()!!.y + bitmapJerseyOutfield!!.height)

        player.setJerseyRect(rectJersey)
        setPlayerNumberAndNameRect(player)
    }

    private fun getXmLine(xLineInMetres: Double): Int {
        return (rectPitchPerimeter.top + xLineInMetres * pixelsPerMetre).toInt()
    }

    private fun getLine(line: Int): Int {
        val numLines = (rectPitchPerimeter.height())/6.0
        //  - (bitmapJerseyOutfield!!.height)/2).toInt()
        return (rectPitchPerimeter.top + (numLines*line).toInt())
    }

    private fun getLineMinusJerseyOffset(line: Int): Int {
        return getLine(line) - (bitmapJerseyOutfield!!.height)/2
    }

    private fun getLinePlusJerseyOffset(line: Int): Int {
        return getLine(line) + (bitmapJerseyOutfield!!.height)/2 + (resources.getDimension(R.dimen.player_name_offset).toInt())
    }

    private fun getYmLine(xLineInMetres :Double): Int{
        return getXmLine(xLineInMetres) + textOffsetY
    }

    fun initJerseyListener(myOnTeamClickListener: OnTeamClickListener){
        this.myOnTeamClickListener = myOnTeamClickListener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x.toInt()
        val eventY = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val player = isMouseEventOnThePlayer(eventX, eventY)
                if(player != null){
                    val view = inflate(context, R.layout.dialog_edit_player, null)
                    val isGoalkeeper = player.getDefaultName().contains(resources.getString(R.string.goalkeeper))
                    val pageAdapter = JerseyPagerAdapter(context, myOnTeamClickListener, isGoalkeeper)
                    viewPagerJersey = view.findViewById(R.id.viewPager)
                    viewPagerJersey.adapter = pageAdapter
                    viewPagerJersey.addOnPageChangeListener(this)
                    //viewPagerJersey.offscreenPageLimit = JerseyEnum.values().size

                    rbtngJersey = view.findViewById(R.id.rbHomeGroup)

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(R.string.default_edit_player_title)
                    builder.setView(view)
                    builder.apply {
                        setPositiveButton(R.string.ok) { _, _ ->
                            if(etPlayerNumber.text.toString().isNotEmpty()) player.setNumber(etPlayerNumber.text.toString())
                            player.setCustomName(etPlayerName.text.toString())
                            setPlayerNumberAndNameRect(player)

                            viewPagerIndex = viewPagerJersey.currentItem

                            invalidate() //this will call the onDraw() method so the player's name gets updated
                        }
                        setNegativeButton(R.string.cancel) { _, _ ->
                        }
                    }
                    var dialog = builder.create()
                    dialog.show()

                    etPlayerNumber = view.findViewById(R.id.et_edit_player_number)
                    etPlayerNumber.setText(player.getNumber())

                    etPlayerName = view.findViewById(R.id.et_edit_player_name)
                    if(player.isDefaultName()) etPlayerName.hint = player.getName()
                    else etPlayerName.setText(player.getName())
                    etPlayerName.requestFocus()

                    viewPagerJersey.currentItem = viewPagerIndex
                }
            }
        }
        return true
    }

    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

    }

    override fun onPageSelected(p0: Int) {
        if(p0 >= viewPagerJersey.adapter!!.count){
            rbtngJersey.check(rbtngJersey.getChildAt(0).id)
        }
        else{
            rbtngJersey.check(rbtngJersey.getChildAt(p0).id)
        }
    }

    fun setPlayerNumberAndNameRect(player: Player){
        val padding = 10
        val textWidth = (paintPitchText.measureText(player.getName()) / 2).toInt() + padding
        val textSize = paintPitchText.textSize.toInt()
        val rectText = Rect(player.getNamePoint()!!.x - textWidth, player.getNamePoint()!!.y - textSize, player.getNamePoint()!!.x + textWidth, player.getNamePoint()!!.y + padding)

        player.setNameRect(rectText)
    }

    private fun isMouseEventOnThePlayer(eventX: Int, eventY: Int): Player? {
        for(Item in mapOfPlayers) {
            val player = Item.value
            if(player.getJerseyRect()!!.contains(eventX, eventY) ||
                    player.getNameRect()!!.contains(eventX, eventY)){
                return player
            }
        }
        return null
    }
}
