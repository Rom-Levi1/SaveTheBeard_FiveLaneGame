package dev.romle.hw1_app.interfaces

import dev.romle.hw1_app.model.ScoreData

interface HighScoreClicked {
    fun onHighScoreClicked(scoreData: ScoreData)
}