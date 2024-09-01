package com.ifreeze.applock.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.util.Log
import android.widget.Toast

/**
 * DeviceAdminReceiver subclass for handling device administration events.
 *
 * This class is used to respond to various device administration events, such as enabling or disabling
 * the device admin, password changes, and profile provisioning. It provides feedback to the user via toast messages.
 */
class AdminService: DeviceAdminReceiver() {

    /**
     * Called when profile provisioning is complete.
     *
     * @param context The context in which the receiver is running.
     * @param intent The intent containing the details of the provisioning event.
     */
    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        Log.d("islam", "onProfileProvisioningComplete : ")
        super.onProfileProvisioningComplete(context, intent)
    }

    /**
     * Shows a toast message to the user.
     *
     * @param context The context in which to show the toast.
     * @param msg The message to be displayed in the toast.
     */
    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Called when the device admin is enabled.
     *
     * @param context The context in which the receiver is running.
     * @param intent The intent containing the details of the enabling event.
     */
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        showToast(context,"onEnabled")

    }

    /**
     * Called when the device admin is disabled.
     *
     * @param context The context in which the receiver is running.
     * @param intent The intent containing the details of the disabling event.
     */
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        showToast(context,"onDisabled")
    }

    /**
     * Called when the password is changed.
     *
     * @param context The context in which the receiver is running.
     * @param intent The intent containing the details of the password change event.
     * @param user The user whose password was changed.
     */
    override fun onPasswordChanged(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordChanged(context, intent, user)
        showToast(context,"onPasswordChanged")
    }
}