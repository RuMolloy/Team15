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

    private lateinit var mapOfTeams: TreeMap<String, Team>

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
        mapOfTeams = TreeMap()
        mapOfTeams[resources.getString(R.string.boyle)] = Team(resources.getString(R.string.boyle), getDrawable(R.drawable.crest_boyle), R.drawable.jersey_boyle_goalkeeper, R.drawable.jersey_boyle_outfield)
        mapOfTeams[resources.getString(R.string.clan_na_gael)] = Team(resources.getString(R.string.clan_na_gael), getDrawable(R.drawable.crest_clan_na_gael_ros), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.elphin)] = Team(resources.getString(R.string.elphin), getDrawable(R.drawable.crest_elphin), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.fuerty)] = Team(resources.getString(R.string.fuerty), getDrawable(R.drawable.crest_fuerty), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.micheal_glaveys)] = Team(resources.getString(R.string.micheal_glaveys), getDrawable(R.drawable.crest_michael_glaveys), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.padraig_pearses)] = Team(resources.getString(R.string.padraig_pearses), getDrawable(R.drawable.crest_padraig_pearses_ros), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.roscommon_gaels)] = Team(resources.getString(R.string.roscommon_gaels), getDrawable(R.drawable.crest_roscommon_gaels), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.strokestown)] = Team(resources.getString(R.string.strokestown), getDrawable(R.drawable.crest_strokestown), R.drawable.jersey_strokestown_goalkeeper, R.drawable.jersey_strokestown_outfield)
        mapOfTeams[resources.getString(R.string.st_brigids)] = Team(resources.getString(R.string.st_brigids), getDrawable(R.drawable.crest_st_brigids_ros), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.st_croans)] = Team(resources.getString(R.string.st_croans), getDrawable(R.drawable.crest_st_croans), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.st_faithleachs)] = Team(resources.getString(R.string.st_faithleachs), getDrawable(R.drawable.crest_st_faithleachs), R.drawable.jersey_default, R.drawable.jersey_default)
        mapOfTeams[resources.getString(R.string.western_gaels)] = Team(resources.getString(R.string.western_gaels), getDrawable(R.drawable.crest_western_gaels), R.drawable.jersey_default, R.drawable.jersey_default)
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

        viewManager = LinearLayoutManager(this)
        viewAdapter= MyAdapter(resources.getStringArray(R.array.team_names), this)

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
                ivTeamA.setImageDrawable(mapOfTeams[team]!!.getCrest())
                pitchView.setJerseyBitmaps(mapOfTeams[team]!!.getJerseyGoalkeeper(), mapOfTeams[team]!!.getJerseyOutfield())
                pitchView.invalidate()
            }
            else{
                tvTeamB.text = team
                ivTeamB.setImageDrawable(mapOfTeams[team]!!.getCrest())
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
