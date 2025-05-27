package dev.romle.hw1_app

import android.app.Application
import dev.romle.hw1_app.utilities.BackgroundMusicPlayer
import dev.romle.hw1_app.utilities.SharedPreferencesManager
import dev.romle.hw1_app.utilities.SignalManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
        SignalManager.init(this)
        BackgroundMusicPlayer.init(this)
        BackgroundMusicPlayer.getInstance().setResId(R.raw.gamemusicloop)
    }

    override fun onTerminate() {
        super.onTerminate()
        BackgroundMusicPlayer.getInstance().stopMusic()
    }
}