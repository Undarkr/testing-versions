package id.cervicam.mobile.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class LocalStorage {
    enum class PreferenceKeys(val value: String) {
        ID("ID"),
        USERNAME("USERNAME"),
        PASSWORD("PASSWORD"),
        TOKEN("TOKEN")
    }

    companion object {
        private const val PREFERENCE_ID = "CERVICAM_PREF"

        private fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPrefer