package dev.romle.hw1_app.logic

import android.content.SharedPreferences
import dev.romle.hw1_app.model.DataManager
import dev.romle.hw1_app.model.ScoreData
import dev.romle.hw1_app.utilities.SharedPreferencesManager

object ScoreManager {
    fun getScores(): MutableList<ScoreData> {
        return SharedPreferencesManager.getInstance()
            .loadScoreDataList()
            .toMutableList()
    }

    fun addScore(newScore: ScoreData) {
        val scores = getScores().toMutableList()

        if (scores.size < 10){
            scores.add(newScore)
        }
        else{
            val minScoreData = scores.last()
            if (newScore.score > minScoreData.score){
                scores.remove(minScoreData)
                scores.add(newScore)
            }
            else {
                return
            }
        }

        val sortedScores = scores.sortedByDescending { it.score }

        saveScores(sortedScores.toMutableList())
    }


    fun saveScores(scores: MutableList<ScoreData>) {
        SharedPreferencesManager.getInstance().saveScoreDataList(scores)
    }

}