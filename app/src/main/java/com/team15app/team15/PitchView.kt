package com.team15app.team15

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.team15app.team15.dialogs.PlayerDialogFragment.OnFinishEditDialog
import com.team15app.team15.adapters.TeamNameAdapter
import com.team15app.team15.dialogs.PlayerDialogFragment
import com.team15app.team15.listeners.OnTeamClickListener
import java.util.*


class PitchView : View, GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, OnFinishEditDialog {
    private val line13m = 13.0
    private val pitchWidthInMetres = 90.0
    private val pitchLengthInMetres = 145.0
    private val borderX = 0
    private val borderY = 40
    private var paintPitchHashtag: Paint = Paint()
    private var paintPitchText: Paint = Paint()
    private var paintJerseyNumberText: Paint = Paint()
    private var paintJerseyNumberStroke: Paint = Paint()

    private var paintJerseySelected: Paint = Paint()
    private var paintJerseyIsNotOverlapping: Paint = Paint()
    private var paintJerseyIsOverlapping: Paint = Paint()

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

    private var isDrawingPitchDebugLines = false

    private lateinit var myOnTeamClickListener: OnTeamClickListener

    private var mContext: Context
    private lateinit var mDetector: GestureDetectorCompat
    private var timeWhenDownPressed: Long = 0
    private val DEBUG_TAG = "Gestures"

    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mContext = context
        init()
    }

    private fun init() {
        initGestureDetector()
        initUIResources()
    }

    private fun initGestureDetector(){
        mDetector = GestureDetectorCompat(mContext, this)
        mDetector.setOnDoubleTapListener(this)
        mDetector.setIsLongpressEnabled(false)
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

        paintJerseySelected = Paint()
        paintJerseySelected.color = Color.BLUE
        paintJerseySelected.style = Paint.Style.STROKE
        paintJerseySelected.strokeWidth = 3f

        paintJerseyIsNotOverlapping = Paint()
        paintJerseyIsNotOverlapping.color = Color.RED
        paintJerseyIsNotOverlapping.style = Paint.Style.STROKE
        paintJerseyIsNotOverlapping.strokeWidth = 3f

        paintJerseyIsOverlapping = Paint()
        paintJerseyIsOverlapping.color = Color.GREEN
        paintJerseyIsOverlapping.style = Paint.Style.STROKE
        paintJerseyIsOverlapping.strokeWidth = 3f

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
                p = Point(pitchCentre.x, getLineMinusJerseyOffset(0) + offsetPlayerJersey+5)
                r = Point(p.x+offsetPlayerText, getLinePlusJerseyOffset(0) + offsetPlayerJersey+5)
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

        player.setJerseyPointDefault(p)
        player.setJerseyPointCustom(p)
        player.setNamePoint(r)

        val rectJersey = Rect(player.getJerseyPoint()!!.x,
            player.getJerseyPoint()!!.y,
            player.getJerseyPoint()!!.x + bitmapJerseyOutfield!!.width,
            player.getJerseyPoint()!!.y + bitmapJerseyOutfield!!.height)
        player.setJerseyRectDefault(rectJersey)
        player.setJerseyRectCustom(rectJersey)
        setPlayerNumberAndNameRect(player)
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
        for(player in mapOfPlayers.values) {

            canvas.drawBitmap(getJersey(player), player.getJerseyPoint()!!.x.toFloat(), player.getJerseyPoint()!!.y.toFloat(), Paint())
            canvas.drawRect(player.getJerseyRect(), getJerseyBorder(player))
            canvas.drawRect(player.getNameRect(), paintPlayerNumberAndNameRect)
            canvas.drawText(player.getName(), player.getNamePoint()!!.x.toFloat(), player.getNamePoint()!!.y.toFloat(), paintPitchText)
            canvas.drawText(player.getNumber(), player.getJerseyPoint()!!.x.toFloat() + bitmapJerseyOutfield!!.width/2, player.getJerseyPoint()!!.y.toFloat() + bitmapJerseyOutfield!!.height/2, paintJerseyNumberText)
            canvas.drawText(player.getNumber(), player.getJerseyPoint()!!.x.toFloat() + bitmapJerseyOutfield!!.width/2, player.getJerseyPoint()!!.y.toFloat() + bitmapJerseyOutfield!!.height/2, paintJerseyNumberStroke)

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

    private fun getJerseyBorder(player: Player): Paint{
        return if(isAnyPlayerSelected()){
            if(player.isSelected()){
                paintJerseySelected
            } else{
                if(player.isOverlapping()){
                    paintJerseyIsOverlapping
                } else{
                    paintJerseyIsNotOverlapping
                }
            }
        } else{
            paintTranslucent
        }
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
        if (mDetector.onTouchEvent(event) === true) {
            //Fling or other gesture detected (not long-press because it is disabled)
            if (event.action == MotionEvent.ACTION_MOVE) {
                timeWhenDownPressed = System.currentTimeMillis()
                val playerSelected = getSelectedPlayer()
                if(playerSelected != null) {
                    playerSelected.setJerseyPointCustom(Point(eventX-(bitmapJerseyOutfield!!.width/2), eventY-(bitmapJerseyOutfield!!.height/2)))
                    playerSelected.setJerseyRectCustom(Rect(playerSelected.getJerseyPoint()!!.x, playerSelected.getJerseyPoint()!!.y, playerSelected.getJerseyPoint()!!.x + bitmapJerseyOutfield!!.width, playerSelected.getJerseyPoint()!!.y + bitmapJerseyOutfield!!.height))
                    invalidate()

                    resetOverlappedPlayers()
                    isPlayerOverlapping(playerSelected)?.setIsOverlapping(true)
                    invalidate()
                }
            }
        } else {
            if (event.action == MotionEvent.ACTION_MOVE) {
                /*Check if user is actually longpressing, not slow-moving
                  if current position differs much then press positon then discard whole thing
                  if position change is minimal then after 0.5s that is a longpress. You can now process your other gestures*/
                val timeNow = System.currentTimeMillis()
                if(timeNow - timeWhenDownPressed > 200){
                    timeWhenDownPressed = timeNow
                    val player = isMouseEventOnThePlayer(eventX, eventY)
                    if(player != null && !player.isSelected()){
                        resetSelectedPlayers()
                        player.setIsSelected(true)
                        this.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        invalidate()
                    }
                }
            }
            if (event.action == MotionEvent.ACTION_UP) {
                //Log.e(DEBUG_TAG, "Action up")
                swapPlayerName()
            }
        }
        return true
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onSingleTapConfirmed: $event")
        val eventX = event.x.toInt()
        val eventY = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val player = isMouseEventOnThePlayer(eventX, eventY)
                if(player != null){
                    if(!isAnyPlayerSelected()){
                        val mainActivity = (mContext as MainActivity)
                        val args = Bundle()
                        args.putSerializable(resources.getString(R.string.default_edit_player_title), mapOfPlayers)
                        args.putInt(resources.getString(R.string.outfielder), player.getDefaultNumber().toInt())

                        val dialogFragment =
                            PlayerDialogFragment()
                        dialogFragment.arguments = args
                        dialogFragment.show(mainActivity.supportFragmentManager, "")
                        mainActivity.supportFragmentManager.executePendingTransactions();
                        dialogFragment.dialog.window.clearFlags(
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                    or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                        )
                        dialogFragment.setOnFinishEditDialogListener(this)
                    }
                    else{
                        val isThisPlayerSelected = player.isSelected()
                        setPlayersUnselected()
                        if(!isThisPlayerSelected){
                            player.setIsSelected(true)
                        }
                        invalidate()
                    }
                }
                else{
                    setPlayersUnselected()
                    invalidate()
                }
            }
        }
        return true
    }

    override fun onFinishEditDialog(isChangeAccepted: Boolean) {
        //Log.d(DEBUG_TAG, "onFinishEditDialog")
        for(player in mapOfPlayers.values) {
            if(isChangeAccepted){
                if(player.getEditNumber().isNotEmpty()) player.setCustomNumber(player.getEditNumber())
                if(player.getEditName().isNotEmpty()) player.setCustomName(player.getEditName())
                setPlayerNumberAndNameRect(player)
            }
            player.setEditNumber("")
            player.setEditName("")
        }
        invalidate()
    }

    override fun onDown(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onDown: $event")
        timeWhenDownPressed = System.currentTimeMillis()
        return true
    }

    override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        //Log.d(DEBUG_TAG, "onFling: $event1 $event2")
        swapPlayerName()
        return true
    }

    fun setPlayerNumberAndNameRect(player: Player){
        val padding = 10
        val textWidth = (paintPitchText.measureText(player.getName()) / 2).toInt() + padding
        val textSize = paintPitchText.textSize.toInt()
        val rectText = Rect(player.getNamePoint()!!.x - textWidth,
            player.getNamePoint()!!.y - textSize,
            player.getNamePoint()!!.x + textWidth,
            player.getNamePoint()!!.y + padding)

        player.setNameRect(rectText)
    }

    private fun getSelectedPlayer(): Player? {
        for(player in mapOfPlayers.values) {
            if(player.isSelected()){
                return player
            }
        }
        return null
    }

    private fun getOverlappedPlayer(): Player? {
        for(player in mapOfPlayers.values) {
            if(player.isOverlapping()){
                return player
            }
        }
        return null
    }

    private fun setPlayersUnselected(){
        for(player in mapOfPlayers.values) {
            player.setIsSelected(false)
        }
    }

    private fun resetSelectedPlayer(){
        val player = getSelectedPlayer()
        if(player != null) {
            player.setJerseyPointCustom(player.getJerseyPointDefault()!!)
            player.setJerseyRectCustom(player.getJerseyRectDefault()!!)
        }
    }

    fun resetSelectedPlayers(){
        for(player in mapOfPlayers.values) {
            player.setIsSelected(false)
        }
    }

    private fun resetOverlappedPlayers(){
        for(player in mapOfPlayers.values) {
            player.setIsOverlapping(false)
        }
    }

    private fun isMouseEventOnThePlayer(eventX: Int, eventY: Int): Player? {
        for(player in mapOfPlayers.values) {
            if(player.getJerseyRect()!!.contains(eventX, eventY) ||
                    player.getNameRect()!!.contains(eventX, eventY)){
                return player
            }
        }
        return null
    }

    private fun isPlayerOverlapping(playerSelected: Player): Player? {
        for(player in mapOfPlayers.values) {
            if(player != playerSelected){
                if(player.getJerseyRect()!!.centerX() < playerSelected.getJerseyRect()!!.centerX() + playerSelected.getJerseyRect()!!.width()
                    && player.getJerseyRect()!!.centerX() + player.getJerseyRect()!!.width() > playerSelected.getJerseyRect()!!.centerX()
                    && player.getJerseyRect()!!.centerY() < playerSelected.getJerseyRect()!!.centerY() + playerSelected.getJerseyRect()!!.height()
                    && player.getJerseyRect()!!.centerY() + player.getJerseyRect()!!.height() > playerSelected.getJerseyRect()!!.centerY()){
                    return player
                }
            }
        }
        return null
    }

    private fun isPlayerNumberInUse(selectedPlayer: Player, number: String): String{
        for(player in mapOfPlayers.values) {
            if(player != selectedPlayer && player.getNumber() == number){
                return player.getName()
            }
        }
        return ""
    }

    private fun isAnyPlayerSelected(): Boolean{
        for(player in mapOfPlayers.values) {
            if(player.isSelected()){
                return true
            }
        }
        return false
    }

    private fun swapPlayerName(){
        resetSelectedPlayer()
        invalidate()

        val playerOverlapped = getOverlappedPlayer()
        val playerSelected = getSelectedPlayer()
        if(playerOverlapped != null && playerSelected != null){
//            openSwapPlayer(playerSelected, playerOverlapped)

            val p  = playerSelected.getJerseyPointDefault()
            val r = playerSelected.getJerseyRectDefault()
            val np = playerSelected.getNamePoint()
            val nr = playerSelected.getNameRect()

//            playerSelected.setJerseyPointDefault(playerOverlapped.getJerseyPointDefault())
//            playerSelected.setJerseyRectDefault(playerOverlapped.getJerseyRectDefault())
//            playerSelected.setJerseyPointCustom(playerOverlapped.getJerseyPointDefault())
//            playerSelected.setJerseyRectCustom(playerOverlapped.getJerseyRectDefault())
            playerSelected.setNamePoint(playerOverlapped.getNamePoint())
            playerSelected.setNameRect(playerOverlapped.getNameRect())
            setPlayerNumberAndNameRect(playerSelected)

//            playerOverlapped.setJerseyPointDefault(p)
//            playerOverlapped.setJerseyRectDefault(r)
//            playerOverlapped.setJerseyPointCustom(p)
//            playerOverlapped.setJerseyRectCustom(r)
            playerOverlapped.setNamePoint(np)
            playerOverlapped.setNameRect(nr)
            setPlayerNumberAndNameRect(playerOverlapped)

            resetSelectedPlayers()
            resetOverlappedPlayers()

            invalidate()
        }
    }

    private fun openSwapPlayer(playerA: Player, playerB: Player){
        val view = inflate(context, R.layout.dialog_custom_title, null)
        val tvTitle = view.findViewById<TextView>(R.id.tv_team_name)
        tvTitle.setText(R.string.action_swap)

        val builder = AlertDialog.Builder(context, R.style.DialogWindowTitle_Holo)
        builder.setCustomTitle(view)
        builder.setTitle(R.string.action_swap)

        var mainActivity = (mContext as MainActivity)
        val row = mainActivity.layoutInflater.inflate(R.layout.dialog_swap_player, null)
        var isGoalkeeper = playerA.getDefaultName().contains(resources.getString(R.string.goalkeeper))
        val ivPlayerA = row.findViewById<ImageView>(R.id.iv_goalkeeper)
        when {
            isGoalkeeper -> ivPlayerA.setImageBitmap(bitmapJerseyGoalkeeper)
            else -> ivPlayerA.setImageBitmap(bitmapJerseyOutfield)
        }
        val tvPlayerA = row.findViewById<TextView>(R.id.tv_goalkeeper)
        tvPlayerA.text = playerA.getName()

        isGoalkeeper = playerB.getDefaultName().contains(resources.getString(R.string.goalkeeper))
        val ivPlayerB = row.findViewById<ImageView>(R.id.iv_outfielder)
        when {
            isGoalkeeper -> ivPlayerB.setImageBitmap(bitmapJerseyGoalkeeper)
            else -> ivPlayerB.setImageBitmap(bitmapJerseyOutfield)
        }
        val tvPlayerB = row.findViewById<TextView>(R.id.tv_outfielder)
        tvPlayerB.text=playerB.getName()

        val swapOptions = resources.getStringArray(R.array.swap_options)
        val listOfSwapOptions = ArrayList<String>()
        for(swapOption in swapOptions){
            listOfSwapOptions.add(swapOption)
        }

        val viewManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        val viewAdapter = TeamNameAdapter(listOfSwapOptions, true, myOnTeamClickListener)

        val rvTeams:RecyclerView = row.findViewById<RecyclerView>(R.id.rv_team_names).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

            // adds a line between each recyclerview item
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        builder.setView(row)
        val dialogSwapOption = builder.create()
        dialogSwapOption.show()

        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.48).toInt()

        dialogSwapOption.window.setLayout(width, height)
    }

    override fun onLongPress(event: MotionEvent) {
        //Log.d(DEBUG_TAG, "onLongPress: $event")
    }

    override fun onShowPress(event: MotionEvent) {
        //Log.d(DEBUG_TAG, "onShowPress: $event")
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onSingleTapUp: $event")
        return true
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onDoubleTap: $event")
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        //Log.d(DEBUG_TAG, "onDoubleTapEvent: $event")
        return true
    }

    override fun onScroll(event1: MotionEvent, event2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        //Log.d(DEBUG_TAG, "onScroll: $event1 $event2")
        return true
    }
}
