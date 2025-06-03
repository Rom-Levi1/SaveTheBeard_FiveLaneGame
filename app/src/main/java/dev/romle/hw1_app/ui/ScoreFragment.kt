package dev.romle.hw1_app.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import dev.romle.hw1_app.R
import dev.romle.hw1_app.interfaces.HighScoreClicked
import dev.romle.hw1_app.logic.ScoreManager
import dev.romle.hw1_app.model.ScoreData


class ScoreFragment : Fragment() {

    private lateinit var scoreContainer: LinearLayout

    var highScoreItemClicked: HighScoreClicked? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_score, container, false)

        findViews(view)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {
        val inflater = LayoutInflater.from(requireContext())
        val scores = ScoreManager.getScores()

        Log.d("DEBUG_SCORE_FRAGMENT", "Total scores loaded: ${scores.size}")


        for ((index,score) in scores.withIndex()) {
            Log.d("DEBUG_SCORE_FRAGMENT", "Score #$index → Name: ${score.playerName}, Score: ${score.score}, Lat: ${score.latitude}, Lon: ${score.longitude}")

            val row = inflater.inflate(R.layout.item_score, scoreContainer, false)

            row.findViewById<TextView>(R.id.txt_player_name).text = "#${index + 1} ${score.playerName}"
            row.findViewById<TextView>(R.id.txt_score).text = score.score.toString()

            row.setOnClickListener {
                highScoreItemClicked?.onHighScoreClicked(score)

            }

            scoreContainer.addView(row)
        }
    }


    private fun findViews(view: View) {
        scoreContainer = view.findViewById(R.id.score_container)
    }

}