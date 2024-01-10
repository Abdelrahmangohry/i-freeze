package com.lock.data.repo

import android.content.Context
import android.content.Intent
import com.lock.data.dp.AppsDB
import com.lock.data.model.AppsModel
import javax.inject.Inject

class ApksRepoImp @Inject constructor(private val room: AppsDB, private val context: Context) :
    ApksRepo {
    override suspend fun saveApps(list: List<AppsModel>) {
        room.daoApps().insertApps(list)
    }

    override suspend fun updateApp(app: AppsModel) {
        room.daoApps().update(app)
    }

    override suspend fun deleteApp(app: String) {
        room.daoApps().deleteAppByPackageName(app)
    }

    override suspend fun getAllApps(): List<AppsModel> = room.daoApps().getAppsList()
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

    private fun isBrowserPackage(packageName: String): Boolean {
        val launcherPackages = listOf(
            "com.android.chrome", "org.mozilla.firefox", "com.microsoft.emmx",
            "com.opera.browser", "com.brave.browser", "com.sec.android.app.sbrowser", "com.UCMobile.intl"
        )
        return launcherPackages.any { packageName.startsWith(it) }
    }
}