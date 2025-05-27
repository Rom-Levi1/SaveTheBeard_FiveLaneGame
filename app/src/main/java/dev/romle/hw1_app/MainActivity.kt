package dev.romle.hw1_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
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
import androidx.core.view.isVisible
import com.google.android.material.textview.MaterialTextView
import dev.romle.hw1_app.utilities.BackgroundMusicPlayer
import dev.romle.hw1_app.utilities.SingleSoundPlayer
import dev.romle.hw1_app.interfaces.TiltCallback
import dev.romle.hw1_app.model.ScoreData
import dev.romle.hw1_app.utilities.TiltDetector
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.romle.hw1_app.utilities.SharedPreferencesManager


class MainActivity : AppCompatActivity() {

    private lateinit var BTN_Left : MaterialButton

    private lateinit var BTN_Right : MaterialButton

    private lateinit var IMG_beards : Array<AppCompatImageView>

    private lateinit var main_IMG_hearts :  Array<AppCompatImageView>

    private lateinit var gameManager: GameManager

    private lateinit var obstacleViews: Array<Array<AppCompatImageView>>

    private lateinit var main_LBL_score: MaterialTextView

    private var startTime: Long = 0

    private var timerOn: Boolean = false

    private lateinit var timerJob: Job

    private var gameDelay = Constants.Timer.DELAY_SLOW //default

    private var lastHitTime = 0L

    private lateinit var tiltDetector: TiltDetector


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

        initViews()

        initTiltDetector()

