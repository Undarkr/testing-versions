package id.cervicam.mobile.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class LocalStorage {
    enum class PreferenceKeys(val value: String) {
        ID("ID"),
        USERNAME("USERNAME"),
        PASSWORD("P