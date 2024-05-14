package com.ifreeze.data.dp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ifreeze.data.model.AppsModel
import com.ifreeze.data.model.Converters

@Database(entities = [AppsModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppsDB : RoomDatabase() {
    abstract fun daoApps(): DaoApps
}