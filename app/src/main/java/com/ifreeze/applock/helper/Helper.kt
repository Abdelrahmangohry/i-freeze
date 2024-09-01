package com.ifreeze.applock.helper

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.ifreeze.data.model.AppsModel
import com.ifreeze.data.cash.PreferencesGateway
import java.io.ByteArrayOutputStream


fun Context.getWhiteListApps(
): ArrayList<AppsModel> {
    val preference = PreferencesGateway(this)
    val lockedApp = preference.getWhiteAppsList()
    val apps = ArrayList<AppsModel>()
    val pk = packageManager
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val resolveInfoList = pk.queryIntentActivities(intent, 0)
    for (resolveInfo in resolveInfoList) {
        val activityInfo = resolveInfo.activityInfo
        val name = activityInfo.loadLabel(packageManager).toString()
        val icon = activityInfo.loadIcon(packageManager)
        val packageName = activityInfo.packageName
        val iconByteArray = drawableToByteArray(icon)

        if (!packageName.matches("com.ifreeze.applock|com.android.settings".toRegex())) {
            if (lockedApp?.contains(packageName) == true) {
                apps.add(AppsModel(name, packageName, true))
            } else {
                apps.add(AppsModel(name, packageName, false))
            }
        }
    }
    return apps
}

fun Context.getBlackListApps(
): ArrayList<AppsModel> {
    val preference = PreferencesGateway(this)
    val lockedApp = preference.getLockedAppsList()
    val apps = ArrayList<AppsModel>()
    val pk = packageManager
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val resolveInfoList = pk.queryIntentActivities(intent, 0)
    for (resolveInfo in resolveInfoList) {
        val activityInfo = resolveInfo.activityInfo
        val name = activityInfo.loadLabel(packageManager).toString()
        val icon = activityInfo.loadIcon(pk)
        val iconByteArray = drawableToByteArray(icon)
        val packageName = activityInfo.packageName
        if (!packageName.matches("com.ifreeze.applock|com.android.settings".toRegex())) {
            if (lockedApp?.contains(packageName) == true) {
                apps.add(AppsModel(name, packageName, true))
            } else {
                apps.add(AppsModel(name, packageName, false))
            }
        }
    }
    return apps
}

fun Context.getAppIconByPackageName(packageName: String): Drawable? {
    return try {
        packageManager.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}

fun Context.getListApps(): ArrayList<AppsModel> {
    val apps = ArrayList<AppsModel>()
    val pk = packageManager
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val resolveInfoList = pk.queryIntentActivities(intent, 0)
    for (resolveInfo in resolveInfoList) {
        val activityInfo = resolveInfo.activityInfo
        val name = activityInfo.loadLabel(pk).toString()
        val icon = activityInfo.loadIcon(pk)
        val packageName = activityInfo.packageName
        apps.add(AppsModel(name, packageName, false))
    }
    return apps
}
fun drawableToByteArray(drawable: Drawable): ByteArray {
    val bitmap = if (drawable is BitmapDrawable) {
        drawable.bitmap
    } else {
        Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

    return ByteArrayOutputStream().apply {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
    }.toByteArray()
}
fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable && this.bitmap != null) {
        return this.bitmap
    }

    val bitmap = Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}
fun Drawable.toImageBitmap(): ImageBitmap {
    return this.toBitmap().asImageBitmap()
}
fun ByteArray.toImageBitmap(): ImageBitmap? {
    return BitmapFactory.decodeByteArray(this, 0, this.size)?.asImageBitmap()
}