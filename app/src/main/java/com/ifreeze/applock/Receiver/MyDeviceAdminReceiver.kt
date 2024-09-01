package com.ifreeze.applock.Receiver

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.util.Log

/**
 * Custom implementation of [DeviceAdminReceiver] to handle device admin events.
 *
 * This class extends [DeviceAdminReceiver] to manage device administration status changes.
 * It provides callbacks for when the device admin is enabled or disabled.
 */
class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    /**
     * Called when the device admin component is enabled.
     *
     * This method is invoked when the device administrator is enabled through the device's settings.
     * It logs a message indicating that the device admin has been enabled.
     *
     * @param context The [Context] in which the receiver is running.
     * @param intent The [Intent] that triggered this method.
     */
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d("abdo", "Device Admin Enabled")
    }

    /**
     * Called when the device admin component is disabled.
     *
     * This method is invoked when the device administrator is disabled through the device's settings.
     * It logs a message indicating that the device admin has been disabled.
     *
     * @param context The [Context] in which the receiver is running.
     * @param intent The [Intent] that triggered this method.
     */
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d("abdo", "Device Admin Disabled")
    }


}