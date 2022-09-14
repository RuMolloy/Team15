package com.team15app.team15

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.team15app.team15.adapters.TeamNameAdapter
import com.team15app.team15.data.LoadTeamUI
import com.team15app.team15.data.Team
import com.team15app.team15.databinding.ActivityMainBinding
import com.team15app.team15.databinding.DialogCustomTitleBinding
import com.team15app.team15.dialogs.MatchInfoDialogFragment
import com.team15app.team15.listeners.OnTeamClickListener
import com.team15app.team15.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity:
    OnTeamClickListener,
    AppCompatActivity(),
    MatchInfoDialogFragment.OnFinishEditDialog
{
    companion object {
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE = 1
        const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE = 2
        const val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_LOAD = 3
    }

    private lateinit var viewAdapter: TeamNameAdapter

    private lateinit var tvDialogTitle: TextView
    private lateinit var ivDialogBack: ImageView
    private lateinit var ivDeleteFile: ImageView

    private lateinit var dialogSelectTeam: AlertDialog

    private lateinit var team: Team

    private var csvFile = File("")

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingDialog: DialogCustomTitleBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var rootPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // show back button on action bar

        rootPath = getExternalFilesDir(null).toString() + "/" + getString(R.string.app_name)
        setupListeners()
        initUI()
        requestPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        deleteImagesIfHasPermission()
        getListOfSaveTeams()

        lifecycleScope.launch {
            viewModel.loadTeamUI.collectLatest {
                when (it) {
                    is Result.Success -> updateUI(it.data)
                    is Result.Error -> showSnackbar(it.message)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.teamImageFileWritten.collectLatest {
                when(it) {
                    is Result.Success -> shareImage(it.data)
                    is Result.Error -> showSnackbar(it.message)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.teamTextFileWritten.collectLatest {
                when(it) {
                    is Result.Success -> {
                        csvFile = it.data
                        if(viewModel.isBackPressed.value) { getListOfSaveTeams() }
                    }
                    is Result.Error -> showSnackbar(it.message)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.teamTextFilesLoaded.collectLatest {
                openLoadTeamsDialog(it)
            }
        }
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    private fun setupListeners(){
        binding.contentMain.rlMatchInfo.rlMatchInfoRead.setOnClickListener {
            binding.contentMain.viewPitch.resetSelectedPlayers()
            binding.contentMain.viewPitch.invalidate()
            openMatchInfoDialog()
        }
        binding.contentMain.viewPitch.initJerseyListener(this)
    }

    private fun initUI(){
        team = Team(getString(R.string.default_team_name_a),
            R.drawable.crest_default,
            resources.getResourceEntryName(R.drawable.jersey_default),
            resources.getResourceEntryName(R.drawable.jersey_default))
        csvFile = File("")
        setDefaultTeamAName()
        setDefaultTeamALineup()
        setDefaultTeamBName()
        updateMatchInfo(getString(R.string.default_match_info))
    }

    private fun updateUI(loadTeamUI: LoadTeamUI) {
        updateTeamASelection(loadTeamUI.teamNameA)
        updateTeamBSelection(loadTeamUI.teamNameB)
        binding.contentMain.rlMatchInfo.tvMatchInfoNameRead.text = loadTeamUI.matchInfo
        loadTeamUI.listOfPlayers.forEachIndexed { index, element ->
            val player = binding.contentMain.viewPitch.mapOfPlayers[index]
            val number = element.substringBefore("\t")
            val name = element.substringAfter("\t")
            if (number != element) {
                player!!.setDefaultNumber(number)
                player.setCustomNumber(number)
            }
            player!!.setCustomName(name)
            binding.contentMain.viewPitch.setPlayerNumberAndNameRect(player)
        }
        updateJersey(true, loadTeamUI.jerseyGoalkeeper)
        updateJersey(false, loadTeamUI.jerseyOutfield)
        if(!isDuplicateTeamOptionSelected()){
            //only store file path if lineup not to be duplicated
            csvFile = File(loadTeamUI.filePath)
        }
    }

    private fun deleteImagesIfHasPermission(){
        // delete previously shared images to stop image build up in the user's team15 directory
        when {isPermissionGranted() -> deleteImageFiles()}
    }

    private fun openMatchInfoDialog(){
        val args = Bundle().apply {
            putString(resources.getString(R.string.default_team_name_a),
                binding.contentMain.rlMatchInfo.tvMatchInfoTeamA.text.toString())
            putString(resources.getString(R.string.default_team_name_b),
                binding.contentMain.rlMatchInfo.tvMatchInfoTeamB.text.toString())
            putString(resources.getString(R.string.default_match_info),
                binding.contentMain.rlMatchInfo.tvMatchInfoNameRead.text.toString())
            putString(resources.getString(R.string.goalkeeper),
                team.jerseyGoalkeeper)
            putString(resources.getString(R.string.outfielder),
                team.jerseyOutfield)
        }

        val dialogFragment = MatchInfoDialogFragment()
        dialogFragment.arguments = args
        dialogFragment.show(this.supportFragmentManager, "")
        dialogFragment.setOnFinishEditDialogListener(this)
        supportFragmentManager.executePendingTransactions();
        dialogFragment.dialog?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )
        dialogFragment.dialog?.setCanceledOnTouchOutside(false);
    }

    override fun onFinishEditMatchInfoDialog(bundle: Bundle) {
        val tvTeamNameA = bundle.getString(getString(R.string.default_team_name_a))
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

        team.name = tvTeamNameA
        updateJersey(true, dGoalKeeper)
        updateJersey(false, dOutfielder)

        if (isLineupChanged()) {
            checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE, false)
        }
    }

    private fun openLoadTeamsDialog(listOfTeamFilesToLoad: ArrayList<String>) {
        if(listOfTeamFilesToLoad.isNotEmpty()){
            listOfTeamFilesToLoad.sort()
            listOfTeamFilesToLoad.add(0, getString(R.string.create_new_team))
            listOfTeamFilesToLoad.add(1, getString(R.string.duplicate_a_team))

            bindingDialog = DialogCustomTitleBinding.inflate(layoutInflater)

            val builder = AlertDialog.Builder(this)
            builder.setCustomTitle(bindingDialog.root)
            builder.setTitle(R.string.swap_name)
            builder.setOnCancelListener { // if from activity
                finish()
            }

            val row = layoutInflater.inflate(R.layout.team_name_view, null)

            val viewManager = LinearLayoutManager(this)
            viewAdapter = TeamNameAdapter(listOfTeamFilesToLoad,
                true,
                this)

            row.findViewById<RecyclerView>(R.id.rv_team_names).apply {
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                setHasFixedSize(true)

                // use a linear layout manager
                layoutManager = viewManager

                // specify an viewAdapter (see also next example)
                adapter = viewAdapter

                // adds a line between each recyclerview item
                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        LinearLayoutManager.VERTICAL
                    )
                )
            }
            builder.setView(row)

            dialogSelectTeam = builder.create()
            dialogSelectTeam.setCanceledOnTouchOutside(false)
            dialogSelectTeam.show()

            val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.75).toInt()

            dialogSelectTeam.window?.setLayout(width, height)

            tvDialogTitle = bindingDialog.tvDialogTitle
            ivDialogBack = bindingDialog.ivDialogBack
            ivDialogBack.setOnClickListener {
                removeDuplicateTeamOption()
            }

            ivDeleteFile = bindingDialog.ivDeleteTeam
            ivDeleteFile.setOnClickListener {
                val listOfFiles = viewAdapter.getSelectedItems()
                if(listOfFiles.isNotEmpty()){
                    openDeleteDialog(listOfFiles)
                }
                else{
                    showToast(getString(R.string.delete_not_available))
                }
            }
        }
    }

    override fun onLoadTeamClick(teamName: String) {
        when (teamName) {
            getString(R.string.create_new_team) -> {
                initUI()
                dialogSelectTeam.dismiss()
            }
            getString(R.string.duplicate_a_team) -> {
                csvFile = File("")
                addDuplicateTeamOption()
            }
            else -> {
                loadTeamFromFile(teamName)
                dialogSelectTeam.dismiss()
            }
        }
    }

    override fun onLoadTeamLongClick(isSelected: Boolean){
        if(isSelected){
            ivDeleteFile.visibility = View.VISIBLE
        }
        else{
            ivDeleteFile.visibility = View.INVISIBLE
        }
    }

    private fun updateJersey(isGoalkeeper: Boolean, resourceName: String?){
        when {
            isGoalkeeper -> {
                team.jerseyGoalkeeper = resourceName
                binding.contentMain.viewPitch.setJerseyGoalkeeperBitmap(resourceName)
            }
            else -> {
                team.jerseyOutfield = resourceName
                binding.contentMain.viewPitch.setJerseyOutfieldBitmap(resourceName)
            }
        }
        binding.contentMain.viewPitch.invalidate()
    }

    private fun updateTeamASelection(teamName: String?){
        binding.contentMain.rlMatchInfo.tvMatchInfoTeamA.text = teamName
    }

    private fun updateTeamBSelection(teamName: String?){
        binding.contentMain.rlMatchInfo.tvMatchInfoTeamB.text = "vs. $teamName"
    }

    private fun updateMatchInfo(matchInfo: String?){
        binding.contentMain.rlMatchInfo.tvMatchInfoNameRead.text = matchInfo
    }

    private fun setDefaultTeamAName(){
        updateTeamASelection(getString(R.string.default_team_name_a))
    }

    private fun setDefaultTeamBName(){
        updateTeamBSelection(getString(R.string.default_team_name_b))
    }

    private fun setDefaultTeamALineup(){
        binding.contentMain.viewPitch.setJerseyBitmaps(
            resources.getResourceEntryName(R.drawable.jersey_default),
            resources.getResourceEntryName(R.drawable.jersey_default)
        )
        var number = 1
        for(item in binding.contentMain.viewPitch.mapOfPlayers){
            item.value.setCustomNumber(number.toString())
            item.value.setDefaultNumber(number.toString())
            item.value.setCustomName(item.value.getDefaultName())
            item.value.setNamePointCustom(item.value.getNamePointDefault())

            binding.contentMain.viewPitch.setPlayerNumberAndNameRect(item.value)
            number++
        }

        binding.contentMain.viewPitch.invalidate()
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
        binding.contentMain.viewPitch.resetSelectedPlayers()
        binding.contentMain.viewPitch.invalidate()
        return when (item.itemId) {
            R.id.menu_item_delete -> {
                openResetDialog()
                true
            }
            R.id.menu_item_share -> {
                checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE, false)
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

    private fun checkPermission(requestCode: Int, isBackPressed: Boolean){
        if (!isPermissionGranted()) {
            requestPermission(requestCode)
        } else {
            // Permission has already been granted
            when (requestCode) {
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE -> {
                    writeTeamToFile(isBackPressed)
                    storeAndShareImage(getScreenImage())
                }
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE -> writeTeamToFile(isBackPressed)
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_LOAD -> getListOfSaveTeams()
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // permission was granted, proceed to operation
            when (requestCode) {
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SHARE -> storeAndShareImage(getScreenImage())
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE -> writeTeamToFile(false)
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_LOAD -> getListOfSaveTeams()
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
        // we don't want the action bar in the screenshot so using the app's main layout
        val bitmap = Bitmap.createBitmap(
            binding.contentMain.clMain.width,
            binding.contentMain.clMain.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        binding.contentMain.clMain.draw(canvas)
        return bitmap
    }

    private fun storeAndShareImage(bitmap: Bitmap) {
        val teamNameA = binding.contentMain.rlMatchInfo.tvMatchInfoTeamA.text.toString()
        viewModel.writeImageToFile(rootPath, teamNameA, bitmap)
    }

    private fun shareImage(imagePath: File) {
        val provider = getFileProviderAuthorities(this)
        val uri = FileProvider.getUriForFile(
            this,
            provider,
            imagePath
        )

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        val shareBody = "#" + getString(R.string.app_name)
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, csvFile.name)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)

        // Added as we have a file provider for the debug build flavor too
        val intentChooser = Intent.createChooser(sharingIntent, getString(R.string.share))
        val resInfoList = this.packageManager.queryIntentActivities(
            intentChooser,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        startActivity(intentChooser)
    }

    private fun getFileProviderAuthorities(context: Context): String {
        val component = ComponentName(context, FileProvider::class.java)
        val info = context.packageManager.getProviderInfo(component, PackageManager.GET_META_DATA)
        return info.authority
    }

    private fun deleteImageFiles(){
        viewModel.deleteImageFiles(rootPath)
    }

    private fun writeTeamToFile(isBackPressed: Boolean){
        viewModel.isBackPressed.value = isBackPressed
        viewModel.writeTeamToFile(
            csvFile = csvFile,
            filePath = rootPath,
            teamNameA = binding.contentMain.rlMatchInfo.tvMatchInfoTeamA.text.toString(),
            teamNameB = binding.contentMain.rlMatchInfo.tvMatchInfoTeamB.text.toString(),
            matchInfo = binding.contentMain.rlMatchInfo.tvMatchInfoNameRead.text.toString()
                .replace("\n", ""),
            jerseyGoalkeeper = team.jerseyGoalkeeper,
            jerseyOutfield = team.jerseyOutfield,
            binding.contentMain.viewPitch.mapOfPlayers
        )
    }

    private fun getListOfSaveTeams(){
        viewModel.getListOfSavedTeams(
            rootPath,
            getString(R.string.version))
    }

    private fun addDuplicateTeamOption(){
        viewAdapter.removeItem(getString(R.string.create_new_team))
        viewAdapter.removeItem(getString(R.string.duplicate_a_team))
        viewAdapter.notifyDataSetChanged()

        ivDialogBack.visibility = View.VISIBLE
        tvDialogTitle.text = getString(R.string.select_team_to_duplicate)
    }

    private fun removeDuplicateTeamOption(){
        viewAdapter.addItem(getString(R.string.duplicate_a_team))
        viewAdapter.addItem(getString(R.string.create_new_team))
        viewAdapter.notifyDataSetChanged()

        ivDialogBack.visibility = View.GONE
        tvDialogTitle.text = getString(R.string.action_load)
    }

    private fun isDuplicateTeamOptionSelected(): Boolean {
        return viewModel.isDuplicate(
            tvDialogTitle.text.toString(),
            getString(R.string.select_team_to_duplicate)
        )
    }

    private fun loadTeamFromFile(team: String) {
        viewModel.loadTeamFromFile(
            rootPath,
            team,
            getString(R.string.version))
    }

    private fun openResetDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.delete_title)
        builder.setMessage(R.string.reset_message)
        builder.apply {
            setPositiveButton(R.string.ok) { _, _ ->
                //overwrite previous file
                viewModel.deleteTextFile(csvFile)
                initUI()
            }
            setNegativeButton(R.string.cancel) { _, _ ->
            }
        }
        builder.show()
    }

    private fun openDeleteDialog(listOfFiles: ArrayList<String>) {
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
                val csvDir = File(rootPath)
                if(csvDir.exists()) {
                    for (file in csvDir.listFiles()) {
                        if(listOfFiles.contains(file.nameWithoutExtension)){
                            viewModel.deleteTextFile(file)
                            viewAdapter.removeItem(file.nameWithoutExtension)
                        }
                    }
                    viewAdapter.notifyDataSetChanged()
                    //size of two means only 'create new' and 'duplicate' options available
                    if(viewAdapter.itemCount == 2){
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
        binding.contentMain.viewPitch.closeEditPlayerDialogIfOpen()
        super.onPause()
    }

    override fun onUserLeaveHint() {
        binding.contentMain.viewPitch.closeEditPlayerDialogIfOpen()
        super.onUserLeaveHint()
    }

    override fun onBackPressed() {
        handleOnBack()
    }

    override fun onSupportNavigateUp(): Boolean {
        handleOnBack()
        return true
    }

    private fun handleOnBack() {
        if(isLineupChanged()) {
            checkPermission(PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_SAVE, true)
        }
        else{
            getListOfSaveTeams()
        }
    }

    private fun isLineupChanged(): Boolean {
        return viewModel.isLineupChanged(
            teamNameACustom = binding.contentMain.rlMatchInfo.tvMatchInfoTeamA.text.toString(),
            teamNameBCustom = binding.contentMain.rlMatchInfo.tvMatchInfoTeamB.text.toString(),
            matchInfoCustom = binding.contentMain.rlMatchInfo.tvMatchInfoNameRead.text.toString(),
            jerseyGoalkeeper = team.jerseyGoalkeeper.toString(),
            jerseyOutfield = team.jerseyOutfield.toString(),
            mapOfPlayers = binding.contentMain.viewPitch.mapOfPlayers.values,
            teamNameADefault = getString(R.string.default_team_name_a),
            teamNameBDefault = getString(R.string.default_team_name_b),
            matchInfoDefault = getString(R.string.default_match_info),
            jerseyGoalkeeperDefault = resources.getResourceEntryName(R.drawable.jersey_default),
            jerseyOutfieldDefault = resources.getResourceEntryName(R.drawable.jersey_default)
        )
    }
}
