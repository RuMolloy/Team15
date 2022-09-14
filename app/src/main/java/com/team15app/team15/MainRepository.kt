package com.team15app.team15

import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import com.team15app.team15.data.LoadTeamUI
import com.team15app.team15.data.Player
import com.team15app.team15.util.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/*
TODO investigate warnings appearing on try/catches 'Inappropriate blocking method call'
See discussion here
https://stackoverflow.com/questions/58680028/how-to-make-inappropriate-blocking-method-call-appropriate
 */
class MainRepository(private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun loadTeamFromFile(
        filePath: String,
        teamNameA: String,
        version: String
    ): Result<LoadTeamUI> =

    withContext(defaultDispatcher) {

        val loadTeamUI = LoadTeamUI()
        val newFilePath = "$filePath/$teamNameA.csv"
        val bufferedReader = File(newFilePath).bufferedReader()
        try {
            val lineList = mutableListOf<String>()
            bufferedReader.useLines { lines -> lines.forEach { lineList.add(it) } }
            var lineNum = 0
            var offset = 0
            lineList.forEach {
                when (lineNum) {
                    0 -> {
                        if(!it.contains(version)){
                            loadTeamUI.teamNameA = it
                            loadTeamUI.jerseyGoalkeeper = R.drawable.jersey_default.toString()
                            loadTeamUI.jerseyOutfield = R.drawable.jersey_default.toString()
                            offset = 1
                        }
                    }
                    1-offset -> loadTeamUI.teamNameA = it
                    2-offset -> loadTeamUI.matchInfo = it
                    3-offset -> loadTeamUI.teamNameB = it
                    in 4-offset..18-offset -> {
                        loadTeamUI.listOfPlayers.add(it)
                    }
                    19 -> loadTeamUI.jerseyGoalkeeper = it
                    20 -> loadTeamUI.jerseyOutfield = it
                }
                lineNum++
            }

            loadTeamUI.fileName = teamNameA
            loadTeamUI.filePath = newFilePath

        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(message = e.message.toString())
        } finally {
            try {
                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Result.Success(data = loadTeamUI)
    }

    suspend fun writeTeamToFile(
        fileName: String,
        filePath: String,
        teamNameA: String,
        teamNameB: String,
        matchInfo: String,
        jerseyGoalkeeper: String?,
        jerseyOutfield: String?,
        mapOfPlayers: TreeMap<Int, Player>
    ): Result<File> =

    withContext(defaultDispatcher) {
        val csvDir = File(filePath)
        csvDir.mkdirs()

        val newCsvFile = File(csvDir, "$fileName.csv")

        try {
            val fileWriter = FileWriter(newCsvFile, false)
            val bufferedWriter = BufferedWriter(fileWriter)
            val newMatchInfo = matchInfo.replace("\n", "")

            bufferedWriter.write(BuildConfig.VERSION_NAME)
            bufferedWriter.write("\n")
            bufferedWriter.write(teamNameA)
            bufferedWriter.write("\n")
            bufferedWriter.write(newMatchInfo)
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
        catch(io: IOException) {
            Result.Error(message = io.message.toString())
        }

        Result.Success(data = newCsvFile)
    }

    suspend fun writeImageToFile(
        filePath: String,
        teamNameA: String,
        bitmap: Bitmap
    ): Result<File> =

    withContext(defaultDispatcher) {
        val imageDir = File(filePath)
        imageDir.mkdirs()

        val dateAndTimeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()).format(Date())
        val imageFile = File(imageDir, dateAndTimeStamp + "_" +teamNameA +".jpg")

        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (io: IOException) {
            Result.Error(message = io.message.toString())
        } catch (fnfe: FileNotFoundException) {
            Result.Error(message = fnfe.message.toString())
        }
        Result.Success(data = imageFile)
    }

    suspend fun getListOfSavedTeams(
        filePath: String,
        version: String
    ): ArrayList<String> =

    withContext(defaultDispatcher) {
        val listOfTeamFilesToLoad = ArrayList<String>()

        val csvDir = File(filePath)
        if (csvDir.exists()) {
            val files = csvDir.listFiles()
            if (files != null) {
                for (file in files.iterator()) {
                    if (isValidFile(file, version)) {
                        listOfTeamFilesToLoad.add(file.nameWithoutExtension)
                    }
                }
            }
        }
        listOfTeamFilesToLoad
    }

    private fun isValidFile(
        file: File,
        version: String
    ): Boolean {
        if(file.extension != "csv"){
            return false
        }

        val bufferedReader = file.bufferedReader()
        val firstLine: String? = bufferedReader.readLine()
        bufferedReader.close()

        return firstLine?.contains(version) ?: false
    }
}
