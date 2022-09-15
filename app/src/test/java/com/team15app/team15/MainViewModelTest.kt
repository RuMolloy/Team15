package com.team15app.team15

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        val repo = MainRepository()
        viewModel = MainViewModel(repo)
    }

    @Test
    fun `Lineup has changed true`() {
        val isLineupChanged = viewModel.isLineupChanged(teamNameACustom = "TeamA",
            teamNameBCustom = "TeamB",
            matchInfoCustom = "Match Info",
            jerseyGoalkeeper = "",
            jerseyOutfield = "",
            mapOfPlayers = mutableListOf(),
            teamNameADefault = "Mayo",
            teamNameBDefault = "Dublin",
            matchInfoDefault = "Match Info",
            jerseyGoalkeeperDefault = "",
            jerseyOutfieldDefault = "")

        assertThat(isLineupChanged).isTrue()
    }

    @Test
    fun `Lineup has changed false`() {
        val isLineupChanged = viewModel.isLineupChanged(
            teamNameACustom = "TeamA",
            teamNameBCustom = "vs. TeamB",
            matchInfoCustom = "Match Info",
            jerseyGoalkeeper = "",
            jerseyOutfield = "",
            mapOfPlayers = mutableListOf(),
            teamNameADefault = "TeamA",
            teamNameBDefault = "TeamB",
            matchInfoDefault = "Match Info",
            jerseyGoalkeeperDefault = "",
            jerseyOutfieldDefault = "")

        assertThat(isLineupChanged).isFalse()
    }

    @Test
    fun `Is duplicate option selected`() {
        assertThat(viewModel.isDuplicate("Duplicate", "Duplicate")).isTrue()
    }

}