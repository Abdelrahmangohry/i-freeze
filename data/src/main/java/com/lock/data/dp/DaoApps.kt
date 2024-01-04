package com.lock.data.dp


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lock.data.model.AppsModel

@Dao
interface DaoApps {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(app : List<AppsModel>)
    @Update
    suspend fun update(app  : AppsModel)
    @Query("DELETE FROM Apps WHERE packageName = :packageName")
    suspend fun deleteAppByPackageName(packageName: String)
    @Query("SELECT * FROM Apps")
    suspend fun getAppsList(): List<AppsModel>
}