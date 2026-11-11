package com.example.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ghar_ka_hisab_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN = "key_pin"
        private const val KEY_PIN_ENABLED = "key_pin_enabled"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_SAMPLE_LOADED = "key_sample_loaded"
    }

    var isPinEnabled: Boolean
        get() = prefs.getBoolean(KEY_PIN_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_PIN_ENABLED, value).apply()

    var storedPin: String
        get() = prefs.getString(KEY_PIN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PIN, value).apply()

    var language: AppLanguage
        get() {
            val code = prefs.getString(KEY_LANGUAGE, "hi") ?: "hi"
            return if (code == "en") AppLanguage.ENGLISH else AppLanguage.HINDI
        }
        set(value) {
            prefs.edit().putString(KEY_LANGUAGE, value.code).apply()
        }

    var isSampleLoaded: Boolean
        get() = prefs.getBoolean(KEY_SAMPLE_LOADED, false)
        set(value) = prefs.edit().putBoolean(KEY_SAMPLE_LOADED, value).apply()

    fun verifyPin(input: String): Boolean {
        val pin = storedPin
        return isPinEnabled && pin.isNotEmpty() && pin == input
    }

    fun setPin(pin: String) {
        storedPin = pin
        isPinEnabled = true
    }

    fun disablePin() {
        storedPin = ""
        isPinEnabled = false
    }
}
