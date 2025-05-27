package dev.romle.hw1_app

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.romle.hw1_app.utilities.Constants
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.EditText
import dev.romle.hw1_app.utilities.SignalManager
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import dev.romle.hw1_app.logic.ScoreManager
import dev.romle.hw1_app.model.ScoreData

class GameOverActivity : AppCompatActivity() {

    private lateinit var BTN_exitButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_over)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermission()

        } else {
            // access after permission
            handleScoreEntry()
        }

    }

    private fun findViews() {
        BTN_exitButton = findViewById(R.id.BTN_exitButton)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            handleScoreEntry()
        } else {
            SignalManager.getInstance().toast( "Location permission is required to save your place on the scoreboard")
            BTN_exitButton.visibility = View.VISIBLE
            BTN_exitButton.setOnClickListener {
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            }
        }
    }

    private fun askPlayerName(onNameEntered: (String) -> Unit) {
        val input = EditText(this).apply {
            hint = "Enter your name"
        }

        AlertDialog.Builder(this)
            .setTitle("Game Over!")
            .setMessage("What's your name?")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    onNameEntered(name)
                } else {
                    SignalManager.getInstance().toast("Name can't be empty")
                    askPlayerName(onNameEntered)
                }
            }
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(callback: (Double, Double) -> Unit) {
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation
            .addOnSuccessListener { location ->
                if (location != null)
                    callback(location.latitude, location.longitude)
                else
                    callback(0.0, 0.0)
            }
            .addOnFailureListener {
                callback(0.0, 0.0)
            }
    }


    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, 1001)
    }


    private fun handleScoreEntry() {
        val bundle: Bundle? = intent.extras
        val score = bundle?.getInt(Constants.BundleKeys.SCORE_KEY,0)

        askPlayerName { playerName ->
            getLastLocation { lat, lon ->
                val newScore = ScoreData(playerName, score!! , lat, lon)
                Log.d("HANDLE_SCORE_ENTRY", "Saving score for: name='$playerName', score=$score, lat=$lat, lon=$lon")
                ScoreManager.addScore(newScore)
                startActivity(Intent(this, ScoreActivity::class.java))
                finish()
            }
        }
    }
}