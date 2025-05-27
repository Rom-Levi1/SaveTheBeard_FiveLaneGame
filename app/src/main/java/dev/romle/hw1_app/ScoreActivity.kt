package dev.romle.hw1_app

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.romle.hw1_app.interfaces.HighScoreClicked
import dev.romle.hw1_app.model.ScoreData
import dev.romle.hw1_app.ui.MapFragment
import dev.romle.hw1_app.ui.ScoreFragment
import dev.romle.hw1_app.utilities.SignalManager
private val LOCATION_PERMISSION_REQUEST_CODE = 1002


class ScoreActivity : AppCompatActivity() {

    private lateinit var main_FRAME_highScores : FrameLayout

    private lateinit var main_FRAME_map : FrameLayout

    private lateinit var mapFragment : MapFragment

    private lateinit var scoreFragment: ScoreFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_score)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        if (!hasLocationPermission()) {
            requestLocationPermission()
        } else {
            initViews()
        }
    }


    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            initViews()
        } else {
            SignalManager.getInstance().toast("Location permission is needed to view the map.")
        }
    }


    private fun initViews() {
        mapFragment = MapFragment()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_FRAME_map, mapFragment)
            .commit()


        scoreFragment = ScoreFragment()

        scoreFragment.highScoreItemClicked = object : HighScoreClicked {
            override fun onHighScoreClicked(scoreData: ScoreData) {
                mapFragment.zoom(scoreData)
            }
        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_FRAME_highScores, scoreFragment)
            .commit()


    }

    private fun findViews() {
        main_FRAME_map = findViewById(R.id.main_FRAME_map)
        main_FRAME_highScores = findViewById(R.id.main_FRAME_highScores)
    }


}