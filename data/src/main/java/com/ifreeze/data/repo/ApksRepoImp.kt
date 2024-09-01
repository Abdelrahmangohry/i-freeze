package com.ifreeze.data.repo

import android.content.Context
import android.content.Intent
import com.ifreeze.data.dp.AppsDB
import com.ifreeze.data.model.AppsModel
import javax.inject.Inject

/**
 * Implementation of the `ApksRepo` interface that provides methods to interact with the local database and the device's installed applications.
 *
 * @param room The Room database instance used to access the application's database.
 * @param context The context used to access system services, such as the package manager.
 */
class ApksRepoImp @Inject constructor(private val room: AppsDB, private val context: Context) :
    ApksRepo {

// Saves a list of apps to the local database.

    override suspend fun saveApps(list: List<AppsModel>) {
        room.daoApps().insertApps(list)
    }


     //Updates an existing app in the local database.

    override suspend fun updateApp(app: AppsModel) {
        room.daoApps().update(app)
    }

    //Deletes an app from the local database by its package name.
    override suspend fun deleteApp(app: String) {
        room.daoApps().deleteAppByPackageName(app)
    }

    /**
     * Retrieves a list of all apps stored in the local database.
     * @return A list of `AppsModel` objects representing the apps stored in the database.
     */
    override suspend fun getAllApps(): List<AppsModel> = room.daoApps().getAppsList()

    /**
     * Retrieves a list of all installed apps on the device that are not browser apps.
     * An `ArrayList` of `AppsModel` objects representing the installed apps on the device.
     */
    override suspend fun getAllAppsFromDevice(): ArrayList<AppsModel> {
        val apps = ArrayList<AppsModel>()
        val pk = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = pk.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfoList) {
            val activityInfo = resolveInfo.activityInfo
            val name = activityInfo.loadLabel(pk).toString()
            val packageName = activityInfo.packageName
            if (!isBrowserPackage(packageName)) {
                apps.add(AppsModel(name, packageName, false))
            }
        }
        return apps
    }

    /**
     * Checks whether the given package name belongs to a browser app.
     *
     * @param packageName The package name to be checked.
     * @return `true` if the package name belongs to a browser app, `false` otherwise.
     */
    private fun isBrowserPackage(packageName: String): Boolean {
        val launcherPackages = listOf(
            "com.android.chrome", "org.mozilla.firefox", "com.microsoft.emmx",
            "com.opera.browser", "com.brave.browser", "com.sec.android.app.sbrowser", "com.UCMobile.intl"
        )
        return launcherPackages.any { packageName.startsWith(it) }
    }
}