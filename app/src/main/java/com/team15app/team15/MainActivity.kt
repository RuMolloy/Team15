package com.team15app.team15

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.team15app.team15.adapters.TeamNameAdapter
import com.team15app.team15.dialogs.MatchInfoDialogFragment
import com.team15app.team15.listeners.OnTeamClickListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : OnTeamClickListener, AppCompatActivity(), MatchInfoDialogFragment.OnFinishEditDialog {
    companion object {
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE = 1
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE = 2
        const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_LOAD = 3
    }

    private lateinit var tvTeamNameA: TextView
    private lateinit var tvTeamNameB: TextView
    private lateinit var tvMatchInfo: TextView

    private lateinit var viewAdapter: TeamNameAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var rvTeams: RecyclerView
    private lateinit var tvDialogTitle: TextView
    private lateinit var ivDialogBack: ImageView
    private lateinit var ivDeleteFile: ImageView

    private lateinit var dialogSelectTeam: AlertDialog

    private lateinit var pitchView: PitchView
    private lateinit var team: Team

    private var fileName: String = ""
    private var csvFile = File(fileName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true); // show back button on action bar
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //UI only designed for portrait, so disable landscape

        loadVars()
        initVars()
        requestPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        deleteImagesIfHasPermission()
        openLoadDialog()
    }

    private fun loadVars(){
        val rlMatchInfo = findViewById<RelativeLayout>(R.id.rl_match_info)
        rlMatchInfo.setOnClickListener{
            pitchView.resetSelectedPlayers()
            pitchView.invalidate()
            openMatchInfoDialog()
        }
        tvTeamNameA = rlMatchInfo.findViewById(R.id.tv_match_info_team_a)
        tvTeamNameB = rlMatchInfo.findViewById(R.id.tv_match_info_team_b)
        tvMatchInfo = rlMatchInfo.findViewById(R.id.tv_match_info_name_read)

        pitchView = findViewById(R.id.view_pitch)
        pitchView.initJerseyListener(this)
    }

    private fun initVars(){
        team = Team(getString(R.string.default_team_name_a),
            R.drawable.crest_default,
            resources.getResourceEntryName(R.drawable.jersey_default),
            resources.getResourceEntryName(R.drawable.jersey_default))
        setDefaultTeamAName()
        setDefaultTeamALineup()
        setDefaultTeamBName()
        updateMatchInfo(getString(R.string.default_match_info))
        fileName = ""
        csvFile = File(fileName)
    }

    private fun deleteImagesIfHasPermission(){
        // delete previously shared images to stop image build up in the user's team15 directory
        when {isPermissionGranted() -> deleteImages()}
    }

    private fun openMatchInfoDialog(){
        val args = Bundle()
        args.putString(resources.getString(R.string.default_team_name_a), tvTeamNameA.text.toString())
        args.putString(resources.getString(R.string.default_team_name_b), tvTeamNameB.text.toString())
        args.putString(resources.getString(R.string.default_match_info), tvMatchInfo.text.toString())
        args.putString(resources.getString(R.string.goalkeeper), team.getJerseyGoalkeeper())
        args.putString(resources.getString(R.string.outfielder), team.getJerseyOutfield())

        val dialogFragment = MatchInfoDialogFragment()
        dialogFragment.arguments = args
        dialogFragment.show(this.supportFragmentManager, "")
        dialogFragment.setOnFinishEditDialogListener(this)
        supportFragmentManager.executePendingTransactions();
        dialogFragment.dialog.window.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )
        dialogFragment.dialog.setCanceledOnTouchOutside(false);
    }

    override fun onFinishEditDialog(bundle: Bundle) {
        val tvTeamNameA = bundle!!.getString(getString(R.string.default_team_name_a))
        val tvTeamNameB = bundle.getString(getString(R.string.default_team_name_b))
        val tvMatchInfo = bundle.getString(getString(R.string.default_match_info))
        val dGoalKeeper = bundle.getString(getString(R.string.goalkeeper))
        val dOutfielder = bundle.getString(getString(R.string.outfielder))

        when {
            !tvTeamNameA.isNullOrEmpty() -> updateTeamASelection(tvTeamNameA)
            else -> updateTeamASelection(getString(R.string.default_team_name_a))
        }
        when {
            !tvTeamNameB.isNullOrEmpty() -> updateTeamBSelection(tvTeamNameB)
            else -> updateTeamBSelection(getString(R.string.default_team_name_b))
        }
        when {
            !tvMatchInfo.isNullOrEmpty() -> updateMatchInfo(tvMatchInfo)
            else -> updateMatchInfo(getString(R.string.default_match_info))
        }

        team.setName(tvTeamNameA)
        updateJersey(true, dGoalKeeper)
        updateJersey(false, dOutfielder)

        if (isLineupChanged()) {
            checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE)
        }
    }

    override fun onTeamClick(teamName: String) {
        when (teamName) {
            getString(R.string.create_new_team) -> {
                initVars()
                dialogSelectTeam.dismiss()
            }
            getString(R.string.duplicate_a_team) -> {
                fileName = ""
                csvFile = File(fileName)
                addDuplicate()
            }
            else -> {
                val filePath = loadTeamFromFile(teamName)
                dialogSelectTeam.dismiss()
                if(!isDuplicate()){
                    csvFile = File(filePath) //only store file path if lineup not to be duplicated
                }
            }
        }
    }

    override fun onTeamLongClick(isSelected: Boolean){
        if(isSelected){
            ivDeleteFile.visibility = View.VISIBLE
        }
        else{
            ivDeleteFile.visibility = View.INVISIBLE
        }
    }

    private fun updateJersey(isGoalkeeper: Boolean, resourceName: String){
        when {
            isGoalkeeper -> {
                team.setJerseyGoalkeeper(resourceName)
                pitchView.setJerseyGoalkeeperBitmap(resourceName)
            }
            else -> {
                team.setJerseyOutfield(resourceName)
                pitchView.setJerseyOutfieldBitmap(resourceName)
            }
        }
        pitchView.invalidate()
    }

    private fun updateTeamASelection(teamName: String?){
        tvTeamNameA.text = teamName
    }

    private fun updateTeamBSelection(teamName: String?){
        tvTeamNameB.text = "vs. $teamName"
    }

    private fun updateMatchInfo(matchInfo: String?){
        tvMatchInfo.text = matchInfo
    }

    private fun setDefaultTeamAName(){
        updateTeamASelection(getString(R.string.default_team_name_a))
    }

    private fun setDefaultTeamBName(){
        updateTeamBSelection(getString(R.string.default_team_name_b))
    }

    private fun setDefaultTeamALineup(){
        pitchView.setJerseyBitmaps(resources.getResourceEntryName(R.drawable.jersey_default),
            resources.getResourceEntryName(R.drawable.jersey_default))
        var number = 1
        for(item in pitchView.mapOfPlayers){
            item.value.setCustomNumber(number.toString())
            item.value.setDefaultNumber(number.toString())
            item.value.setCustomName(item.value.getDefaultName())
            item.value.setNamePointCustom(item.value.getNamePointDefault())

            pitchView.setPlayerNumberAndNameRect(item.value)
            number++
        }

        pitchView.invalidate()
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
        pitchView.resetSelectedPlayers()
        pitchView.invalidate()
        return when (item.itemId) {
            R.id.menu_item_delete -> {
                openResetDialog()
                true
            }
            R.id.menu_item_share -> {
                checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE)
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
        builder.setTitle(R.string.delete_title)
        builder.setMessage(R.string.reset_message)
        builder.apply {
            setPositiveButton(R.string.ok) { _, _ ->
                if(csvFile.exists()){
                    csvFile.delete() //overwrite previous file
                }
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
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE -> {
                    writeTeamToFile()
                    storeAndShareImage(getScreenImage())
                }
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE -> writeTeamToFile()
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
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE -> writeTeamToFile()
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

    private fun writeTeamToFile(){
        var fileName = tvTeamNameA.text.toString() + " " + tvTeamNameB.text.toString()
        fileName = fileName.replace("/", "")
        this.fileName = fileName

        val csvDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
        csvDir.mkdirs()

        if(csvFile.exists()){
            csvFile.delete() //overwrite previous file
        }
        fileName = appendNumericToFileNameIfExists(fileName)
        csvFile = File(csvDir, "$fileName.csv")

        val fileWriter = FileWriter(csvFile, false)
        val bufferedWriter = BufferedWriter(fileWriter)
        val matchInfo = tvMatchInfo.text.toString().replace("\n", "")

        bufferedWriter.write(BuildConfig.VERSION_NAME)
        bufferedWriter.write("\n")
        bufferedWriter.write(tvTeamNameA.text.toString())
        bufferedWriter.write("\n")
        bufferedWriter.write(matchInfo)
        bufferedWriter.write("\n")
        bufferedWriter.write(tvTeamNameB.text.toString().substringAfter("vs. "))
        bufferedWriter.write("\n")
        for(item in pitchView.mapOfPlayers){
            bufferedWriter.write(item.value.getNumber() + "\t" + item.value.getName())
            if(item != pitchView.mapOfPlayers.lastEntry()){
                bufferedWriter.write("\n")
            }
        }
        bufferedWriter.write("\n")
        bufferedWriter.write(team.getJerseyGoalkeeper().toString())
        bufferedWriter.write("\n")
        bufferedWriter.write(team.getJerseyOutfield().toString())

        bufferedWriter.close()
        fileWriter.close()
        //showToast("Team saved as $fileName.csv")
    }

    private fun appendNumericToFileNameIfExists(fileName: String): String{
        var fileName = fileName

        var newFileName = fileName
        var counter = 1
        while (isFileExisting(newFileName)) {
            fileName = "$fileName($counter)"
            newFileName = fileName
            counter++
        }
        return newFileName
    }

    private fun isFileExisting(fileName: String): Boolean{
        val csvDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
        val csvFile = File(csvDir, "$fileName.csv")

        return csvFile.exists()
    }

    private fun openLoadDialog(){
        val listOfTeamFilesToLoad = ArrayList<String>()

        val csvDir = File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name))
        if(csvDir.exists()){
            val files = csvDir.listFiles()
            if(files != null){
                for(file in files.iterator()){
                    if(isValidFile(file)){
                        listOfTeamFilesToLoad.add(file.nameWithoutExtension)
                    }
                }
            }
        }

        if(listOfTeamFilesToLoad.isNotEmpty()){
            listOfTeamFilesToLoad.sort()
            listOfTeamFilesToLoad.add(0, getString(R.string.create_new_team))
            listOfTeamFilesToLoad.add(1, getString(R.string.duplicate_a_team))
            val view = View.inflate(this, R.layout.dialog_custom_title, null)

            val builder = AlertDialog.Builder(this)
            builder.setCustomTitle(view)
            builder.setTitle(R.string.swap_name)
            builder.setOnCancelListener { // if from activity
                finish()
            }

            val row = layoutInflater.inflate(R.layout.team_name_view, null)

            viewManager = LinearLayoutManager(this)
            viewAdapter = TeamNameAdapter(listOfTeamFilesToLoad, true, this)

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
            dialogSelectTeam.setCanceledOnTouchOutside(false);
            dialogSelectTeam.show()

            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.75).toInt()

            dialogSelectTeam.window.setLayout(width, height)

            tvDialogTitle = view.findViewById(R.id.tv_dialog_title)
            ivDialogBack = view.findViewById(R.id.iv_dialog_back)
            ivDialogBack.setOnClickListener {
                removeDuplicate()
            }

            ivDeleteFile = view.findViewById(R.id.iv_delete_team)
            ivDeleteFile.setOnClickListener {
                val listOfFiles = viewAdapter.getSelectedItems()
                if(listOfFiles.isNotEmpty()){
                    val builder = AlertDialog.Builder(this)
                    var msgPre = listOfFiles.size.toString()
                    var msgPost = listOfFiles.size.toString()
                    msgPre += when (listOfFiles.size) {
                        1 -> " team?"
                        else -> " teams?"
                    }
                    msgPost += when (listOfFiles.size) {
                        1 -> " team has"
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
                                    if(listOfFiles.contains(file.nameWithoutExtension)){
                                        file.delete()
                                        viewAdapter.removeItem(file.nameWithoutExtension)
                                    }
                                }
                                viewAdapter.notifyDataSetChanged()
                                if(viewAdapter.itemCount == 2){ //size of two means only 'create new' and 'duplicate' options available
                                    dialogSelectTeam.dismiss()
                                    showToast(getString(R.string.delete_all_success))
                                }
                                else{
                                    viewAdapter.resetCheckbox()
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
    }

    private fun isValidFile(file: File): Boolean{
        if(file.extension != "csv"){
            return false
        }
        val version = getString(R.string.version)
        val bufferedReader = file.bufferedReader()
        val firstLine = bufferedReader.readLine()

        return firstLine.contains(version)
    }

    private fun addDuplicate(){
        viewAdapter.removeItem(getString(R.string.create_new_team))
        viewAdapter.removeItem(getString(R.string.duplicate_a_team))
        viewAdapter.notifyDataSetChanged()

        ivDialogBack.visibility = View.VISIBLE
        tvDialogTitle.text = getString(R.string.select_team_to_duplicate)
    }

    private fun removeDuplicate(){
        viewAdapter.addItem(getString(R.string.duplicate_a_team))
        viewAdapter.addItem(getString(R.string.create_new_team))
        viewAdapter.notifyDataSetChanged()

        ivDialogBack.visibility = View.GONE
        tvDialogTitle.text = getString(R.string.action_load)
    }

    private fun isDuplicate(): Boolean{
        return tvDialogTitle.text == getString(R.string.select_team_to_duplicate)
    }

    private fun loadTeamFromFile(team: String): String{
        val filePath = Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name) + "/" + team + ".csv"
        val bufferedReader = File(filePath).bufferedReader()
        try {
            val lineList = mutableListOf<String>()
            bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
            var lineNum = 0
            var playerIndex = 0
            var offset = 0
            var version = getString(R.string.version)
            lineList.forEach {
                when (lineNum) {
                    0 -> {
                        if(!it.contains(version)){
                            updateTeamASelection(it)
                            updateJersey(true, resources.getResourceEntryName(R.drawable.jersey_default))
                            updateJersey(false, resources.getResourceEntryName(R.drawable.jersey_default))
                            offset = 1
                        }
                    }
                    1-offset -> updateTeamASelection(it)
                    2-offset -> tvMatchInfo.text = it
                    3-offset -> updateTeamBSelection(it)
                    in 4-offset..18-offset -> {
                        val player = pitchView.mapOfPlayers[playerIndex]
                        val numberAndName = it
                        val number = it.substringBefore("\t")
                        val name = it.substringAfter("\t")
                        if(number != numberAndName){
                            player!!.setDefaultNumber(number)
                            player!!.setCustomNumber(number)
                        }
                        player!!.setCustomName(name)
                        pitchView.setPlayerNumberAndNameRect(player)
                        playerIndex++
                    }
                    19 -> updateJersey(true, it.toString())
                    20 -> updateJersey(false, it.toString())
                }
                lineNum++
            }

            fileName = team
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
        return filePath
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

    override fun onPause() {
        pitchView.closeEditPlayerDialogIfOpen()
        super.onPause()
    }

    override fun onUserLeaveHint() {
        pitchView.closeEditPlayerDialogIfOpen()
        super.onUserLeaveHint()
    }

    override fun onBackPressed() {
        if (isLineupChanged()) {
            checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE)
        }
        openLoadDialog()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (isLineupChanged()) {
            checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE)
        }
        openLoadDialog()
        return true
    }

    private fun isLineupChanged(): Boolean{
        when {
            (tvTeamNameA.text.toString() != getString(R.string.default_team_name_a) ||
                team.getJerseyGoalkeeper() != resources.getResourceEntryName(R.drawable.jersey_default) ||
                team.getJerseyOutfield() != resources.getResourceEntryName(R.drawable.jersey_default)) -> return true
            tvTeamNameB.text.toString() != ("vs. "+getString(R.string.default_team_name_b)) -> return true
            tvMatchInfo.text.toString() != getString(R.string.default_match_info) -> return true
            else -> {
                for (player in pitchView.mapOfPlayers.values) {
                    if (player.isDefaultName() && player.isDefaultNumber()) continue
                    return true
                }
                return false
            }
        }
    }
}
