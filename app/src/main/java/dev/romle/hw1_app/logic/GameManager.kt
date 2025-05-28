package dev.romle.hw1_app.logic

import dev.romle.hw1_app.model.DataManager


class GameManager(private val lifeCount: Int = 3) {


    var flag = true

    var score : Int = 0

    var disqualifications: Int = 0
        private set

    private var playerIndex = 2

    val isGameOver: Boolean
        get() = disqualifications == lifeCount

    fun checkCollision(): Int {
        if (DataManager.obstacles[7][playerIndex] == 1){
            disqualifications++
            flag = false
            return 1
        }

        else if (DataManager.obstacles[7][playerIndex] == 2){
            score += 10
            return 2
        }

        else if (DataManager.obstacles[7][playerIndex] == 3 && disqualifications > 0){
            disqualifications--
            flag = false
            return 3
        }
        return 0
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

    fun newDropItemIndex(): Int{
        return (0..4).random()
    }

    fun dropItemIdentity(): Int{
        return (1..10).random()
    }


    fun arrangeObstacles() {
        for (i in 7 downTo 1)
            for (j in 0 .. 4)
                DataManager.obstacles[i][j] = DataManager.obstacles[i - 1][j]

        val newIndex = newDropItemIndex()

        for (i in 0..4)
            if (i == newIndex) {
                val identity = dropItemIdentity() % 10
                //obstacles
                if (identity <= 6)
                    DataManager.obstacles[0][i] = 1
                //coins
                else if (identity == 8 || identity == 7)
                    DataManager.obstacles[0][i] = 2
                //life
                else if (identity == 9)
                    DataManager.obstacles[0][i] = 3
                else
                    DataManager.obstacles[0][i] = 0
            }
            else {
                DataManager.obstacles[0][i] = 0
            }
    }


}
