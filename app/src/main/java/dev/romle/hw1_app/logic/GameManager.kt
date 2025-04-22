package dev.romle.hw1_app.logic

import dev.romle.hw1_app.model.DataManager
import dev.romle.hw1_app.utilities.Constants

class GameManager(private val lifeCount: Int = 3) {


    var disqualifications: Int = 0
        private set

    private var playerIndex = 1

    val isGameOver: Boolean
        get() = disqualifications == lifeCount

    fun checkCollision(){
        if (DataManager.obstacles[6][playerIndex] == 1)
            disqualifications++
    }

    fun moveLeft()
    {
        if (playerIndex > 0)
            playerIndex--
    }

    fun moveRight()
    {
        if (playerIndex < 2)
            playerIndex++
    }

    fun getPlayerIndex(): Int = playerIndex

    fun newObstacleIndex(): Int{
        return (0..2).random()
    }

    fun arrangeObstacles() {
        for (i in 6 downTo 1)
            for (j in 0 .. 2)
                DataManager.obstacles[i][j] = DataManager.obstacles[i - 1][j]

        val newIndex = newObstacleIndex()

        for (i in 0..2)
            if (i == newIndex)
                DataManager.obstacles[0][i] = 1
            else
                DataManager.obstacles[0][i] = 0
    }

}
