package app.android.serv.util

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

import app.android.serv.Serv

/**
 * Created by kombo on 28/03/2018.
 */
object PrefUtils {

    const val USER = "user"

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        val settings = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE)
        return settings.getBoolean(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        val settings = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE)
        return settings.getInt(key, defValue)
    }

    fun putInt(key: String, value: Int) {
        val editor = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        val settings = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE)
        return settings.getLong(key, defValue)
    }

    fun putLong(key: String, value: Long) {
        val editor = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getString(key: String, defValue: String): String? {
        val settings = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE)
        return settings.getString(key, defValue)
    }

    fun putString(key: String, value: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun remove(key: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE).edit()
        editor.remove(key)
        editor.apply()
    }

    fun registerOnPrefChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        try {
            PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE).registerOnSharedPreferenceChangeListener(listener)
        } catch (ignored: Exception) { // Seems to be possible to have a NPE here... Why??
            Log.e("PrefUtils", ignored.message, ignored)
        }

    }

    fun unregisterOnPrefChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        try {
            PreferenceManager.getDefaultSharedPreferences(Serv.INSTANCE).unregisterOnSharedPreferenceChangeListener(listener)
        } catch (ignored: Exception) { // Seems to be possible to have a NPE here... Why??
            Log.e("PrefUtils", ignored.message, ignored)
        }

    }
}
