package dev.romle.hw1_app.model

import dev.romle.hw1_app.R

class DataManager {
    companion object{
        var obstacles = Array(7) {IntArray(3)}
            private set

        var imageIndex = 0

        val playerImages = arrayOf(
            R.drawable.beard,
            R.drawable.goatee,
            R.drawable.mustache,
            R.drawable.shaved
        )

    }
}