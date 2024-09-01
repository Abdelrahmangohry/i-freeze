package com.ifreeze.data.dp


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ifreeze.data.model.AppsModel

/**
 * Data Access Object (DAO) interface for performing database operations on the `AppsModel` entity.
 * This interface provides methods to insert, update, delete, and retrieve app data in the database.
 */
@Dao
interface DaoApps {

    /**
     * Inserts a list of apps into the database.
     * If an app with the same primary key already exists, it will be replaced.
     *
     * @param app The list of `AppsModel` objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(app : List<AppsModel>)
    /**
     * Updates the details of an existing app in the database.
     *
     * @param app The `AppsModel` object containing the updated app information.
     */
    @Update
    suspend fun update(app  : AppsModel)
    /**
     * Deletes an app from the database by its package name.
     *
     * @param packageName The package name of the app to be deleted.
     */
    @Query("DELETE FROM Apps WHERE packageName = :packageName")
    suspend fun deleteAppByPackageName(packageName: String)
    /**
     * Retrieves a list of all apps stored in the database.
     *
     * @return A list of `AppsModel` objects representing all apps in the database.
     */
    @Query("SELECT * FROM Apps")
    suspend fun getAppsList(): List<AppsModel>
}