        gameLoop()

    }

    private fun initTiltDetector() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback{
                override fun tiltX() {
                    val index = gameManager.getPlayerIndex()

                    if (tiltDetector.tiltCounterX == 1 && index < 4){
                        gameManager.moveRight()
                        moveRightUI()
                    }

                    else if (tiltDetector.tiltCounterX == -1 && index > 0){
                        gameManager.moveLeft()
                        moveLeftUI()
                    }
                }
            }
        )
    }


    private fun findViews() {
        main_LBL_score = findViewById(R.id.main_LBL_score)
        BTN_Left = findViewById(R.id.BTN_Left)
        BTN_Right = findViewById(R.id.BTN_Right)

        IMG_beards = arrayOf(
            findViewById(R.id.IMG_beard0),
            findViewById(R.id.IMG_beard1),
            findViewById(R.id.IMG_beard2),
            findViewById(R.id.IMG_beard3),
            findViewById(R.id.IMG_beard4)
        )

        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )


        obstacleViews = Array (8) {row ->
            Array (5) {col ->
                val resId = resources.getIdentifier("IMG_razor${row}_${col}", "id", packageName)
                findViewById<AppCompatImageView>(resId) // <-- return directly!
            }
        }
    }

    private fun initViews() {
        val bundle: Bundle? = intent.extras

        val sensors = bundle?.getBoolean(Constants.BundleKeys.SENSORS_KEY,false)!!
        val speed = bundle?.getInt(Constants.BundleKeys.SPEED_KEY,0)

        gameDelay = when (speed){
            0 -> Constants.Timer.DELAY_SLOW
            1-> Constants.Timer.DELAY_FAST
            else -> Constants.Timer.DELAY_SLOW
        }

        main_LBL_score.text = "000"

        initMode(sensors)

        for (i in 0..4) {
            if (i != 2)
                IMG_beards[i].visibility = View.INVISIBLE
            else
                IMG_beards[i].visibility = View.VISIBLE
        }


        for (i in 0..7)
            for (j in 0..4)
                obstacleViews[i][j].visibility = View.INVISIBLE


    }


    //true = sensors , false = buttons
    private fun initMode(mode: Boolean){
        if (mode)
        {
            BTN_Left.visibility = View.INVISIBLE
            BTN_Right.visibility = View.INVISIBLE

        }
        else{
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
        }

    }

    private fun moveRightUI() {
        positionBeard(gameManager.getPlayerIndex())
    }

    private fun moveLeftUI() {
        positionBeard(gameManager.getPlayerIndex())
    }

    private fun updateButtonsState() {
        BTN_Left.isEnabled = gameManager.getPlayerIndex() > 0
        BTN_Right.isEnabled = gameManager.getPlayerIndex() < 4
    }

    private fun gameLoop() {

        if (!timerOn) {
            timerOn = true
            startTime = System.currentTimeMillis()
            timerJob = lifecycleScope.launch {
                while (timerOn){
                    gameManager.arrangeObstacles()
                    val collisionVal = gameManager.checkCollision()
                    if (collisionVal == 1) {
                        val now = System.currentTimeMillis()
                        if (now - lastHitTime > 1000) {
                            toasts()
                            vibrate()
                            var ssp = SingleSoundPlayer(this@MainActivity)
                            ssp.playSound(R.raw.ohmygod)
                            lastHitTime = now
                        }
                    }
                    else if (collisionVal == 2){
                        var ssp = SingleSoundPlayer(this@MainActivity)
                        ssp.playSound(R.raw.videogamebonus)
                    }
                    gameManager.score++
                    updateUI()
                   if(gameManager.isGameOver){
                       timerOn = false
                       changeActivity()
                       return@launch
                    }

                    delay(gameDelay)
                }
            }
        }
    }

    private fun changeActivity() {
        val intent = Intent(this, GameOverActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(Constants.BundleKeys.SCORE_KEY,gameManager.score)
        intent.putExtras(bundle)
        startActivity(intent)
        finish()
    }

    private fun updateUI() {
        main_LBL_score.text = gameManager.score.toString()
        updateDropItems()
        updateLife()
    }

    private fun updateDropItems(){
        for (i in 0..7)
            for (j in 0..4) {
                if (DataManager.obstacles[i][j] == 1){
                    obstacleViews[i][j].setImageResource(DataManager.dropItems[0])
                    obstacleViews[i][j].visibility = View.VISIBLE
                }

                else if (DataManager.obstacles[i][j] == 2){
                    obstacleViews[i][j].setImageResource(DataManager.dropItems[1])
                    obstacleViews[i][j].visibility = View.VISIBLE
                }

                else if (DataManager.obstacles[i][j] == 3){
                    obstacleViews[i][j].setImageResource(DataManager.dropItems[2])
                    obstacleViews[i][j].visibility = View.VISIBLE
                }

                else
                    obstacleViews[i][j].visibility = View.INVISIBLE
            }
    }

    private fun updateLife() {
        if (gameManager.flag) return

        val disq = gameManager.disqualifications

        if (disq in 1..3) {
            val heartIndex = main_IMG_hearts.size - disq

            if (main_IMG_hearts[heartIndex].isVisible) {
                // Lose life
                main_IMG_hearts[heartIndex].visibility = View.INVISIBLE
                updateBeardUI(false)
            } else {
                // Gain life back
                main_IMG_hearts[heartIndex - 1].visibility = View.VISIBLE
                updateBeardUI(true)
            }
        }

        if (disq == 0) {
            // Full health restoration (maybe from a pickup)
            for (heart in main_IMG_hearts) heart.visibility = View.VISIBLE
            updateBeardUI(true)
        }

        gameManager.flag = true
    }



    private fun positionBeard(lane: Int) {
        for (i in 0..4){
            if (i != lane)
                IMG_beards[i].visibility = View.INVISIBLE
            else
                IMG_beards[i].visibility = View.VISIBLE
        }

    }

    private fun updateBeardUI(gainLife: Boolean){
        if (!gainLife)
            DataManager.imageIndex++
        else
            DataManager.imageIndex--

        for (i in 0..4) {
            findViewById<AppCompatImageView>(DataManager.beardImageIds[i])
                .setImageResource(DataManager.playerImages[DataManager.imageIndex])
        }
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


    override fun onResume() {
        super.onResume()
        tiltDetector.start()
        BackgroundMusicPlayer.getInstance().playMusic()
    }

    override fun onPause() {
        super.onPause()
        tiltDetector.stop()
        BackgroundMusicPlayer.getInstance().pauseMusic()
    }
}
