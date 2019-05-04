package com.example.first15

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
import java.io.File
import java.io.FileOutputStream
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException


class MainActivity : OnTeamClickListener, AppCompatActivity(){

    companion object {
        const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }

    private lateinit var llMatchInfo: LinearLayout

    private lateinit var llTeamA: LinearLayout
    private lateinit var tvTeamA: TextView
    private lateinit var ivTeamA: ImageView

    private lateinit var rlCompetition: RelativeLayout
    private lateinit var tvCompetitionTitle: TextView
    private lateinit var tvCompetitionName: TextView

    private lateinit var rlCompetitionWrite: RelativeLayout
    private lateinit var etCompetitionName: EditText

    private lateinit var rlVenue: RelativeLayout
    private lateinit var tvVenueTitle: TextView
    private lateinit var tvVenueName: TextView

    private lateinit var rlVenueWrite: RelativeLayout
    private lateinit var etVenueName: EditText

    private lateinit var rlTime: RelativeLayout
    private lateinit var tvTimeTitle: TextView
    private lateinit var tvTimeName: TextView

    private lateinit var rlTimeWrite: RelativeLayout
    private lateinit var etTimeName: EditText

    private lateinit var llTeamB: LinearLayout
    private lateinit var tvTeamB: TextView
    private lateinit var ivTeamB: ImageView

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var rvTeams: RecyclerView

    private lateinit var dialogSelectTeam: AlertDialog

    private lateinit var mapOfTeamsClub: TreeMap<String, Team>
    private lateinit var mapOfTeamsCounty: TreeMap<String, Team>

