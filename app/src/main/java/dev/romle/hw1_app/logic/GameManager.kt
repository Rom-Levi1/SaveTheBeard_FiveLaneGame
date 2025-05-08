package dev.romle.hw1_app.logic

import dev.romle.hw1_app.model.DataManager
import dev.romle.hw1_app.utilities.Constants

class GameManager(private val lifeCount: Int = 3) {

    var flag = false

    var disqualifications: Int = 0
        private set

    private var playerIndex = 2

    val isGameOver: Boolean
        get() = disqualifications == lifeCount

    fun checkCollision(): Boolean {
        if (DataManager.obstacles[7][playerIndex] == 1){
            disqualifications++
            flag = false
            return true
        }
        return false
    }

    fun moveLeft()
    {
        if (playerIndex > 0)
            playerIndex--
    }

    fun moveRight()
    {
        if (playerIndex < 5)
            playerIndex++
    }

    fun getPlayerIndex(): Int = playerIndex

    fun newObstacleIndex(): Int{
        return (0..4).random()
    }

    fun arrangeObstacles() {
        for (i in 7 downTo 1)
            for (j in 0 .. 4)
                DataManager.obstacles[i][j] = DataManager.obstacles[i - 1][j]

        val newIndex = newObstacleIndex()

        for (i in 0..4)
            if (i == newIndex)
                DataManager.obstacles[0][i] = 1
            else
                DataManager.obstacles[0][i] = 0
    }

}
