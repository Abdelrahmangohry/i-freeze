package com.ifreeze.data.dp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ifreeze.data.model.AppsModel
import com.ifreeze.data.model.Converters


/**
 * The Room Database class for managing the app's data storage.
 * This class defines the database configuration and serves as the main access point
 * for the underlying connection to the app's persisted data.
 *
 * @Database annotation specifies the list of entities and the database version.
 * @TypeConverters annotation indicates the use of type converters for custom data types.
 *
 * @property daoApps The data access object (DAO) for accessing and managing data in the database.
 */
@Database(entities = [AppsModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppsDB : RoomDatabase() {

    /**
     * Provides the DAO for accessing the database operations related to the `AppsModel` entity.
     *
     * @return The DAO object for `AppsModel`.
     */
    abstract fun daoApps(): DaoApps
}