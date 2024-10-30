/*
 * MIT License
 *
 * Copyright (c) 2022 Nauman Khaliq
 *
 */
package com.naumankhaliq.imovies.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    /**
     * Formats date and time return only time
     * @return [String] Formatted time
     */
    fun formatDateTime(date: String): String? {
        return try {
            var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            format.timeZone = TimeZone.getTimeZone("UTC")
            val newDate = format.parse(date)
            //format = SimpleDateFormat("dd-M-yyyy hh:mm a")
            format = SimpleDateFormat("hh:mm a")
            format.timeZone = TimeZone.getDefault()
            format.format(newDate)
        } catch (ex: Exception) {
            null
        }

    }

    /**
     * Formats time in milli sec to date and time return
     * @return [String] Formatted date and time
     */
    fun formatDateTime(timeInMillis: Long): String? {
        return try {
            val date = Date(timeInMillis)
            //val format = SimpleDateFormat("MMM dd yyyy EEE hh:mm a")
            val format = SimpleDateFormat("MMM dd yyyy hh:mm a")
            format.timeZone = TimeZone.getTimeZone("UTC")
            format.timeZone = TimeZone.getDefault()
            format.format(date)
        } catch (ex: Exception) {
            null
        }
    }
}