package com.team15app

import com.team15app.team15.BuildConfig
import com.team15app.team15.R
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class MainRepository() {

//    fun writeTeamToFile() {
//        var fileName = binding.contentMain.rlMatchInfo.tvMatchInfoTeamA.text.toString() + " " + binding.contentMain.rlMatchInfo.tvMatchInfoTeamB.text.toString()
//        fileName = fileName.replace("/", "")
//        this.fileName = fileName
//
//        val csvDir = File(getExternalFilesDir(null).toString() + "/" + getString(R.string.app_name))
//        csvDir.mkdirs()
//
//        if(csvFile.exists()){
//            csvFile.delete() //overwrite previous file
//        }
//        fileName = appendNumericToFileNameIfExists(fileName)
//        csvFile = File(csvDir, "$fileName.csv")
//
//        val fileWriter = FileWriter(csvFile, false)
//        val bufferedWriter = BufferedWriter(fileWriter)
//        val matchInfo = binding.contentMain.rlMatchInfo.tvMatchInfoNameRead.text.toString().replace("\n", "")
//
//        bufferedWriter.write(BuildConfig.VERSION_NAME)
//        bufferedWriter.write("\n")
//        bufferedWriter.write(binding.contentMain.rlMatchInfo.tvMatchInfoTeamA.text.toString())
//        bufferedWriter.write("\n")
//        bufferedWriter.write(matchInfo)
//        bufferedWriter.write("\n")
//        bufferedWriter.write(binding.contentMain.rlMatchInfo.tvMatchInfoTeamB.text.toString().substringAfter("vs. "))
//        bufferedWriter.write("\n")
//        for(item in binding.contentMain.viewPitch.mapOfPlayers){
//            bufferedWriter.write(item.value.getNumber() + "\t" + item.value.getName())
//            if(item != binding.contentMain.viewPitch.mapOfPlayers.lastEntry()){
//                bufferedWriter.write("\n")
//            }
//        }
//        bufferedWriter.write("\n")
//        bufferedWriter.write(team.jerseyGoalkeeper)
//        bufferedWriter.write("\n")
//        bufferedWriter.write(team.jerseyOutfield)
//
//        bufferedWriter.close()
//        fileWriter.close()
//    }
}