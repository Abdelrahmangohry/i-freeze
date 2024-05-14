package com.ifreeze.applock

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.patient.data.cashe.PreferencesGateway

class SharedPreferencesProvider : ContentProvider() {

    private lateinit var sharedPreferences: PreferencesGateway

    companion object {
        private const val AUTHORITY = "com.ifreeze.applock.provider"
        private const val CONTENT_PATH = "shared_prefs"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$CONTENT_PATH")

        private const val PREFERENCES_TABLE = 1
        private const val PREFERENCES_TABLE_ROW = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, CONTENT_PATH, PREFERENCES_TABLE)
            addURI(AUTHORITY, "$CONTENT_PATH/#", PREFERENCES_TABLE_ROW)
        }
    }

    override fun onCreate(): Boolean {
        sharedPreferences = PreferencesGateway(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return null // No need to implement for shared preferences
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (uriMatcher.match(uri)) {
            PREFERENCES_TABLE -> {
                val key = values?.getAsString("key") ?: throw IllegalArgumentException("Key cannot be null")
                val value = values.getAsString("value")
                sharedPreferences.save(key, value)
                ContentUris.withAppendedId(CONTENT_URI, 1)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Updates are not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Deletions are not supported")
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            PREFERENCES_TABLE -> "vnd.android.cursor.dir/vnd.$AUTHORITY.$CONTENT_PATH"
            PREFERENCES_TABLE_ROW -> "vnd.android.cursor.item/vnd.$AUTHORITY.$CONTENT_PATH"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }
}