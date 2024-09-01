package com.ifreeze.data.cash

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

const val PREFERENCES_NAME = "PREFERENCES_NAME"
const val BASE_URL_KEY = "BaseUrl"

/**
 * This class provides an interface for managing shared preferences in an Android application.
 * It allows saving, loading, and removing different types of data in shared preferences,
 * as well as working with lists and JSON serialization.
 *
 * @property context The application context used to access shared preferences.
 */
class PreferencesGateway @Inject constructor(@ApplicationContext val context: Context) {

    /**
     * Saves a value in shared preferences for a given key.
     *
     * @param T The type of the value to be saved.
     * @param key The key under which the value will be stored.
     * @param value The value to be saved.
     */
    inline fun <reified T : Any> save(key: String, value: T) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply {
                putValue(key, value) }
            .apply()
    }

    /**
     * Loads a value from shared preferences for a given key.
     *
     * @param T The type of the value to be loaded.
     * @param key The key under which the value is stored.
     * @param defaultValue The default value to return if the key does not exist.
     * @return The value associated with the key, or the default value if the key does not exist.
     */
    inline fun <reified T : Any> load(key: String, defaultValue: T): T? {
        return context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .run { getValue(key, defaultValue) }
    }

    /**
     * Removes a value from shared preferences for a given key.
     */
    fun remove(key: String) {
        context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(key)
            .apply()
    }


    /**
     * Extension function to save a value of type [T] in shared preferences.
     */
    inline fun <reified T : Any> SharedPreferences.Editor.putValue(
        key: String,
        value: T
    ) {
        when (T::class) {
            Boolean::class -> putBoolean(key, value as Boolean)
            Int::class -> putInt(key, value as Int)
            Long::class -> putLong(key, value as Long)
            Float::class -> putFloat(key, value as Float)
            String::class -> putString(key, value as String)
            else -> throw UnsupportedOperationException("not supported preferences type")
        }
    }

    /**
     * Extension function to load a value of type [T] from shared preferences.
     */
    inline fun <reified T : Any?> SharedPreferences.getValue(
        key: String,
        defaultValue: T?
    ): T? {
        return when (T::class) {
            Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
            Int::class -> getInt(key, defaultValue as Int) as T
            Long::class -> getLong(key, defaultValue as Long) as T
            Float::class -> getFloat(key, defaultValue as Float) as T
            String::class -> getString(key, defaultValue as String) as T
            else -> throw UnsupportedOperationException("not supported preferences type")
        }
    }

//    * Updates an existing value in shared preferences for a given key.
    inline fun <reified T : Any> update(key: String, value: T) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply { putValue(key, value) }
            .apply()
    }

    val gson: Gson = Gson()

//    * Serializes an object and saves it as a JSON string in shared preferences.

    inline fun <reified T : Any> saveUser(key: String, value: T) {
        val json = gson.toJson(value)
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply { putString(key, json) }
            .apply()
    }

    /**
     * Retrieves a list of locked apps from shared preferences.
     * @return A list of locked app package names, or an empty list if none are found.
     */
    fun getLockedAppsList(): List<String>? {
        val temp: MutableList<String> = ArrayList()
        val size: Int = load("listSize",0)?:0
        for (i in 0 until size) {
            temp.add(load("app_$i","")?:"")
        }
        return temp
    }

    /**
     * Retrieves a list of white-listed apps from shared preferences.
     * @return A list of white-listed app package names, or an empty list if none are found.
     */
    fun getWhiteAppsList(): List<String>? {
        val temp: MutableList<String> = ArrayList()
        val size: Int = load("whitelistSize",0)?:0
        for (i in 0 until size) {
            temp.add(load("white_$i","")?:"")
        }
        return temp
    }

    /**
     * Saves the package name of the last accessed app in shared preferences.
     * @param packageName The package name of the last accessed app.
     */
    fun setLastApp(packageName: String?) {
        save("EXTRA_LAST_APP", packageName?:"")
    }


    /**
     * Serializes a list of strings and saves it in shared preferences.
     *
     * @param key The key under which the JSON string will be stored.
     * @param list The list of strings to be serialized and saved.
     */
    fun saveList(key: String, list: List<String>) {
        val jsonString = gson.toJson(list)
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(key, jsonString)
            .apply()
    }

    /**
     * Deserializes and retrieves a list of strings from shared preferences.
     *
     * @param key The key under which the JSON string is stored.
     * @return A list of strings, or an empty list if the key does not exist.
     */
    fun getList(key: String): ArrayList<String> {
        val jsonString = context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(key, null)

        return if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<ArrayList<String>>() {}.type)
        } else {
            ArrayList()
        }
    }

    /**
     * Retrieves the shared preferences instance.
     *
     * @param context The application context used to access shared preferences.
     * @return The shared preferences instance.
     */
    fun getPrefVal(context: Context): SharedPreferences {
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences
    }


    /**
     * Saves a string value in shared preferences for a given key.
     *
     * @param context The application context used to access shared preferences.
     * @param key The key under which the value will be stored.
     * @param value The string value to be saved.
     */
    fun setPrefVal(context: Context, key: String, value: String) {
        if (key != null) {
            val editor: SharedPreferences.Editor = getPrefVal(context).edit()
            editor.putString(key, value)
            editor.apply()
        }
    }

    /**
     * Saves the base URL in shared preferences.
     *
     * @param baseUrl The base URL to be saved.
     */
    fun saveBaseUrl(baseUrl: String) {
        save(BASE_URL_KEY, baseUrl)
    }

    /**
     * Loads the base URL from shared preferences.
     *
     * @return The base URL, or an empty string if the key does not exist.
     */
    fun loadBaseUrl(): String? {
        return load(BASE_URL_KEY, "")
    }

    /**
     * Saves a double value in shared preferences for a given key.
     *
     * @param key The key under which the value will be stored.
     * @param value The double value to be saved.
     */
    fun saveDouble(key: String, value: Double) {
        save(key, value)
    }

    /**
     * Loads a double value from shared preferences for a given key.
     *
     * @param key The key under which the value is stored.
     * @param defaultValue The default value to return if the key does not exist.
     * @return The double value associated with the key, or the default value if the key does not exist.
     */
    fun loadDouble(key: String, defaultValue: Double): Double? {
        return load(key, defaultValue)
    }


}

