package org.techtown.cryptoculus.repository.model

import android.content.SharedPreferences

interface SavedSharedPreferences {
    fun getExchange(): String

    fun putExchange(value: String)

    fun getSortMode(): Int

    fun putSortMode(value: Int)
}

class SavedSharedPreferencesImpl(private val preferences: SharedPreferences) : SavedSharedPreferences {
    override fun getExchange() = preferences.getString("exchange", "Coinone")!!

    override fun putExchange(value: String) = preferences.edit().putString("exchange", value).apply()

    override fun getSortMode() = preferences.getInt("sortMode", 0)

    override fun putSortMode(value: Int) = preferences.edit().putInt("sortMode", value).apply()
}