package com.ifreeze.applock.service

import android.app.AlertDialog
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ifreeze.applock.R
import com.ifreeze.applock.helper.getAppIconByPackageName
import com.ifreeze.applock.helper.toImageBitmap
import com.ifreeze.applock.presentation.adapter.AdapterKiosk
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.patient.data.cashe.PreferencesGateway

class ForceCloseKiosk : Service() {
    private var chatHeadView: View? = null
    private val myBinder = BinderForce()
    private var windowManager: WindowManager? = null
    private val correctPassword = "123"
    private lateinit var preferenc: PreferencesGateway
    inner class BinderForce : Binder() {
        fun getServices(): ForceCloseKiosk {
            return this@ForceCloseKiosk
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    override fun onCreate() {
        preferenc = PreferencesGateway(applicationContext)
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
        val kioskPackageList = preferenc.getList("kioskApplications")

        val recyclerView = chatHeadView?.findViewById<RecyclerView>(R.id.firstRow)
//        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.layoutManager = GridLayoutManager(this, 3)
        recyclerView?.adapter = AdapterKiosk(this, kioskPackageList)

        val iFreeze = chatHeadView?.findViewById<ImageView>(R.id.i_freeze)

        iFreeze?.setOnClickListener {
            showPasswordDialog()
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


    private fun showPasswordDialog() {
        val builder = AlertDialog.Builder(applicationContext)
        builder.setTitle("Enter Password")

        val input = EditText(applicationContext)
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val enteredPassword = input.text.toString()
            if (enteredPassword == correctPassword) {
                preferenc.update("BlockState", false)
                Toast.makeText(applicationContext, "Kiosk mode stopped", Toast.LENGTH_SHORT).show()
                removeChatHeadView()
//
            } else {
                dialog.dismiss()
                Toast.makeText(applicationContext, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        // Ensure the AlertDialog uses a theme that allows it to be displayed from a Service context
        val alertDialog = builder.create()
        alertDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        alertDialog.show()
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