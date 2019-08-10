package com.team15app.team15

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
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

    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var rvTeams: RecyclerView
    private lateinit var ivDeleteFile: ImageView

    private lateinit var dialogSelectTeam: AlertDialog

    private var tabPosition = 0
    private lateinit var tvClubDisclaimer: TextView

    private lateinit var mapOfTeamsClub: TreeMap<String, Team>
    private lateinit var mapOfTeamsCounty: TreeMap<String, Team>
    private lateinit var mapOfTeams: TreeMap<String, Team>

    private lateinit var pitchView: PitchView

    private var fileName: String? = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loadTeams(){
        mapOfTeamsClub = TreeMap()

        //Roscommon clubs
        mapOfTeamsClub[resources.getString(R.string.ballinameen)] = Team(resources.getString(R.string.ballinameen), getDrawable(R.drawable.crest_ballinameen), R.drawable.jersey_ballinameen_goalkeeper, R.drawable.jersey_ballinameen_outfield)
        mapOfTeamsClub[resources.getString(R.string.boyle)] = Team(resources.getString(R.string.boyle), getDrawable(R.drawable.crest_boyle), R.drawable.jersey_boyle_goalkeeper, R.drawable.jersey_boyle_outfield)
        mapOfTeamsClub[resources.getString(R.string.castlerea)] = Team(resources.getString(R.string.castlerea), getDrawable(R.drawable.crest_castlerea), R.drawable.jersey_castlerea_goalkeeper, R.drawable.jersey_castlerea_outfield)
        mapOfTeamsClub[resources.getString(R.string.clann_na_ngael)] = Team(resources.getString(R.string.clann_na_ngael), getDrawable(R.drawable.crest_clan_na_gael_ros), R.drawable.jersey_clan_na_gael_ros_goalkeeper, R.drawable.jersey_clan_na_gael_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.creggs)] = Team(resources.getString(R.string.creggs), getDrawable(R.drawable.crest_creggs), R.drawable.jersey_creggs_goalkeeper, R.drawable.jersey_creggs_outfield)
        mapOfTeamsClub[resources.getString(R.string.eire_og)] = Team(resources.getString(R.string.eire_og), getDrawable(R.drawable.crest_eire_og_ros), R.drawable.jersey_eire_og_ros_goalkeeper, R.drawable.jersey_eire_og_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.elphin)] = Team(resources.getString(R.string.elphin), getDrawable(R.drawable.crest_elphin), R.drawable.jersey_elphin_goalkeeper, R.drawable.jersey_elphin_outfield)
        mapOfTeamsClub[resources.getString(R.string.fuerty)] = Team(resources.getString(R.string.fuerty), getDrawable(R.drawable.crest_fuerty), R.drawable.jersey_fuerty_goalkeeper, R.drawable.jersey_fuerty_outfield)
        mapOfTeamsClub[resources.getString(R.string.kilbride)] = Team(resources.getString(R.string.kilbride), getDrawable(R.drawable.crest_kilbride_ros), R.drawable.jersey_kilbride_ros_goalkeeper, R.drawable.jersey_kilbride_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.kilglass_gaels)] = Team(resources.getString(R.string.kilglass_gaels), getDrawable(R.drawable.crest_kilglass_gaels), R.drawable.jersey_kilglass_gaels_goalkeeper, R.drawable.jersey_kilglass_gaels_outfield)
        mapOfTeamsClub[resources.getString(R.string.kilmore)] = Team(resources.getString(R.string.kilmore), getDrawable(R.drawable.crest_kilmore_ros), R.drawable.jersey_kilmore_ros_goalkeeper, R.drawable.jersey_kilmore_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.micheal_glaveys)] = Team(resources.getString(R.string.micheal_glaveys), getDrawable(R.drawable.crest_michael_glaveys), R.drawable.jersey_michael_glaveys_goalkeeper, R.drawable.jersey_michael_glaveys_outfield)
        mapOfTeamsClub[resources.getString(R.string.oran)] = Team(resources.getString(R.string.oran), getDrawable(R.drawable.crest_oran), R.drawable.jersey_oran_goalkeeper, R.drawable.jersey_oran_outfield)
        mapOfTeamsClub[resources.getString(R.string.padraig_pearses)] = Team(resources.getString(R.string.padraig_pearses), getDrawable(R.drawable.crest_padraig_pearses_ros), R.drawable.jersey_padraig_pearses_ros_goalkeeper, R.drawable.jersey_padraig_pearses_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.roscommon_gaels)] = Team(resources.getString(R.string.roscommon_gaels), getDrawable(R.drawable.crest_roscommon_gaels), R.drawable.jersey_ros_gaels_goalkeeper, R.drawable.jersey_ros_gaels_outfield)
        mapOfTeamsClub[resources.getString(R.string.shannon_gaels)] = Team(resources.getString(R.string.shannon_gaels), getDrawable(R.drawable.crest_shannon_gaels), R.drawable.jersey_shannon_gaels_goalkeeper, R.drawable.jersey_shannon_gaels_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_aidans)] = Team(resources.getString(R.string.st_aidans), getDrawable(R.drawable.crest_st_aidans_ros), R.drawable.jersey_st_aidans_ros_goalkeeper, R.drawable.jersey_st_aidans_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_barrys)] = Team(resources.getString(R.string.st_barrys), getDrawable(R.drawable.crest_st_barrys), R.drawable.jersey_st_barrys_goalkeeper, R.drawable.jersey_st_barrys_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_brigids)] = Team(resources.getString(R.string.st_brigids), getDrawable(R.drawable.crest_st_brigids_ros), R.drawable.jersey_st_brigids_ros_goalkeeper, R.drawable.jersey_st_brigids_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_croans)] = Team(resources.getString(R.string.st_croans), getDrawable(R.drawable.crest_st_croans), R.drawable.jersey_st_croans_goalkeeper, R.drawable.jersey_st_croans_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_dominics)] = Team(resources.getString(R.string.st_dominics), getDrawable(R.drawable.crest_st_dominics), R.drawable.jersey_st_dominics_goalkeeper, R.drawable.jersey_st_dominics_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_faithleachs)] = Team(resources.getString(R.string.st_faithleachs), getDrawable(R.drawable.crest_st_faithleachs), R.drawable.jersey_st_faithleachs_goalkeeper, R.drawable.jersey_st_faithleachs_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_josephs)] = Team(resources.getString(R.string.st_josephs), getDrawable(R.drawable.crest_st_josephs_ros), R.drawable.jersey_st_josephs_ros_goalkeeper, R.drawable.jersey_st_josephs_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_michaels)] = Team(resources.getString(R.string.st_michaels), getDrawable(R.drawable.crest_st_michaels), R.drawable.jersey_st_michaels_goalkeeper, R.drawable.jersey_st_michaels_outfield)
        mapOfTeamsClub[resources.getString(R.string.strokestown)] = Team(resources.getString(R.string.strokestown), getDrawable(R.drawable.crest_strokestown), R.drawable.jersey_strokestown_goalkeeper, R.drawable.jersey_strokestown_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_ronans)] = Team(resources.getString(R.string.st_ronans), getDrawable(R.drawable.crest_st_ronans), R.drawable.jersey_st_ronans_goalkeeper, R.drawable.jersey_st_ronans_outfield)
        mapOfTeamsClub[resources.getString(R.string.tulsk)] = Team(resources.getString(R.string.tulsk), getDrawable(R.drawable.crest_tulsk), R.drawable.jersey_tulsk_goalkeeper, R.drawable.jersey_tulsk_outfield)
        mapOfTeamsClub[resources.getString(R.string.western_gaels)] = Team(resources.getString(R.string.western_gaels), getDrawable(R.drawable.crest_western_gaels), R.drawable.jersey_western_gaels_goalkeeper, R.drawable.jersey_western_gaels_outfield)

        mapOfTeamsCounty = TreeMap()

        // connaught counties
        mapOfTeamsCounty[resources.getString(R.string.galway)] = Team(resources.getString(R.string.galway), getDrawable(R.drawable.crest_galway), R.drawable.jersey_galway_goalkeeper, R.drawable.jersey_galway_outfield)
        mapOfTeamsCounty[resources.getString(R.string.leitrim)] = Team(resources.getString(R.string.leitrim), getDrawable(R.drawable.crest_leitrim), R.drawable.jersey_leitrim_goalkeeper, R.drawable.jersey_leitrim_outfield)
        mapOfTeamsCounty[resources.getString(R.string.london)] = Team(resources.getString(R.string.london), getDrawable(R.drawable.crest_london), R.drawable.jersey_london_goalkeeper, R.drawable.jersey_london_outfield)
        mapOfTeamsCounty[resources.getString(R.string.mayo)] = Team(resources.getString(R.string.mayo), getDrawable(R.drawable.crest_mayo), R.drawable.jersey_mayo_goalkeeper, R.drawable.jersey_mayo_outfield)
        mapOfTeamsCounty[resources.getString(R.string.new_york)] = Team(resources.getString(R.string.new_york), getDrawable(R.drawable.crest_new_york), R.drawable.jersey_new_york_goalkeeper, R.drawable.jersey_new_york_outfield)
        mapOfTeamsCounty[resources.getString(R.string.roscommon)] = Team(resources.getString(R.string.roscommon), getDrawable(R.drawable.crest_roscommon), R.drawable.jersey_roscommon_goalkeeper, R.drawable.jersey_roscommon_outfield)
        mapOfTeamsCounty[resources.getString(R.string.sligo)] = Team(resources.getString(R.string.sligo), getDrawable(R.drawable.crest_sligo), R.drawable.jersey_sligo_goalkeeper, R.drawable.jersey_sligo_outfield)

        // ulster counties
        mapOfTeamsCounty[resources.getString(R.string.antrim)] = Team(resources.getString(R.string.antrim), getDrawable(R.drawable.crest_antrim), R.drawable.jersey_antrim_goalkeeper, R.drawable.jersey_antrim_outfield)
        mapOfTeamsCounty[resources.getString(R.string.armagh)] = Team(resources.getString(R.string.armagh), getDrawable(R.drawable.crest_armagh), R.drawable.jersey_armagh_goalkeeper, R.drawable.jersey_armagh_outfield)
        mapOfTeamsCounty[resources.getString(R.string.cavan)] = Team(resources.getString(R.string.cavan), getDrawable(R.drawable.crest_cavan), R.drawable.jersey_cavan_goalkeeper, R.drawable.jersey_cavan_outfield)
        mapOfTeamsCounty[resources.getString(R.string.derry)] = Team(resources.getString(R.string.derry), getDrawable(R.drawable.crest_derry), R.drawable.jersey_derry_goalkeeper, R.drawable.jersey_derry_outfield)
        mapOfTeamsCounty[resources.getString(R.string.donegal)] = Team(resources.getString(R.string.donegal), getDrawable(R.drawable.crest_donegal), R.drawable.jersey_donegal_goalkeeper, R.drawable.jersey_donegal_outfield)
        mapOfTeamsCounty[resources.getString(R.string.down)] = Team(resources.getString(R.string.down), getDrawable(R.drawable.crest_down), R.drawable.jersey_down_goalkeeper, R.drawable.jersey_down_outfield)
        mapOfTeamsCounty[resources.getString(R.string.fermanagh)] = Team(resources.getString(R.string.fermanagh), getDrawable(R.drawable.crest_fermanagh), R.drawable.jersey_fermanagh_goalkeeper, R.drawable.jersey_fermanagh_outfield)
        mapOfTeamsCounty[resources.getString(R.string.monaghan)] = Team(resources.getString(R.string.monaghan), getDrawable(R.drawable.crest_monaghan), R.drawable.jersey_monaghan_goalkeeper, R.drawable.jersey_monaghan_outfield)
        mapOfTeamsCounty[resources.getString(R.string.tyrone)] = Team(resources.getString(R.string.tyrone), getDrawable(R.drawable.crest_tyrone), R.drawable.jersey_tyrone_goalkeeper, R.drawable.jersey_tyrone_outfield)

        // munster counties
        mapOfTeamsCounty[resources.getString(R.string.clare)] = Team(resources.getString(R.string.clare), getDrawable(R.drawable.crest_clare), R.drawable.jersey_clare_goalkeeper, R.drawable.jersey_clare_outfield)
        mapOfTeamsCounty[resources.getString(R.string.cork)] = Team(resources.getString(R.string.cork), getDrawable(R.drawable.crest_cork), R.drawable.jersey_cork_goalkeeper, R.drawable.jersey_cork_outfield)
        mapOfTeamsCounty[resources.getString(R.string.kerry)] = Team(resources.getString(R.string.kerry), getDrawable(R.drawable.crest_kerry), R.drawable.jersey_kerry_goalkeeper, R.drawable.jersey_kerry_outfield)
        mapOfTeamsCounty[resources.getString(R.string.limerick)] = Team(resources.getString(R.string.limerick), getDrawable(R.drawable.crest_limerick), R.drawable.jersey_limerick_goalkeeper, R.drawable.jersey_limerick_outfield)
        mapOfTeamsCounty[resources.getString(R.string.tipperary)] = Team(resources.getString(R.string.tipperary), getDrawable(R.drawable.crest_tipperary), R.drawable.jersey_tipperary_goalkeeper, R.drawable.jersey_tipperary_outfield)
        mapOfTeamsCounty[resources.getString(R.string.waterford)] = Team(resources.getString(R.string.waterford), getDrawable(R.drawable.crest_waterford), R.drawable.jersey_waterford_goalkeeper, R.drawable.jersey_waterford_outfield)

        // leinster counties
        mapOfTeamsCounty[resources.getString(R.string.carlow)] = Team(resources.getString(R.string.carlow), getDrawable(R.drawable.crest_carlow), R.drawable.jersey_carlow_goalkeeper, R.drawable.jersey_carlow_outfield)
        mapOfTeamsCounty[resources.getString(R.string.dublin)] = Team(resources.getString(R.string.dublin), getDrawable(R.drawable.crest_dublin), R.drawable.jersey_dublin_goalkeeper, R.drawable.jersey_dublin_outfield)
        mapOfTeamsCounty[resources.getString(R.string.kildare)] = Team(resources.getString(R.string.kildare), getDrawable(R.drawable.crest_kildare), R.drawable.jersey_kildare_goalkeeper, R.drawable.jersey_kildare_outfield)
        mapOfTeamsCounty[resources.getString(R.string.kilkenny)] = Team(resources.getString(R.string.kilkenny), getDrawable(R.drawable.crest_kilkenny), R.drawable.jersey_kilkenny_goalkeeper, R.drawable.jersey_kilkenny_outfield)
        mapOfTeamsCounty[resources.getString(R.string.laois)] = Team(resources.getString(R.string.laois), getDrawable(R.drawable.crest_laois), R.drawable.jersey_laois_goalkeeper, R.drawable.jersey_laois_outfield)
        mapOfTeamsCounty[resources.getString(R.string.longford)] = Team(resources.getString(R.string.longford), getDrawable(R.drawable.crest_longford), R.drawable.jersey_longford_goalkeeper, R.drawable.jersey_longford_outfield)
        mapOfTeamsCounty[resources.getString(R.string.louth)] = Team(resources.getString(R.string.louth), getDrawable(R.drawable.crest_louth), R.drawable.jersey_louth_goalkeeper, R.drawable.jersey_louth_outfield)
        mapOfTeamsCounty[resources.getString(R.string.meath)] = Team(resources.getString(R.string.meath), getDrawable(R.drawable.crest_meath), R.drawable.jersey_meath_goalkeeper, R.drawable.jersey_meath_outfield)
        mapOfTeamsCounty[resources.getString(R.string.offaly)] = Team(resources.getString(R.string.offaly), getDrawable(R.drawable.crest_offaly), R.drawable.jersey_offaly_goalkeeper, R.drawable.jersey_offaly_outfield)
        mapOfTeamsCounty[resources.getString(R.string.westmeath)] = Team(resources.getString(R.string.westmeath), getDrawable(R.drawable.crest_westmeath), R.drawable.jersey_westmeath_goalkeeper, R.drawable.jersey_westmeath_outfield)
        mapOfTeamsCounty[resources.getString(R.string.wexford)] = Team(resources.getString(R.string.wexford), getDrawable(R.drawable.crest_wexford), R.drawable.jersey_wexford_goalkeeper, R.drawable.jersey_wexford_outfield)
        mapOfTeamsCounty[resources.getString(R.string.wicklow)] = Team(resources.getString(R.string.wicklow), getDrawable(R.drawable.crest_wicklow), R.drawable.jersey_wicklow_goalkeeper, R.drawable.jersey_wicklow_outfield)

        mapOfTeams = TreeMap()
        mapOfTeams.putAll(mapOfTeamsCounty)
        mapOfTeams.putAll(mapOfTeamsClub)
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

        val row = layoutInflater.inflate(R.layout.dialog_tabs, null)
        val tab = row.findViewById<TabLayout>(R.id.tab)
        val pageAdapter = CustomPagerAdapter(applicationContext, this)
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

    override fun onTeamLongClick(isOn: Boolean){
        if(isOn){
            ivDeleteFile.visibility = View.VISIBLE
        }
        else{
            ivDeleteFile.visibility = View.INVISIBLE
        }
    }

    private fun updateTeamASelection(teamName: String?){
        val team = mapOfTeams[teamName]
        if(team != null){
            ivTeamA.setImageDrawable(team.getCrest())
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
            ivTeamB.setImageDrawable(team.getCrest())
            tvTeamNameB.text = "vs. $teamName"
        }
        else{
            setDefaultTeamBNameAndCrest()
        }
    }

    private fun setDefaultTeamANameAndCrest(){
        ivTeamA.setImageResource(R.drawable.crest_default)
        tvTeamNameA.setText(R.string.default_team_name_a)

        pitchView.setJerseyBitmaps(R.drawable.jersey_default, R.drawable.jersey_default)
        for(item in pitchView.mapOfPlayers){
            item.value.setCustomName("")
            pitchView.setPlayerNumberAndNameRect(item.value)
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

        val dateAndTimeStamp = SimpleDateFormat("dd-MMM-yyyy").format(Date())
        var defaultFileName = (tvTeamNameA.text.toString() + " " + tvTeamNameB.text.toString())
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
            viewAdapter = MyAdapter(listOfTeamFilesToLoad, true, this)

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