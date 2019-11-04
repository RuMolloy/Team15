package com.team15app.team15

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.TextView
import java.util.*
import android.graphics.Bitmap
import android.os.Environment
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.ViewPager
import android.util.Log
import com.team15app.team15.adapters.CountyAdapter
import com.team15app.team15.adapters.CustomPagerAdapter
import com.team15app.team15.listeners.OnTeamClickListener
import java.io.*
import java.text.SimpleDateFormat


class MainActivity : OnTeamClickListener, AppCompatActivity(){

    companion object {
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE = 1
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE = 2
        const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_LOAD = 3
    }

    private lateinit var llTeamA: LinearLayout
    private lateinit var ivTeamA: ImageView

    private lateinit var rlTeamNames: RelativeLayout
    private lateinit var tvTeamNameA: TextView
    private lateinit var tvTeamNameB: TextView
    private lateinit var tvMatchInfo: TextView

    private lateinit var rlMatchInfo: RelativeLayout
    private lateinit var etMatchInfo: EditText

    private lateinit var llTeamB: LinearLayout
    private lateinit var ivTeamB: ImageView

    private lateinit var viewAdapter: CountyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var rvTeams: RecyclerView
    private lateinit var ivDeleteFile: ImageView

    private lateinit var dialogSelectTeam: AlertDialog

    private var tabPosition = 0
    private lateinit var tvClubDisclaimer: TextView

    //TODO refactor to just use one map -> mapOfTeamsClubInCounty
    private lateinit var mapOfTeamsClub: TreeMap<String, Team>
    private lateinit var mapOfTeamsCounty: TreeMap<String, Team>
    private lateinit var mapOfTeams: TreeMap<String, Team>
    private lateinit var mapOfTeamsClubInCounty: TreeMap<String, County>

    private lateinit var pitchView: PitchView

    private var fileName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //UI only designed for portrait, so disable landscape

