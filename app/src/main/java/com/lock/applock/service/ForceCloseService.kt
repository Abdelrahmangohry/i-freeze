package com.lock.applock.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.lock.applock.R

class ForceCloseService : Service() {
    private var chatHeadView: View? = null // Declare the view as a field
    private val myBinder = BinderForce()
   private var windowManager: WindowManager? = null

    inner class BinderForce : Binder() {

        fun getServices(): ForceCloseService {
            return this@ForceCloseService
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChatHeadView()
        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onCreate() {
        createChatHeadView()
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove the chat head view when the service is destroyed
        removeChatHeadView()
    }

    // Function to create the chat head view
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

    // Function to remove the chat head view
    fun removeChatHeadView() {
        chatHeadView?.let { view ->
            windowManager?.removeView(view)
            chatHeadView = null // Set the view to null after removal
        } ?: Log.e("ForceCloseService", "chatHeadView is null")
    }
}
