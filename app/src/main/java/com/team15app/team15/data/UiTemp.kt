package com.team15app.team15.data

data class UiTemp(
    var teamNameA: String = "",
    var teamNameB: String = "",
    var matchInfo: String = "",
    var filePath: String = "",
    var fileName: String = "",
    var jerseyGoalkeeper: String? = null,
    var jerseyOutfield: String? = null,
    var listOfPlayers: MutableList<String> = mutableListOf()
)
