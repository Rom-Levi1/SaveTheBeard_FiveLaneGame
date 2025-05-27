package dev.romle.hw1_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import dev.romle.hw1_app.utilities.Constants

class MenuActivity : AppCompatActivity() {

    private lateinit var BTN_buttons_slow : MaterialButton

    private lateinit var BTN_buttons_fast : MaterialButton

    private lateinit var BTN_sensor : MaterialButton

    private lateinit var BTN_scoreboard: MaterialButton



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initViews()
    }

    private fun initViews() {
        BTN_buttons_slow.setOnClickListener { view: View -> changeActivity(false, 0) }
        BTN_buttons_fast.setOnClickListener { view: View -> changeActivity(false, 1) }
        BTN_sensor.setOnClickListener { view: View -> changeActivity(true, 0) }
        BTN_scoreboard.setOnClickListener { view: View -> changeToScoreActivity() }
    }

    private fun changeToScoreActivity() {
        val intent = Intent(this,ScoreActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun findViews() {
        BTN_buttons_slow = findViewById(R.id.BTN_buttons_slow)
        BTN_buttons_fast = findViewById(R.id.BTN_buttons_fast)
        BTN_sensor = findViewById(R.id.BTN_sensor)
        BTN_scoreboard = findViewById(R.id.BTN_scoreboard)
    }

    private fun changeActivity(sensors: Boolean, speed: Int){
        val intent = Intent(this,MainActivity::class.java)
        val bundle = Bundle()
        bundle.putBoolean(Constants.BundleKeys.SENSORS_KEY,sensors)
        bundle.putInt(Constants.BundleKeys.SPEED_KEY,speed)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }
}