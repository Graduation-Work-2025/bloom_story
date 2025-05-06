package com.example.bloom.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREF_NAME = "bloom_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_CHARACTER_ID = "character_id"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // ✅ Access Token
    fun setAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearAccessToken() {
        prefs.edit().remove(KEY_ACCESS_TOKEN).apply()
    }

    // ✅ 캐릭터 ID
    fun setCharacterId(characterId: Int) {
        prefs.edit().putInt(KEY_CHARACTER_ID, characterId).apply()
    }

    fun getCharacterId(): Int? {
        val id = prefs.getInt(KEY_CHARACTER_ID, -1)
        return if (id == -1) null else id
    }

    // ✅ 닉네임 저장/조회
    fun setNickname(nickname: String) {
        prefs.edit().putString(KEY_NICKNAME, nickname).apply()
    }

    fun getNickname(): String? {
        return prefs.getString(KEY_NICKNAME, null)
    }

    // ✅ 프로필 이미지 URI 저장/조회
    fun setProfileImageUri(uri: String) {
        prefs.edit().putString(KEY_PROFILE_IMAGE_URI, uri).apply()
    }

    fun getProfileImageUri(): String? {
        return prefs.getString(KEY_PROFILE_IMAGE_URI, null)
    }

    private const val KEY_USER_ID = "user_id"

    fun setUserId(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
    }

    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)
        return if (id == -1) null else id
    }



}
