package dev.romle.hw1_app.utilities

import android.content.Context
import android.content.SharedPreferences
import android.system.Os.remove
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.romle.hw1_app.model.ScoreData

class SharedPreferencesManager private constructor(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(
            Constants.SP_KEYS.SCORE_KEY,
            Context.MODE_PRIVATE
        )

    companion object{
        @Volatile
        private var instance: SharedPreferencesManager? = null

        fun init(context: Context): SharedPreferencesManager{
            return instance ?: synchronized(this){
                instance ?: SharedPreferencesManager(context).also { instance = it }
            }
        }

        fun getInstance(): SharedPreferencesManager {
            return instance ?: throw IllegalStateException(
                "SharedPreferencesManagerV3 must be initialized by calling init(context) before use."
            )
        }
    }


    fun putString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences
            .getString(
                key, defaultValue
            ) ?: defaultValue
    }

    fun saveScoreDataList(scoreDataList: MutableList<ScoreData>){
        val json = Gson().toJson(scoreDataList)
        getInstance().putString(Constants.SP_KEYS.SCORE_KEY,json)
    }

    fun loadScoreDataList(): List<ScoreData>{
        val json = getInstance().getString(Constants.SP_KEYS.SCORE_KEY,"[]")

        Log.d("SP_JSON_LOAD", "Raw JSON loaded: $json") // 👈 LOG THE RAW JSON STRING

        val type = object : TypeToken<List<ScoreData>>(){}.type
        return Gson().fromJson<List<ScoreData>>(json, type).toMutableList()
    }

    fun clearScores() {
        sharedPreferences.edit()
            .remove(Constants.SP_KEYS.SCORE_KEY)
            .apply()
    }
}