package dev.romle.hw1_app.utilities

import android.content.Context
import android.media.MediaPlayer
import java.lang.ref.WeakReference

class BackgroundMusicPlayer private constructor(context: Context) {
    private val contextRef = WeakReference(context)

    private var mediaPlayer: MediaPlayer? = null
    private var resId: Int = 0

    fun setResId(id:Int){
        resId = id
        initMediaPlayer()
    }

    private fun initMediaPlayer() {
        if (mediaPlayer != null)
            release()
        mediaPlayer = MediaPlayer.create(contextRef.get(), resId)
        mediaPlayer!!.isLooping = true
        mediaPlayer!!.setVolume(0.4f,0.4f)
    }

    private fun release() {
        if (mediaPlayer == null)
            return
        try {
            mediaPlayer!!.release()
            mediaPlayer = null
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
        }
    }

    companion object {
        @Volatile
        private var instance: BackgroundMusicPlayer? = null

        fun init(context: Context): BackgroundMusicPlayer {
            return instance ?: synchronized(this) {
                instance ?: BackgroundMusicPlayer(context).also { instance = it }
            }
        }

        fun getInstance(): BackgroundMusicPlayer {
            return instance ?: throw IllegalStateException(
                "BackgroundMusicPlayer must be initialized by calling init(context) before use."
            )
        }
    }

    fun playMusic(){
        if (mediaPlayer == null || !mediaPlayer!!.isPlaying)
            initMediaPlayer()
        try {
            mediaPlayer!!.start()
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
        }
    }

    fun pauseMusic(){
        if (mediaPlayer == null || !mediaPlayer!!.isPlaying)
            return
        try {
            mediaPlayer!!.pause()
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
        }
    }


    fun stopMusic(){
        if (mediaPlayer == null)
            return
        try {
            mediaPlayer!!.stop()
            release()
        } catch (ex: IllegalStateException){
            ex.printStackTrace()
        }
    }
}