package com.team15app.team15

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team15app.team15.data.Player
import com.team15app.team15.data.LoadTeamUI
import com.team15app.team15.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _loadTeamUI: MutableSharedFlow<Result<LoadTeamUI>> = MutableSharedFlow()
    val loadTeamUI: SharedFlow<Result<LoadTeamUI>> = _loadTeamUI

    private val _teamImageFileWritten: MutableSharedFlow<Result<File>> = MutableSharedFlow()
    val teamImageFileWritten: SharedFlow<Result<File>> = _teamImageFileWritten

    private val _teamTextFileWritten: MutableSharedFlow<Result<File>> = MutableSharedFlow()
    val teamTextFileWritten: SharedFlow<Result<File>> = _teamTextFileWritten

    private val _teamTextFilesLoaded: MutableSharedFlow<ArrayList<String>> = MutableSharedFlow()
    val teamTextFilesLoaded: SharedFlow<ArrayList<String>> = _teamTextFilesLoaded

    var isBackPressed = MutableStateFlow(false)

    fun getListOfSavedTeams(
        filePath: String,
        version: String
    ) {
        viewModelScope.launch {
            _teamTextFilesLoaded.emit(repository.getListOfSavedTeams(filePath, version))
        }
    }

    fun loadTeamFromFile(
        filePath: String,
        teamNameA: String,
        version: String
    ) {
        viewModelScope.launch {
            when(val result = repository.loadTeamFromFile(filePath, teamNameA, version)) {
                is Result.Error -> _loadTeamUI.emit(Result.Error(message = result.message))
                is Result.Success -> _loadTeamUI.emit(Result.Success(data = result.data))
            }
        }
    }

    fun writeTeamToFile(csvFile: File,
        filePath: String,
        teamNameA: String,
        teamNameB: String,
        matchInfo: String,
        jerseyGoalkeeper: String?,
        jerseyOutfield: String?,
        mapOfPlayers: TreeMap<Int, Player>
    ) {
        //overwrite existing one
        deleteTextFile(csvFile)

        var fileName = "$teamNameA $teamNameB"
        fileName = fileName.replace("/", "")
        fileName = appendNumericToFileNameIfExists(fileName, filePath)

        viewModelScope.launch {
            val result = repository.writeTeamToFile(
                fileName,
                filePath,
                teamNameA,
                teamNameB,
                matchInfo,
                jerseyGoalkeeper,
                jerseyOutfield,
                mapOfPlayers
            )
            when(result) {
                is Result.Error -> _teamTextFileWritten.emit(Result.Error(result.message))
                is Result.Success -> _teamTextFileWritten.emit(Result.Success(result.data))
            }
        }
    }

    fun writeImageToFile(
        filePath: String,
        teamNameA: String,
        bitmap: Bitmap
    ) {
        viewModelScope.launch {
            when(val result = repository.writeImageToFile(filePath, teamNameA, bitmap)) {
                is Result.Error -> _teamImageFileWritten.emit(Result.Error(message = result.message))
                is Result.Success -> _teamImageFileWritten.emit(Result.Success(data = result.data))
            }
        }
    }

    private fun appendNumericToFileNameIfExists(
        fileName: String,
        filePath: String
    ): String {
        var fName = fileName

        var newFileName = fName
        var counter = 1
        while (isFileExisting(newFileName, filePath)) {
            fName = "$fName($counter)"
            newFileName = fName
            counter++
        }
        return newFileName
    }

    private fun isFileExisting(
        fileName: String,
        filePath: String
    ): Boolean {
        val csvDir = File(filePath)
        val csvFile = File(csvDir, "$fileName.csv")

        return csvFile.exists()
    }

    fun isLineupChanged(
        teamNameACustom: String,
        teamNameBCustom: String,
        matchInfoCustom: String,
        jerseyGoalkeeper: String,
        jerseyOutfield: String,
        mapOfPlayers: MutableCollection<Player>,
        teamNameADefault: String,
        teamNameBDefault: String,
        matchInfoDefault: String,
        jerseyGoalkeeperDefault: String,
        jerseyOutfieldDefault: String
    ): Boolean {
        when {
            (teamNameACustom != teamNameADefault ||
                    jerseyGoalkeeper != jerseyGoalkeeperDefault ||
                    jerseyOutfield != jerseyOutfieldDefault) -> return true
            teamNameBCustom != ("vs. $teamNameBDefault") -> return true
            matchInfoCustom != matchInfoDefault -> return true
            else -> {
                for (player in mapOfPlayers) {
                    if (player.isDefaultName() && player.isDefaultNumber()) continue
                    return true
                }
                return false
            }
        }
    }

    fun isDuplicate(dialogTitleCustom: String, dialogTitleDefault: String): Boolean {
        return dialogTitleCustom == dialogTitleDefault
    }

    fun deleteTextFile(csvFile: File) {
        if(csvFile.exists()){
            csvFile.delete()
        }
    }

    fun deleteImageFiles(filePath: String) {
        val imageDir = File(filePath)
        if(imageDir.exists()){
            val files = imageDir.listFiles()
            for(file in files.iterator()){
                if(file.extension == "jpg"){
                    file.delete()
                }
            }
        }
    }
}