package dev.romle.hw1_app

import android.app.Application
import dev.romle.hw1_app.utilities.SignalManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}