package com.patient.data.cashe

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lock.data.model.AppsModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

const val PREFERENCES_NAME = "PREFERENCES_NAME"

class PreferencesGateway @Inject constructor(@ApplicationContext val context: Context) {

    inline fun <reified T : Any> save(key: String, value: T) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply {
                Log.d("islam", "save : ${value} ")
                putValue(key, value) }
            .apply()
    }

    fun createLockedAppsList(appList: List<String>) {
        for (i in appList.indices) {
            save("app_$i", appList[i])
        }
        save("listSize", appList.size)
    }

    fun createWithListApps(appList: List<String>) {
        for (i in appList.indices) {
            save("white_$i", appList[i])
        }
        save("whitelistSize", appList.size)
    }

    inline fun <reified T : Any> load(key: String, defaultValue: T): T? {
        return context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .run { getValue(key, defaultValue) }
    }

    fun isSaved(key: String): Boolean {
        return context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .contains(key)
    }
    fun remove(key: String) {
        context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(key)
            .apply()
    }

    fun clearAll() {
        context
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

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
    inline fun <reified T : Any> update(key: String, value: T) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply { putValue(key, value) }
            .apply()
    }

    val gson: Gson = Gson()
    inline fun <reified T : Any> saveUser(key: String, value: T) {
        val json = gson.toJson(value)
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .apply { putString(key, json) }
            .apply()
    }

    fun getLockedAppsList(): List<String>? {
        val temp: MutableList<String> = ArrayList()
        val size: Int = load("listSize",0)?:0
        for (i in 0 until size) {
            temp.add(load("app_$i","")?:"")
        }
        return temp
    }
    fun insertSSIDName(ssid :String){
        val size=load("SSIDSize",0)?:0
        save("SSIDSize",size+1)
        save("SSID$size",ssid)
    }
    fun getSSIDList(): List<String>? {
        val temp: MutableList<String> = ArrayList()
        val size: Int = load("SSIDSize",0)?:0
        for (i in 0 until size) {
            temp.add(load("SSID$i","")?:"")
        }
        return temp
    }

    fun getWhiteAppsList(): List<String>? {
        val temp: MutableList<String> = ArrayList()
        val size: Int = load("whitelistSize",0)?:0
        for (i in 0 until size) {
            temp.add(load("white_$i","")?:"")
        }
        return temp
    }
    fun setLastApp(packageName: String?) {
        save("EXTRA_LAST_APP", packageName?:"")
    }

    fun clearBlackList() {
        val size: Int = load("listSize",0)?:0
        for (i in 0 until size) {
            remove("app_$i")
        }
        remove("listSize")
    }
    fun clearWhiteList() {
        val size: Int = load("listSize",0)?:0
        for (i in 0 until size) {
            remove("white_$i")
        }
        remove("whitelistSize")

    }

    fun saveList(key: String, list: List<String>) {
        val jsonString = gson.toJson(list)
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(key, jsonString)
            .apply()
    }

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

    //get shared preferences
    fun getPrefVal(context: Context): SharedPreferences {
        val sharedPreferences =
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences
    }


    //set shared preferences
    fun setPrefVal(context: Context, key: String, value: String) {
        if (key != null) {
            val editor: SharedPreferences.Editor = getPrefVal(context).edit()
            editor.putString(key, value)
            editor.apply()
        }
    }

}

