/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 *
 */
package com.naumankhaliq.imovies.utils

import android.content.SharedPreferences
import javax.inject.Inject
/**
 * A helper class that will be used to manage data for [SharedPreferences]
 * Only class that will be used to communicate with [SharedPreferences]
 */
class PreferenceHelper @Inject constructor(private val preferences: SharedPreferences) {

    fun saveAccessToken(accessToken: String) {
        preferences.edit().putString(ACCESS_TOKEN_KEY, accessToken).apply()
    }
    fun getAccessToken(): String? {
        return preferences.getString(ACCESS_TOKEN_KEY, "")
    }
    fun deleteAccessToken() {
        preferences.edit().remove(ACCESS_TOKEN_KEY).apply()
    }
    fun saveLastVisitedTime(timeInMilli: Long) {
        preferences.edit().putLong(LAST_VISITED_KEY, timeInMilli).apply()
    }
    fun getLastVisitedTime(): Long {
        return preferences.getLong(LAST_VISITED_KEY, 0L)
    }
    fun deleteLastVisitedTime() {
        preferences.edit().remove(LAST_VISITED_KEY).apply()
    }
    companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
        const val LAST_VISITED_KEY = "last_visited"
    }
}