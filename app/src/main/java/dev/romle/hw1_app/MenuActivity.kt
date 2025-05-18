package dev.romle.hw1_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MenuActivity : AppCompatActivity() {

    private lateinit var BTN_buttons_slow : MaterialButton

    private lateinit var BTN_buttons_fast : MaterialButton

    private lateinit var BTN_sensor : MaterialButton


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

    }

    private fun findViews() {
        BTN_buttons_slow = findViewById(R.id.BTN_buttons_slow)
        BTN_buttons_fast = findViewById(R.id.BTN_buttons_fast)
        BTN_sensor = findViewById(R.id.BTN_sensor)
    }
}