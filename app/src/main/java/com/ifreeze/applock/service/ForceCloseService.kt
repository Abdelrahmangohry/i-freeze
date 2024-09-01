package com.ifreeze.applock.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.ifreeze.applock.R

/**
 * A service that manages a chat head view as an overlay on the screen.
 */
class ForceCloseService : Service() {
    private val handler = Handler()
    private var chatHeadView: View? = null // Declare the view as a field
    private val myBinder = BinderForce()
   private var windowManager: WindowManager? = null

    /**
     * Binder class for the service to provide a reference to the service itself.
     */
    inner class BinderForce : Binder() {

        /**
         * Provides a reference to the ForceCloseService service.
         *
         * @return The ForceCloseService service instance.
         */
        fun getServices(): ForceCloseService {
            return this@ForceCloseService
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
        createChatHeadView()
        super.onCreate()
    }

    /**
     * Called when the service is destroyed. Removes the chat head view and performs cleanup.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Remove the chat head view when the service is destroyed
        removeChatHeadView()
    }

    /**
     * Creates and displays a chat head view as an overlay on the screen.
     *
     * Configures the view's layout parameters and adds it to the window manager to be displayed.
     */
    fun createChatHeadView() {
        chatHeadView = LayoutInflater.from(this).inflate(R.layout.force_close, null)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        windowManager?.addView(chatHeadView, params)
    }

    /**
     * Removes the chat head view from the screen and performs cleanup.
     */
    fun removeChatHeadView() {
        chatHeadView?.let { view ->
            windowManager?.removeView(view)
            chatHeadView = null // Set the view to null after removal to prevent memory leaks
        }
    }
}