    private lateinit var pitchView: PitchView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        loadVars()
        loadTeams()
        initVars()
    }

    private fun loadVars(){
        llMatchInfo = findViewById(R.id.ll_match_info_read)
        llMatchInfo.setOnClickListener {
            openMatchInfoDialog()
        }

        llTeamA = findViewById(R.id.ll_team_a)
        tvTeamA = llTeamA.findViewById(R.id.tv_team) as TextView
        ivTeamA = llTeamA.findViewById(R.id.iv_team) as ImageView
        ivTeamA.setOnClickListener{
            openTeamSelectionDialog(getString(R.string.default_team_name_a))
        }

        rlCompetition = findViewById(R.id.rl_competition_read)
        tvCompetitionTitle = rlCompetition.findViewById(R.id.tv_match_info_title_read) as TextView
        tvCompetitionName = rlCompetition.findViewById(R.id.tv_match_info_name_read) as TextView

        rlVenue = findViewById(R.id.rl_venue_write_read)
        tvVenueTitle = rlVenue.findViewById(R.id.tv_match_info_title_read) as TextView
        tvVenueName = rlVenue.findViewById(R.id.tv_match_info_name_read) as TextView

        rlTime = findViewById(R.id.rl_time_write_read)
        tvTimeTitle = rlTime.findViewById(R.id.tv_match_info_title_read) as TextView
        tvTimeName = rlTime.findViewById(R.id.tv_match_info_name_read) as TextView

        llTeamB = findViewById(R.id.ll_team_b)
        ivTeamB = llTeamB.findViewById(R.id.iv_team) as ImageView
        ivTeamB.setOnClickListener{
            openTeamSelectionDialog(getString(R.string.default_team_name_b))
        }
        tvTeamB = llTeamB.findViewById(R.id.tv_team) as TextView

        pitchView = findViewById(R.id.view_pitch)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loadTeams(){
        mapOfTeamsClub = TreeMap()
        mapOfTeamsClub[resources.getString(R.string.boyle)] = Team(resources.getString(R.string.boyle), getDrawable(R.drawable.crest_boyle), R.drawable.jersey_boyle_goalkeeper, R.drawable.jersey_boyle_outfield)
        mapOfTeamsClub[resources.getString(R.string.clan_na_gael)] = Team(resources.getString(R.string.clan_na_gael), getDrawable(R.drawable.crest_clan_na_gael_ros), R.drawable.jersey_clan_na_gael_ros_goalkeeper, R.drawable.jersey_clan_na_gael_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.elphin)] = Team(resources.getString(R.string.elphin), getDrawable(R.drawable.crest_elphin), R.drawable.jersey_elphin_goalkeeper, R.drawable.jersey_elphin_outfield)
        mapOfTeamsClub[resources.getString(R.string.fuerty)] = Team(resources.getString(R.string.fuerty), getDrawable(R.drawable.crest_fuerty), R.drawable.jersey_fuerty_goalkeeper, R.drawable.jersey_fuerty_outfield)
        mapOfTeamsClub[resources.getString(R.string.micheal_glaveys)] = Team(resources.getString(R.string.micheal_glaveys), getDrawable(R.drawable.crest_michael_glaveys), R.drawable.jersey_michael_glaveys_goalkeeper, R.drawable.jersey_michael_glaveys_outfield)
        mapOfTeamsClub[resources.getString(R.string.padraig_pearses)] = Team(resources.getString(R.string.padraig_pearses), getDrawable(R.drawable.crest_padraig_pearses_ros), R.drawable.jersey_padraig_pearses_ros_goalkeeper, R.drawable.jersey_padraig_pearses_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.roscommon_gaels)] = Team(resources.getString(R.string.roscommon_gaels), getDrawable(R.drawable.crest_roscommon_gaels), R.drawable.jersey_ros_gaels_goalkeeper, R.drawable.jersey_ros_gaels_outfield)
        mapOfTeamsClub[resources.getString(R.string.strokestown)] = Team(resources.getString(R.string.strokestown), getDrawable(R.drawable.crest_strokestown), R.drawable.jersey_strokestown_goalkeeper, R.drawable.jersey_strokestown_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_brigids)] = Team(resources.getString(R.string.st_brigids), getDrawable(R.drawable.crest_st_brigids_ros), R.drawable.jersey_st_brigids_ros_goalkeeper, R.drawable.jersey_st_brigids_ros_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_croans)] = Team(resources.getString(R.string.st_croans), getDrawable(R.drawable.crest_st_croans), R.drawable.jersey_st_croans_goalkeeper, R.drawable.jersey_st_croans_outfield)
        mapOfTeamsClub[resources.getString(R.string.st_faithleachs)] = Team(resources.getString(R.string.st_faithleachs), getDrawable(R.drawable.crest_st_faithleachs), R.drawable.jersey_st_faithleachs_goalkeeper, R.drawable.jersey_st_faithleachs_outfield)
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
        mapOfTeamsCounty[resources.getString(R.string.carlow)] = Team(resources.getString(R.string.clare), getDrawable(R.drawable.crest_carlow), R.drawable.jersey_carlow_goalkeeper, R.drawable.jersey_carlow_outfield)
        mapOfTeamsCounty[resources.getString(R.string.dublin)] = Team(resources.getString(R.string.dublin), getDrawable(R.drawable.crest_dublin), R.drawable.jersey_dublin_goalkeeper, R.drawable.jersey_dublin_outfield)
        mapOfTeamsCounty[resources.getString(R.string.kildare)] = Team(resources.getString(R.string.kildare), getDrawable(R.drawable.crest_kildare), R.drawable.jersey_kildare_goalkeeper, R.drawable.jersey_kildare_outfield)
        mapOfTeamsCounty[resources.getString(R.string.kilkenny)] = Team(resources.getString(R.string.kilkenny), getDrawable(R.drawable.crest_kilkenny), R.drawable.jersey_kilkenny_goalkeeper, R.drawable.jersey_kilkenny_outfield)
        mapOfTeamsCounty[resources.getString(R.string.laois)] = Team(resources.getString(R.string.laois), getDrawable(R.drawable.crest_laois), R.drawable.jersey_laois_goalkeeper, R.drawable.jersey_laois_outfield)
        mapOfTeamsCounty[resources.getString(R.string.longford)] = Team(resources.getString(R.string.longford), getDrawable(R.drawable.crest_longford), R.drawable.jersey_longford_goalkeeper, R.drawable.jersey_longford_outfield)
        mapOfTeamsCounty[resources.getString(R.string.louth)] = Team(resources.getString(R.string.louth), getDrawable(R.drawable.crest_louth), R.drawable.jersey_louth_goalkeeper, R.drawable.jersey_louth_outfield)
        mapOfTeamsCounty[resources.getString(R.string.meath)] = Team(resources.getString(R.string.meath), getDrawable(R.drawable.crest_meath), R.drawable.jersey_meath_goalkeeper, R.drawable.jersey_meath_outfield)
        mapOfTeamsCounty[resources.getString(R.string.offaly)] = Team(resources.getString(R.string.offaly), getDrawable(R.drawable.crest_offaly), R.drawable.jersey_offaly_goalkeeper, R.drawable.jersey_offaly_outfield)
        mapOfTeamsCounty[resources.getString(R.string.westmeath)] = Team(resources.getString(R.string.westmeath), getDrawable(R.drawable.crest_westmeath), R.drawable.jersey_westmeath_goalkeeper, R.drawable.jersey_westmeath_outfield)
        mapOfTeamsCounty[resources.getString(R.string.wexford)] = Team(resources.getString(R.string.waterford), getDrawable(R.drawable.crest_wexford), R.drawable.jersey_wexford_goalkeeper, R.drawable.jersey_wexford_outfield)
        mapOfTeamsCounty[resources.getString(R.string.wicklow)] = Team(resources.getString(R.string.wicklow), getDrawable(R.drawable.crest_wicklow), R.drawable.jersey_wicklow_goalkeeper, R.drawable.jersey_wicklow_outfield)
    }

    private fun initVars(){
        ivTeamA.setImageResource(R.drawable.add_team)
        tvTeamA.setText(R.string.default_team_name_a)

        tvCompetitionTitle.setText(R.string.default_match_info_competition_title)
        tvCompetitionName.setText(R.string.default_match_info_competition_name)

        tvVenueTitle.setText(R.string.default_match_info_venue_title)
        tvVenueName.setText(R.string.default_match_info_venue_name)

        tvTimeTitle.setText(R.string.default_match_info_time_title)
        tvTimeName.setText(R.string.default_match_info_time_name)

        ivTeamB.setImageResource(R.drawable.add_team)
        tvTeamB.setText(R.string.default_team_name_b)

        pitchView.setJerseyBitmaps(R.drawable.jersey_default, R.drawable.jersey_default)
        pitchView.invalidate()

        for(item in pitchView.mapOfPlayers){
            item.value.setCustomName("")
        }
    }

    private fun openMatchInfoDialog(){
        val view = View.inflate(this, R.layout.dialog_edit_match_info, null)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.set_match_details)
        builder.setView(view)
        builder.apply {
            setPositiveButton(R.string.ok) { _, _ ->
                if(!etCompetitionName.text.toString().isEmpty()){
                    tvCompetitionName.text = etCompetitionName.text.toString()
                }
                if(!etVenueName.text.toString().isEmpty()){
                    tvVenueName.text = etVenueName.text.toString()
                }
                if(!etTimeName.text.toString().isEmpty()){
                    tvTimeName.text = etTimeName.text.toString()
                }
            }
            setNegativeButton(R.string.cancel) { _, _ ->
                // User cancelled the dialog
            }
        }
        builder.show()

        rlCompetitionWrite = view.findViewById(R.id.rl_competition_write)
        tvCompetitionTitle = rlCompetitionWrite.findViewById(R.id.tv_match_info_title_write)
        tvCompetitionTitle.setText(R.string.default_match_info_competition_title)
        etCompetitionName = rlCompetitionWrite.findViewById(R.id.et_match_info_name_write)
        if(tvCompetitionName.text == getString(R.string.default_match_info_competition_name)) etCompetitionName.setHint(R.string.default_match_info_competition_name)
        else etCompetitionName.setText(tvCompetitionName.text.toString())

        rlVenueWrite = view.findViewById(R.id.rl_venue_write)
        tvVenueTitle = rlVenueWrite.findViewById(R.id.tv_match_info_title_write)
        tvVenueTitle.setText(R.string.default_match_info_venue_title)
        etVenueName = rlVenueWrite.findViewById(R.id.et_match_info_name_write)
        if(tvVenueName.text == getString(R.string.default_match_info_venue_name)) etVenueName.setHint(R.string.default_match_info_venue_name)
        else etVenueName.setText(tvVenueName.text.toString())

        rlTimeWrite = view.findViewById(R.id.rl_time_write)
        tvTimeTitle = rlTimeWrite.findViewById(R.id.tv_match_info_title_write)
        tvTimeTitle.setText(R.string.default_match_info_time_title)
        etTimeName = rlTimeWrite.findViewById(R.id.et_match_info_name_write)
        if(tvTimeName.text == getString(R.string.default_match_info_time_name)) etTimeName.setHint(R.string.default_match_info_time_name)
        else etTimeName.setText(tvTimeName.text.toString())
    }

    private fun openTeamSelectionDialog(title: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        val row = layoutInflater.inflate(R.layout.team_name_view, null)

        val list = resources.getStringArray(R.array.team_names_counties).toList()
        Collections.sort(list)

        viewManager = LinearLayoutManager(this)
        viewAdapter= MyAdapter(list, this)

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

        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.75).toInt()

        dialogSelectTeam.window.setLayout(width, height)
    }

    override fun onTeamClick(team: String?) {
        val dialogTitle = dialogSelectTeam.findViewById<TextView>(android.support.v7.appcompat.R.id.alertTitle)
        if (dialogTitle != null) {
            if(dialogTitle.text == getString(R.string.default_team_name_a)) {
                tvTeamA.text = team
                ivTeamA.setImageDrawable(mapOfTeamsCounty[team]!!.getCrest())
                pitchView.setJerseyBitmaps(mapOfTeamsCounty[team]!!.getJerseyGoalkeeper(), mapOfTeamsCounty[team]!!.getJerseyOutfield())
                pitchView.invalidate()
            }
            else{
                tvTeamB.text = team
                ivTeamB.setImageDrawable(mapOfTeamsCounty[team]!!.getCrest())
            }
        }
        dialogSelectTeam.dismiss()
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
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.reset_title)
                builder.setMessage(R.string.reset_message)
                builder.apply {
                    setPositiveButton(R.string.ok) { _, _ ->
                        initVars()
                    }
                    setNegativeButton(R.string.cancel) { _, _ ->
                        // User cancelled the dialog
                    }
                }
                builder.show()

                true
            }
            R.id.menu_item_share -> {
                checkPermissions()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Request the permission the 'write external storage' permission as this is required to store and share the screenshot
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        } else {
            // Permission has already been granted
            storeAndShareImage(getScreenShot())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, proceed to store and then share the screenshot
                    storeAndShareImage(getScreenShot())
                } else {
                    // permission denied, don't proceed to store and share screenshot
                    Toast.makeText(this, R.string.share_permission_error_msg, Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun getScreenShot(): Bitmap {
        val rootView = findViewById<ConstraintLayout>(R.id.cl_main) // we don't want the action bar in the screenshot so using the app's main layout
        rootView.isDrawingCacheEnabled = true
        val bitmap = rootView.drawingCache.copy(Bitmap.Config.RGB_565, false)
        rootView.isDrawingCacheEnabled = false
        return bitmap
    }

    private fun storeAndShareImage(bitmap: Bitmap) {
        val imagePath = File(Environment.getExternalStorageDirectory().toString() + "/screenshot.png")
        try {
            val fos = FileOutputStream(imagePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.e("GREC", e.message, e)
        } catch (e: IOException) {
            Log.e("GREC", e.message, e)
        }

        shareImage(imagePath)
    }

    private fun shareImage(imagePath: File) {
        val uri = FileProvider.getUriForFile(this, "com.codepath.fileprovider", imagePath)

        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        val shareBody = "#" + getString(R.string.app_name)
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)

        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)))
    }
}
