package com.ifreeze.data.model


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream


/**
 * Data class representing the `Apps` entity in the database.
 * This class defines the structure of the app data stored in the database.
 *
 * @Entity annotation specifies the table name in the database as "Apps".
 *
 * @property appName The name of the app. This is the primary key for the entity.
 * @property packageName The package name of the app.
 * @property status The status of the app (e.g., whether it is active or not).
 * @property statusWhite The whitelist status of the app. Defaults to `false`.
 */
@Entity(tableName = "Apps")
data class AppsModel(
    @PrimaryKey
    val appName: String,
    val packageName: String,
    var status: Boolean,
    var statusWhite: Boolean=false,
)
class Converters {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
            return toByteArray()
        }
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}

