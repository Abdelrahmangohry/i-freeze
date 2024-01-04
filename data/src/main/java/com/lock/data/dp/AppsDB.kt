package com.lock.data.dp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lock.data.model.AppsModel
import com.lock.data.model.Converters

@Database(entities = [AppsModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppsDB : RoomDatabase() {
    abstract fun daoApps(): DaoApps
}