        loadVars()
        loadTeams()
        initVars()
        deleteImagesIfHasPermission()
    }

    private fun loadVars(){
        llTeamA = findViewById(R.id.ll_team_a)
        ivTeamA = llTeamA.findViewById(R.id.iv_team_crest) as ImageView
        ivTeamA.setOnClickListener{
            openTeamSelectionDialog(getString(R.string.default_team_name_a))
        }

        rlTeamNames = findViewById(R.id.rl_team_names)
        tvTeamNameA = rlTeamNames.findViewById(R.id.tv_match_info_team_a)

        tvTeamNameB = rlTeamNames.findViewById(R.id.tv_match_info_team_b)
        tvTeamNameB.setOnClickListener{
            openTeamSelectionDialog(getString(R.string.default_team_name_b))
        }

        tvMatchInfo = rlTeamNames.findViewById(R.id.tv_match_info_name_read)
        tvMatchInfo.setOnClickListener{
            openMatchInfoDialog()
        }

        llTeamB = findViewById(R.id.ll_team_b)
        ivTeamB = llTeamB.findViewById(R.id.iv_team_crest) as ImageView
        ivTeamB.setOnClickListener{
            openTeamSelectionDialog(getString(R.string.default_team_name_b))
        }

        pitchView = findViewById(R.id.view_pitch)
    }


    private fun loadTeams(){
        // load clubs
        val listOfCountiesWithClubSupport = resources.getStringArray(R.array.team_names_counties_with_club_support).toList()
        val listOfClubs = resources.getStringArray(R.array.team_names_clubs).toList()

        mapOfTeamsClubInCounty = TreeMap()
        mapOfTeamsClub = TreeMap()
        for(club in listOfClubs){
            for(county in listOfCountiesWithClubSupport){
                if(isClubResourceFound(county, club)){
                    if(mapOfTeamsClubInCounty.containsKey(county)){
                        mapOfTeamsClubInCounty[county]!!.addClub(Club(club))
                    } else{
                        val clubs = ArrayList<Club>()
                        clubs.add(Club(club))
                        mapOfTeamsClubInCounty[county] = County(county, clubs)
                    }
                    mapOfTeamsClub[club+"_"+county] = getTeam(county, club)
                    break
                }
            }
        }
        //sort clubs alphabetically
        sortClubs()

        // load counties
        val listOfCounties = resources.getStringArray(R.array.team_names_counties).toList()
        mapOfTeamsCounty = TreeMap()
        for(county in listOfCounties){
            mapOfTeamsCounty[county] = getTeam(county, "")
        }

        mapOfTeams = TreeMap()
        mapOfTeams.putAll(mapOfTeamsCounty)
        mapOfTeams.putAll(mapOfTeamsClub)
    }

    private fun isClubResourceFound(county: String, club: String): Boolean{
        val crest = if(club.isEmpty()) getImageResourceId("crest_$county") else getImageResourceId("crest_" + club + "_" + county)
        return crest > 0 && !mapOfTeamsClub.containsKey(club+"_"+county)
    }

    private fun getTeam(county: String, club: String): Team{
        var crest = if(club.isEmpty()) getImageResourceId("crest_$county") else getImageResourceId("crest_" + club + "_" + county)
        if(crest <= 0){
            crest = R.drawable.crest_default
        }

        var jerseyGk = if(club.isEmpty()) getImageResourceId("jersey_" + county + "_gk") else getImageResourceId("jersey_" + club + "_" + county + "_gk")
        if(jerseyGk <= 0){
            jerseyGk = R.drawable.jersey_default
        }

        var jerseyOf = if(club.isEmpty()) getImageResourceId("jersey_" + county + "_of") else getImageResourceId("jersey_" + club + "_" + county + "_of")
        if(jerseyOf <= 0){
            jerseyOf = R.drawable.jersey_default
        }

        return Team(if(club.isEmpty()) county else club, crest, jerseyGk, jerseyOf)
    }

    private fun getImageResourceId(imageName: String): Int{
        val validImageName = imageName.toLowerCase().replace(Regex("[ /-]"), "_")

        return resources.getIdentifier(validImageName, "drawable", packageName)
    }

    private fun sortClubs(){
        //TODO find a better way to sort the clubs rather than creating a new list and clearing the old one
        for(Item in mapOfTeamsClubInCounty){
            val county = Item.value
            val list: ArrayList<Club> = ArrayList(county.getClubs().sortedWith(compareBy { it.name }))
            county.getClubs().clear()
            county.getClubs().addAll(list)
        }
    }

    private fun initVars(){
        setDefaultTeamANameAndCrest()
        setDefaultTeamBNameAndCrest()

        tvMatchInfo.setText(R.string.default_match_info)
        fileName = ""
    }

    private fun deleteImagesIfHasPermission(){
        when {isPermissionGranted() -> deleteImages()}
    }

    private fun openMatchInfoDialog(){
        val view = View.inflate(this, R.layout.dialog_edit_match_info, null)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.set_match_details)
        builder.setView(view)
        builder.apply {
            setPositiveButton(R.string.ok) { _, _ ->
                if(etMatchInfo.text.toString().isNotEmpty()){
                    tvMatchInfo.text = etMatchInfo.text.toString()
                }
                else{
                    tvMatchInfo.setText(R.string.default_match_info)
                }
            }
            setNegativeButton(R.string.cancel) { _, _ ->
            }
        }

        builder.show()

        rlMatchInfo = view.findViewById(R.id.rl_match_info_write)
        etMatchInfo = rlMatchInfo.findViewById(R.id.et_match_info_name_write)
        if(tvMatchInfo.text.isEmpty() || tvMatchInfo.text == getString(R.string.default_match_info)) etMatchInfo.setHint(R.string.default_match_info)
        else etMatchInfo.setText(tvMatchInfo.text.toString())
    }

    private fun openTeamSelectionDialog(title: String){
        val builder = AlertDialog.Builder(this, R.style.DialogWindowTitle_Holo)
        builder.setTitle(title)

        val listOfClubs = ArrayList(mapOfTeamsClubInCounty.values)

        val row = layoutInflater.inflate(R.layout.dialog_tabs, null)
        val tab = row.findViewById<TabLayout>(R.id.tab)
        val pageAdapter = CustomPagerAdapter(applicationContext, this, listOfClubs)
        tvClubDisclaimer = row.findViewById(R.id.tv_club_disclaimer)
        val viewPager = row.findViewById<ViewPager>(R.id.viewpager)
        viewPager.adapter = pageAdapter
        viewPager.currentItem = tabPosition
        tab.setupWithViewPager(viewPager)
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabPosition = tab.position
                showHideClubDisclaimer(tabPosition)
                pageAdapter.updateTabContents(tabPosition)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        builder.setView(row)
        showHideClubDisclaimer(tabPosition)
        dialogSelectTeam = builder.create()
        dialogSelectTeam.show()

        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.75).toInt()
        dialogSelectTeam.window.setLayout(width, height)
    }

    fun showHideClubDisclaimer(tabPosition : Int){
        when (tabPosition) {
            0 -> {
                tvClubDisclaimer.visibility = View.GONE
            }
            else -> {
                tvClubDisclaimer.visibility = View.VISIBLE
            }
        }
    }

    override fun onTeamClick(teamName: String?) {
        val dialogTitle = dialogSelectTeam.findViewById<TextView>(android.support.v7.appcompat.R.id.alertTitle)
        if (dialogTitle != null) when {
            dialogTitle.text == getString(R.string.default_team_name_a) -> updateTeamASelection(teamName)
            dialogTitle.text == getString(R.string.default_team_name_b) -> updateTeamBSelection(teamName)
            else -> {
                loadTeamFromFile(teamName)
            }
        }
        dialogSelectTeam.dismiss()
    }

    override fun onTeamLongClick(isSelected: Boolean){
        if(isSelected){
            ivDeleteFile.visibility = View.VISIBLE
        }
        else{
            ivDeleteFile.visibility = View.INVISIBLE
        }
    }

    private fun updateTeamASelection(teamName: String?){
        val team = mapOfTeams[teamName]
        if(team != null){
            ivTeamA.setImageResource(team.getCrest())
            tvTeamNameA.text = team.getName()
            pitchView.setJerseyBitmaps(team.getJerseyGoalkeeper(), team.getJerseyOutfield())
            pitchView.invalidate()
        }
        else{
            setDefaultTeamANameAndCrest()
        }
    }

    private fun updateTeamBSelection(teamName: String?){
        val team = mapOfTeams[teamName]
        if(team != null){
            ivTeamB.setImageResource(team.getCrest())
            tvTeamNameB.text = "vs." +team.getName()
        }
        else{
            setDefaultTeamBNameAndCrest()
        }
    }

    private fun setDefaultTeamANameAndCrest(){
        ivTeamA.setImageResource(R.drawable.crest_default)
        tvTeamNameA.setText(R.string.default_team_name_a)

        pitchView.setJerseyBitmaps(R.drawable.jersey_default, R.drawable.jersey_default)
        var number = 1
        for(item in pitchView.mapOfPlayers){
            item.value.setCustomName("")
            item.value.setNumber(number.toString())
            pitchView.setPlayerNumberAndNameRect(item.value)
            number++
        }

        pitchView.invalidate()
    }

    private fun setDefaultTeamBNameAndCrest(){
        ivTeamB.setImageResource(R.drawable.crest_default)
        tvTeamNameB.setText(R.string.default_team_name_b)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_item_reset -> {
                openResetDialog()
                true
            }
            R.id.menu_item_share -> {
                checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE)
                true
            }
            R.id.menu_item_save -> {
                checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE)
                true
            }
            R.id.menu_item_load -> {
                checkPermission(PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_LOAD)
                true
            }
            R.id.menu_item_about -> {
                openAboutDialog()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun openResetDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.reset_title)
        builder.setMessage(R.string.reset_message)
        builder.apply {
            setPositiveButton(R.string.ok) { _, _ ->
                initVars()
            }
            setNegativeButton(R.string.cancel) { _, _ ->
            }
        }
        builder.show()
    }

    private fun checkPermission(requestCode: Int){
        if (!isPermissionGranted()) {
            requestPermission(requestCode)
        } else {
            // Permission has already been granted
            when (requestCode) {
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE -> storeAndShareImage(getScreenImage())
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE -> openSaveDialog()
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_LOAD -> openLoadDialog()
            }
        }
    }

    private fun isPermissionGranted(): Boolean{
        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission(requestCode: Int){
        // Request the permission the 'write external storage' permission as this is required to store and share the screenshot
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // permission was granted, proceed to operation
            when (requestCode) {
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE -> storeAndShareImage(getScreenImage())
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE -> openSaveDialog()
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_LOAD -> openLoadDialog()
            }
        }
        else {
            // permission denied, don't proceed to store and share screenshot
            showToast(getString(R.string.permission_error_msg_external_storage))
        }

        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, proceed to store and then share the screenshot
                    storeAndShareImage(getScreenImage())
                } else {
                    // permission denied, don't proceed to store and share screenshot
                    showToast(getString(R.string.permission_error_msg_external_storage))
                }
                return
            }
        }
    }

    private fun getScreenImage(): Bitmap {
        val rootView = findViewById<ConstraintLayout>(R.id.cl_main) // we don't want the action bar in the screenshot so using the app's main layout
        rootView.isDrawingCacheEnabled = true
        val bitmap = rootView.drawingCache.copy(Bitmap.Config.RGB_565, false)
        rootView.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun storeAndShareImage(bitmap: Bitmap) {
        val imageDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
        imageDir.mkdirs()

        val dateAndTimeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFile = File(imageDir, dateAndTimeStamp + "_" +tvTeamNameA.text.toString() +".jpg")

        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.e("GREC", e.message, e)
        } catch (e: IOException) {
            Log.e("GREC", e.message, e)
        }

        shareImage(imageFile)
    }

    private fun shareImage(imagePath: File) {
        val uri = FileProvider.getUriForFile(this, "com.codepath.fileprovider", imagePath)

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        val shareBody = "#" + getString(R.string.app_name)
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, fileName)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)

        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)))
    }

    private fun deleteImages(){
        val imageDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
        if(imageDir.exists()){
            val files = imageDir.listFiles()
            for(file in files.iterator()){
                if(file.extension == "jpg"){
                    file.delete()
                }
            }
        }
    }

    private fun openSaveDialog(){
        val view = View.inflate(this, R.layout.dialog_save_team, null)
        val cbxOverwriteFile = view.findViewById<CheckBox>(R.id.cbx_overwrite_file)

        val builder : AlertDialog = AlertDialog.Builder(this)
        .setTitle(R.string.action_save)
        .setView(view)
        .setPositiveButton(R.string.ok, null)
        .setNegativeButton(R.string.cancel, null)
        .create()

        builder.setOnShowListener {
            val button = builder.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                var fileName = etMatchInfo.text.toString()
                fileName = fileName.replace("/", "")
                val errorMsg = isFileNameValid(fileName, cbxOverwriteFile.isChecked)
                if(errorMsg.isNotEmpty()){
                    etMatchInfo.requestFocus()
                    etMatchInfo.error = errorMsg
                }
                else{
                    writeTeamToFile(fileName)
                    showToast("Team saved as $fileName.csv")
                    builder.dismiss()
                }
            }
        }
        builder.show()

        rlMatchInfo = view.findViewById(R.id.rl_save_team)
        etMatchInfo = rlMatchInfo.findViewById(R.id.et_match_info_name_write)

        val defaultFileName = (tvTeamNameA.text.toString() + " " + tvTeamNameB.text.toString())
        etMatchInfo.hint = defaultFileName
        when {
            fileName?.isEmpty()!! -> etMatchInfo.setText(defaultFileName)
            else -> etMatchInfo.setText(fileName)
        }
    }

    private fun isFileNameValid(fileName: String, isAllowedToOverwriteFile: Boolean):String{
        if(fileName.isEmpty()){
            return getString(R.string.file_name_empty)
        }
        else if(isFileExisting(fileName)){
            if(!isAllowedToOverwriteFile){
                return getString(R.string.file_name_exists)
            }
        }
        return ""
    }

    private fun isFileExisting(fileName: String): Boolean{
        val csvDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
        val csvFile = File(csvDir, "$fileName.csv")

        return csvFile.exists()
    }

    private fun writeTeamToFile(fileName: String){
        this.fileName = fileName

        val csvDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
        csvDir.mkdirs()

        val csvFile = File(csvDir, "$fileName.csv")

        val fileWriter = FileWriter(csvFile, false)
        val bufferedWriter = BufferedWriter(fileWriter)

        bufferedWriter.write(tvTeamNameA.text.toString())
        bufferedWriter.write("\n")
        bufferedWriter.write(tvMatchInfo.text.toString())
        bufferedWriter.write("\n")
        bufferedWriter.write(tvTeamNameB.text.toString().substringAfter("vs. "))
        bufferedWriter.write("\n")
        for(item in pitchView.mapOfPlayers){
            bufferedWriter.write(item.value.getName())
            if(item != pitchView.mapOfPlayers.lastEntry()){
                bufferedWriter.write("\n")
            }
        }

        bufferedWriter.close()
        fileWriter.close()
    }

    private fun openLoadDialog(){
        val listOfTeamFilesToLoad = ArrayList<String>()

        val csvDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
        if(csvDir.exists()){
            val files = csvDir.listFiles()
            for(file in files.iterator()){
                if(file.extension == "csv"){
                    listOfTeamFilesToLoad.add(file.name)
                }
            }
        }

        if(listOfTeamFilesToLoad.isNotEmpty()){
            val view = View.inflate(this, R.layout.dialog_custom_title, null)

            val builder = AlertDialog.Builder(this, R.style.DialogWindowTitle_Holo)
            builder.setCustomTitle(view)
            builder.setTitle(R.string.action_load)

            val row = layoutInflater.inflate(R.layout.team_name_view, null)

            listOfTeamFilesToLoad.sort()
            viewManager = LinearLayoutManager(this)
            viewAdapter = CountyAdapter(listOfTeamFilesToLoad, true, this)

            rvTeams = row.findViewById<RecyclerView>(R.id.rv_team_names).apply {
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

            dialogSelectTeam = builder.create()
            dialogSelectTeam.show()

            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.85).toInt()

            dialogSelectTeam.window.setLayout(width, height)

            ivDeleteFile = view.findViewById(R.id.iv_delete_team)
            ivDeleteFile.setOnClickListener {
                val listOfFiles = viewAdapter.getSelectedItems()
                if(listOfFiles.isNotEmpty()){
                    val builder = AlertDialog.Builder(this)
                    var msgPre = listOfFiles.size.toString()
                    var msgPost = listOfFiles.size.toString()
                    msgPre += when {
                        listOfFiles.size == 1 -> " team?"
                        else -> " teams?"
                    }
                    msgPost += when {
                        listOfFiles.size == 1 -> " team has"
                        else -> " teams have"
                    }
                    builder.setTitle(getString(R.string.delete_title) + " " + msgPre)
                    builder.setMessage(R.string.delete_message)
                    builder.apply {
                        setPositiveButton(R.string.ok) { _, _ ->
                            val csvDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
                            if(csvDir.exists()) {
                                val files = csvDir.listFiles()
                                for (file in files.iterator()) {
                                    if(listOfFiles.contains(file.name)){
                                        file.delete()
                                        viewAdapter.removeItem(file.name)
                                    }
                                }
                                viewAdapter.notifyDataSetChanged()
                                if(viewAdapter.itemCount == 0){
                                    dialogSelectTeam.dismiss()
                                    showToast(getString(R.string.delete_all_success))
                                }
                                else{
                                    showToast("$msgPost been deleted!")
                                }
                            }
                        }
                        setNegativeButton(R.string.cancel) { _, _ ->
                        }
                    }
                    builder.show()
                }
                else{
                    showToast(getString(R.string.delete_not_available))
                }
            }
        }
        else{
            showToast(getString(R.string.load_not_available))
        }
    }

    private fun loadTeamFromFile(team: String?){
        val filePath = Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name) + "/" + team
        val bufferedReader = File(filePath).bufferedReader()
        try {
            val lineList = mutableListOf<String>()
            bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
            var lineNum = 0
            var playerIndex = 0
            lineList.forEach {
                when (lineNum) {
                    0 -> updateTeamASelection(it)
                    1 -> tvMatchInfo.text = it
                    2 -> updateTeamBSelection(it)
                    else -> {
                        val player = pitchView.mapOfPlayers[playerIndex]
                        player!!.setCustomName(it)
                        pitchView.setPlayerNumberAndNameRect(player)
                        playerIndex++
                    }
                }
                lineNum++
            }

            fileName = removeExtensionFromFileName(team)
            pitchView.invalidate()

        } catch (e : IOException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun removeExtensionFromFileName(team :String?) :String?{
        var fileNameWithoutExtension = team
        when {
            team != null -> when {
                team.indexOf(".") > 0 -> fileNameWithoutExtension = team.substring(0, team.lastIndexOf("."))
            }
        }
        return fileNameWithoutExtension
    }

    private fun openAboutDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.action_about) + " " + getString(R.string.app_name))
        builder.setMessage(BuildConfig.VERSION_NAME)
        builder.show()
    }

    private fun showToast(msg: String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
