package com.team15app.team15

enum class TeamEnum(val tabName: Int, val tabLayout: Int){
    COUNTY(R.string.tab_county, R.layout.team_name_view),
    CLUB(R.string.tab_club, R.layout.team_name_view);
}