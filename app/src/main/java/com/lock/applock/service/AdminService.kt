package com.lock.applock.service

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DeviceAdminService
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.UserHandle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.getSystemService
import com.lock.applock.R

class AdminService : DeviceAdminReceiver() {
    private var chatHeadView: View? = null
    private var windowManager: WindowManager? = null

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        Log.d("islam", "onProfileProvisioningComplete : ")
        super.onProfileProvisioningComplete(context, intent)
    }

    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        showToast(context, "onEnabled")
        if (chatHeadView != null)
            removeChatHeadView(context)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        showToast(context, "onDisabled")
        createChatHeadView(context)

    }

    override fun onPasswordChanged(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordChanged(context, intent, user)
        showToast(context, "onPasswordChanged")
    }

    private fun createChatHeadView(context: Context) {
        // Ensure proper handling of overlay permissions and user experience
        chatHeadView = LayoutInflater.from(context).inflate(R.layout.force_close_admin, null)
        windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

        // Configure layout parameters for the overlay
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        // Add the chat head view to the window manager
        windowManager?.addView(chatHeadView, params)
        Log.d("islam", "createChatHeadView : view created ")
    }

    private fun removeChatHeadView(context: Context) {
        chatHeadView = LayoutInflater.from(context).inflate(R.layout.force_close_admin, null)
        windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager

        chatHeadView?.let { view ->
            windowManager?.removeView(view)
            chatHeadView = null // Ensure the view is set to null to prevent memory leaks
        } ?: Log.e("ForceCloseService", "chatHeadView is null")

    }

}