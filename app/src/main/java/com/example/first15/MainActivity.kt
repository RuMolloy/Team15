package com.example.first15

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.TextView
import java.util.*

class MainActivity : OnTeamClickListener, AppCompatActivity(){

    private lateinit var llTeamA: LinearLayout
    private lateinit var tvTeamA: TextView
    private lateinit var ivTeamA: ImageView

    private lateinit var rlCompetition: RelativeLayout
    private lateinit var tvCompetitionTitle: TextView
    private lateinit var tvCompetitionName: TextView

    private lateinit var rlVenue: RelativeLayout
    private lateinit var tvVenueTitle: TextView
    private lateinit var tvVenueName: TextView

    private lateinit var rlTime: RelativeLayout
    private lateinit var tvTimeTitle: TextView
    private lateinit var tvTimeName: TextView

    private lateinit var llTeamB: LinearLayout
    private lateinit var tvTeamB: TextView
    private lateinit var ivTeamB: ImageView

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var rvTeams: RecyclerView

    private lateinit var dialogSelectTeam: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        loadVars()
        initVars()
    }

    private fun loadVars(){
        llTeamA = findViewById(R.id.ll_team_a)
        tvTeamA = llTeamA.findViewById(R.id.tv_team) as TextView
        ivTeamA = llTeamA.findViewById(R.id.iv_team) as ImageView
        ivTeamA.setOnClickListener{
            openTeamSelectionDialog(getString(R.string.default_team_name_a))
        }

        rlCompetition = findViewById(R.id.rl_competition)
        rlCompetition.setOnClickListener {

        }
        tvCompetitionTitle = rlCompetition.findViewById(R.id.tv_match_info_title) as TextView
        tvCompetitionName = rlCompetition.findViewById(R.id.tv_match_info_name) as TextView

        rlVenue = findViewById(R.id.rl_venue)
        rlVenue.setOnClickListener {

        }
        tvVenueTitle = rlVenue.findViewById(R.id.tv_match_info_title) as TextView
        tvVenueName = rlVenue.findViewById(R.id.tv_match_info_name) as TextView

        rlTime = findViewById(R.id.rl_time)
        rlTime.setOnClickListener {

        }
        tvTimeTitle = rlTime.findViewById(R.id.tv_match_info_title) as TextView
        tvTimeName = rlTime.findViewById(R.id.tv_match_info_name) as TextView


        llTeamB = findViewById(R.id.ll_team_b)
        ivTeamB = llTeamB.findViewById(R.id.iv_team) as ImageView
        ivTeamB.setOnClickListener{
            openTeamSelectionDialog(getString(R.string.default_team_name_b))
        }
        tvTeamB = llTeamB.findViewById(R.id.tv_team) as TextView
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
            if(dialogTitle.text == getString(R.string.default_team_name_a)) tvTeamA.text = team
            else  tvTeamB.text = team
        }
        dialogSelectTeam.dismiss()
        Toast.makeText(this, team, Toast.LENGTH_LONG).show()

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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initVars(){
        ivTeamA.setImageResource(R.mipmap.ic_launcher_round)
        ivTeamA.tag = R.string.default_team_name_a
        tvTeamA.setText(R.string.default_team_name_a)

        tvCompetitionTitle.setText(R.string.default_match_info_competition_title)
        tvCompetitionName.setText(R.string.default_match_info_competition_name)

        tvVenueTitle.setText(R.string.default_match_info_venue_title)
        tvVenueName.setText(R.string.default_match_info_venue_name)

        tvTimeTitle.setText(R.string.default_match_info_time_title)
        tvTimeName.setText(R.string.default_match_info_time_name)

        ivTeamB.setImageResource(R.mipmap.ic_launcher_round)
        ivTeamB.tag = R.string.default_team_name_b
        tvTeamB.setText(R.string.default_team_name_b)
    }
}
