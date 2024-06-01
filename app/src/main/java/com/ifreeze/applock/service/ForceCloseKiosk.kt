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
import android.widget.Button
import com.ifreeze.applock.R

class ForceCloseKiosk : Service() {
    private var chatHeadView: View? = null
    private val myBinder = BinderForce()
    private var windowManager: WindowManager? = null

    inner class BinderForce : Binder() {
        fun getServices(): ForceCloseKiosk {
            return this@ForceCloseKiosk
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onCreate() {
        Log.d("abdo", "ForceCloseKiosk onCreate kiosk: ")
        createChatHeadView()
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("abdo", "ForceCloseKiosk onDestroy kiosk")
        removeChatHeadView()
    }

    // Creates a chat head view for overlay
    fun createChatHeadView() {
        // Ensure proper handling of overlay permissions and user experience
        chatHeadView = LayoutInflater.from(this).inflate(R.layout.create_kiosk_mode, null)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val kioskService = Intent(this, KioskModeService::class.java)

        val btn = chatHeadView?.findViewById<Button>(R.id.tryHereAgain)

        btn?.setOnClickListener {
            val packageName = "com.facebook.katana"
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(intent)
        }
        

        // Configure layout parameters for the overlay
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        // Add the chat head view to the window manager
        windowManager?.addView(chatHeadView, params)
        Log.d("abdo", "createChatHeadView : view created location")
    }

    // Removes the chat head view
    fun removeChatHeadView() {
        Log.d("abdo", "removeChatHeadView :  location")
        chatHeadView?.let { view ->
            windowManager?.removeView(view)
            chatHeadView = null // Ensure the view is set to null to prevent memory leaks
        }
    }
}