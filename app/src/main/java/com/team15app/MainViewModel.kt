package com.team15app

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team15app.team15.BuildConfig
import com.team15app.team15.data.Player
import com.team15app.team15.R
import com.team15app.team15.data.UiTemp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    private val _uiTemp: MutableStateFlow<UiTemp?> = MutableStateFlow(null)
    val uiTemp: StateFlow<UiTemp?> = _uiTemp

    fun loadTeamFromFile(
        filePath: String,
        teamNameA: String,
        version: String
    ) {
        val uiTemp = UiTemp()

        val filePath = "$filePath/$teamNameA.csv"
        val bufferedReader = File(filePath).bufferedReader()
        try {
            val lineList = mutableListOf<String>()
            bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
            var lineNum = 0
            var offset = 0
            lineList.forEach {
                when (lineNum) {
                    0 -> {
                        if(!it.contains(version)){
                            uiTemp.teamNameA = it
                            uiTemp.jerseyGoalkeeper = R.drawable.jersey_default.toString()
                            uiTemp.jerseyOutfield = R.drawable.jersey_default.toString()
                            offset = 1
                        }
                    }
                    1-offset -> uiTemp.teamNameA = it
                    2-offset -> uiTemp.matchInfo = it
                    3-offset -> uiTemp.teamNameB = it
                    in 4-offset..18-offset -> {
                        uiTemp.listOfPlayers.add(it)
                    }
                    19 -> uiTemp.jerseyGoalkeeper = it
                    20 -> uiTemp.jerseyOutfield = it
                }
                lineNum++
            }

            uiTemp.fileName = teamNameA
            uiTemp.filePath = filePath

        } catch (e : IOException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        viewModelScope.launch {
            _uiTemp.emit(uiTemp)
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
        var fileName = "$teamNameA $teamNameB"
        fileName = fileName.replace("/", "")

        val csvDir = File(filePath)
        csvDir.mkdirs()

        if(csvFile.exists()){
            csvFile.delete() //overwrite previous file
        }
        fileName = appendNumericToFileNameIfExists(fileName, filePath)
        val csvFile = File(csvDir, "$fileName.csv")

        val fileWriter = FileWriter(csvFile, false)
        val bufferedWriter = BufferedWriter(fileWriter)
        val matchInfo = matchInfo.replace("\n", "")

        bufferedWriter.write(BuildConfig.VERSION_NAME)
        bufferedWriter.write("\n")
        bufferedWriter.write(teamNameA)
        bufferedWriter.write("\n")
        bufferedWriter.write(matchInfo)
        bufferedWriter.write("\n")
        bufferedWriter.write(teamNameB.substringAfter("vs. "))
        bufferedWriter.write("\n")
        for(item in mapOfPlayers){
            bufferedWriter.write(item.value.getNumber() + "\t" + item.value.getName())
            if(item != mapOfPlayers.lastEntry()){
                bufferedWriter.write("\n")
            }
        }
        bufferedWriter.write("\n")
        bufferedWriter.write(jerseyGoalkeeper)
        bufferedWriter.write("\n")
        bufferedWriter.write(jerseyOutfield)

        bufferedWriter.close()
        fileWriter.close()
    }

    private fun appendNumericToFileNameIfExists(fileName: String, filePath: String): String{
        var fileName = fileName

        var newFileName = fileName
        var counter = 1
        while (isFileExisting(newFileName, filePath)) {
            fileName = "$fileName($counter)"
            newFileName = fileName
            counter++
        }
        return newFileName
    }

    private fun isFileExisting(fileName: String, filePath: String): Boolean{
        val csvDir = File(filePath)
        val csvFile = File(csvDir, "$fileName.csv")

        return csvFile.exists()
    }

    fun isValidFile(file: File, version: String): Boolean{
        if(file.extension != "csv"){
            return false
        }

        val bufferedReader = file.bufferedReader()
        val firstLine = bufferedReader.readLine()

        return firstLine.contains(version)
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
    ): Boolean{
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

    fun isDuplicate(dialogTitleCustom: String, dialogTitleDefault: String): Boolean{
        return dialogTitleCustom == dialogTitleDefault
    }

    fun deleteImages(filePath: String){
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

    fun writeImageToFile(filePath: String,
        teamNameA: String,
         bitmap: Bitmap
    ) {
        val imageDir = File(filePath)
        imageDir.mkdirs()

        val dateAndTimeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFile = File(imageDir, dateAndTimeStamp + "_" +teamNameA +".jpg")

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
    }

}