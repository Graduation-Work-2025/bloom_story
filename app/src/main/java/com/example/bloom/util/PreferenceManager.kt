package com.example.bloom.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREF_NAME = "bloom_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearAccessToken() {
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply()
    }
    fun setCharacterId(characterId: Int) {
        prefs.edit().putInt("character_id", characterId).apply()
    }

    fun getCharacterId(): Int? {
        val id = prefs.getInt("character_id", -1)
        return if (id == -1) null else id
    }

}
