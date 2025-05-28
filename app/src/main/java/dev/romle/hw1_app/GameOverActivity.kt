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
import android.net.Uri
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

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

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

        if (requestCode == Constants.PremissionCode.LOCATION_PERMISSION_REQUEST_CODE){
            val granted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if(granted){
                handleScoreEntry()
            }else{
                val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                if (!shouldShow) {
                    showGoToSettingsDialog()
                }
                else{
                    SignalManager.getInstance().toast( "Location permission is required to save your place on the scoreboard")
                    BTN_exitButton.visibility = View.VISIBLE
                    BTN_exitButton.setOnClickListener {
                        startActivity(Intent(this, MenuActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun showGoToSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Location permission was permanently denied. Please enable it in Settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkLocationPermission() {
        val fineGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)


        if (!fineGranted || !coarseGranted) {
            if (!shouldShow) {
                // This could mean: 1st ask ever, or "Don't ask again", or "Ask every time"
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    Constants.PremissionCode.LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                // Optional: show rationale dialog if needed
                SignalManager.getInstance().toast("We need your location to show the map.")
            }
        } else {
            handleScoreEntry()
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
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setWaitForAccurateLocation(true)
            .setMaxUpdates(1)
            .build()

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location != null)
                callback(location.latitude, location.longitude)
            else
                callback(0.0, 0.0)
        }.addOnFailureListener {
            callback(0.0, 0.0)
        }
    }



    private fun requestLocationPermission() {

        val fine =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val rationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (fine != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION", "No permission. Rationale=$rationale")
            ActivityCompat.requestPermissions(this, permissions, 1001)
        }
        else{    Log.d("PERMISSION", "Permission already granted")
        }
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

    override fun onResume() {
        super.onResume()
        Log.d("PERMISSION_FLOW", "Checking location permission...")
        checkLocationPermission()
    }
}