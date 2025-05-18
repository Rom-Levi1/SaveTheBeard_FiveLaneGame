package dev.romle.hw1_app.model

import dev.romle.hw1_app.R

class DataManager {
    companion object{
        var obstacles = Array(8) {IntArray(5)}
            private set

        var imageIndex = 0

        val dropItems = arrayOf(
            R.drawable.razor,
            R.drawable.coin,
            R.drawable.heart,
        )

        val playerImages = arrayOf(
            R.drawable.beard,
            R.drawable.goatee,
            R.drawable.mustache,
            R.drawable.shaved
        )

    }
}