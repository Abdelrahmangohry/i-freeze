package com.ifreeze.applock.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.ifreeze.applock.R

/**
 * A service that manages a chat head view as an overlay on the screen, specifically for handling Wi-Fi related actions.
 */
class ForceCloseWifi : Service() {
    private var chatHeadView: View? = null
    private val myBinder = BinderForce()
    private var windowManager: WindowManager? = null

    /**
     * Binder class for the service to provide a reference to the service itself.
     */
    inner class BinderForce : Binder() {

        /**
         * Provides a reference to the ForceCloseWifi service.
         *
         * @return The ForceCloseWifi service instance.
         */
        fun getServices(): ForceCloseWifi {
            return this@ForceCloseWifi
        }
    }

    /**
     * Called when a client binds to this service.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return The binder instance for this service.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    /**
     * Called when the service is first created. Initializes the service and creates the chat head view.
     */
    override fun onCreate() {
        Log.d("islam", "ForceCloseWifi onCreate: ")
        createChatHeadView()
        super.onCreate()
    }

    /**
     * Called when the service is destroyed. Removes the chat head view and performs cleanup.
     */
    override fun onDestroy() {
        super.onDestroy()
        removeChatHeadView()
    }

    /**
     * Creates and displays a chat head view as an overlay on the screen.
     *
     * Configures the view's layout parameters and adds it to the window manager to be displayed.
     */
    fun createChatHeadView() {
        // Ensure proper handling of overlay permissions and user experience
        chatHeadView = LayoutInflater.from(this).inflate(R.layout.force_close_wifi, null)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
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

    /**
     * Removes the chat head view from the screen and performs cleanup.
     */
    fun removeChatHeadView() {
        Log.d("islam", "removeChatHeadView :  ")
        chatHeadView?.let { view ->
            windowManager?.removeView(view)
            chatHeadView = null // Ensure the view is set to null to prevent memory leaks
        }
    }
}