package dev.romle.hw1_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import dev.romle.hw1_app.logic.GameManager
import dev.romle.hw1_app.model.DataManager
import dev.romle.hw1_app.utilities.Constants
import dev.romle.hw1_app.utilities.SignalManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var BTN_Left : MaterialButton

    private lateinit var BTN_Right : MaterialButton

    private lateinit var IMG_beard1 : AppCompatImageView

    private lateinit var main_IMG_hearts :  Array<AppCompatImageView>

    private lateinit var gameManager: GameManager

    private lateinit var obstacleViews: Array<Array<AppCompatImageView>>

    private lateinit var obstacleLayout: RelativeLayout

    private var startTime: Long = 0

    private var timerOn: Boolean = false

    private lateinit var timerJob: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        gameManager = GameManager()

        findViews()

        obstacleLayout.post {
            Handler(Looper.getMainLooper()).postDelayed({
                positionRazors()
            }, 100)
            positionBeard(gameManager.getPlayerIndex())
        }

        initViews()

        obstacleTimer()

    }

    private fun findViews() {
        BTN_Left = findViewById(R.id.BTN_Left)
        BTN_Right = findViewById(R.id.BTN_Right)
        IMG_beard1 = findViewById(R.id.IMG_beard1)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )

        obstacleLayout = findViewById(R.id.main)

        obstacleViews = Array (7) {row ->
            Array (3) {col ->
                val resId = resources.getIdentifier("IMG_razor${row}_${col}", "id", packageName)
                findViewById<AppCompatImageView>(resId) // <-- return directly!
            }
        }
    }

    private fun initViews() {

        BTN_Left.setOnClickListener{
            gameManager.moveLeft()
            moveLeftUI()
            updateButtonsState()
        }

        BTN_Right.setOnClickListener {
            gameManager.moveRight()
            moveRightUI()
            updateButtonsState()
        }

        for (i in 0..6)
            for (j in 0..2)
                obstacleViews[i][j].visibility = View.INVISIBLE


    }

    private fun moveRightUI() {
        positionBeard(gameManager.getPlayerIndex())
    }

    private fun moveLeftUI() {
        positionBeard(gameManager.getPlayerIndex())
    }

    private fun updateButtonsState() {
        BTN_Left.isEnabled = gameManager.getPlayerIndex() > 0
        BTN_Right.isEnabled = gameManager.getPlayerIndex() < 2
    }

    private fun obstacleTimer() {

        if (!timerOn) {
            timerOn = true
            startTime = System.currentTimeMillis()
            timerJob = lifecycleScope.launch {
                while (timerOn){
                    gameManager.arrangeObstacles()
                    if(gameManager.checkCollision()){
                        toasts()
                        vibrate()
                    }
                    updateUI()
                    delay(Constants.Timer.DELAY)
                }
            }
        }
    }

    private fun updateUI() {
        for (i in 0..6)
            for (j in 0..2) {
                if (DataManager.obstacles[i][j] == 1)
                    obstacleViews[i][j].visibility = View.VISIBLE
                else
                    obstacleViews[i][j].visibility = View.INVISIBLE
            }
        if (gameManager.disqualifications != 0 && gameManager.disqualifications <= 3 && gameManager.flag == false ) {
            main_IMG_hearts[main_IMG_hearts.size - gameManager.disqualifications]
                .visibility = View.INVISIBLE
            updateBeardUI()
            gameManager.flag = true
        }

    }

    private fun positionRazors() {
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val rowCount = 7
        val laneCount = 3

        val rowHeight = screenHeight * 0.75f / rowCount
        val razorSize = resources.getDimensionPixelSize(R.dimen.razor_dimen)

        val lanePositions = listOf(
            screenWidth * 1 / 6f,
            screenWidth * 3 / 6f,
            screenWidth * 5 / 6f
        )

        for (row in 0 until rowCount) {
            for (lane in 0 until laneCount) {
                val razor = obstacleViews[row][lane]

                val params = RelativeLayout.LayoutParams(
                    razorSize,
                    razorSize
                )

                val lanePosition = lanePositions[lane]
                val marginStart = (lanePosition - razorSize / 2).toInt()
                val marginTop = (row * rowHeight).toInt()

                params.setMargins(marginStart, marginTop, 0, 0)

                razor.layoutParams = params
            }
        }
    }

    private fun positionBeard(lane: Int) {
        val screenWidth = obstacleLayout.width
        val screenHeight = obstacleLayout.height

        val rowCount = 9
        val laneCount = 3

        val rowHeight = screenHeight * 0.85f / rowCount

        val beardWidth = IMG_beard1.width
        val beardHeight = IMG_beard1.height

        val laneCenters = listOf(
            screenWidth * 1 / 6f,
            screenWidth * 3 / 6f,
            screenWidth * 5 / 6f
        )

        val lanePosition = laneCenters[lane]
        val marginStart = (lanePosition - beardWidth / 2).toInt()
        val marginTop = (7 * rowHeight + (rowHeight - beardHeight) / 2).toInt()

        val params = RelativeLayout.LayoutParams(
            beardWidth,
            beardHeight
        )

        params.setMargins(marginStart, marginTop, 0, 0)

        IMG_beard1.layoutParams = params
    }

    private fun updateBeardUI(){
        DataManager.imageIndex++
        IMG_beard1.setImageResource(DataManager.playerImages[DataManager.imageIndex])
    }

    private fun toasts(){

        if(gameManager.disqualifications == 2) {
            SignalManager
                .getInstance()
                .toast("Leave me alone!!")
        }
        else if(gameManager.disqualifications == 3){
            SignalManager
                .getInstance()
                .toast("Game Over!, you need to train")
        }

        else {
            SignalManager
                .getInstance()
                .toast("Ouch!!")
        }

    }

    private fun vibrate(){
        SignalManager
            .getInstance()
            .vibrate()
    }
}