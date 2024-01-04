package com.lock.data.model


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

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

