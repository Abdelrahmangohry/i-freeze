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
        val builder = AlertDialog.Builder(this) // Use 'this' instead of applicationContext
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password, null)

        builder.setView(dialogView)

        val input = dialogView.findViewById<EditText>(R.id.passwordInput)
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)

        // Set up number buttons
        val numberButtons = listOf(
            dialogView.findViewById<Button>(R.id.button1),
            dialogView.findViewById<Button>(R.id.button2),
            dialogView.findViewById<Button>(R.id.button3),
            dialogView.findViewById<Button>(R.id.button4),
            dialogView.findViewById<Button>(R.id.button5),
            dialogView.findViewById<Button>(R.id.button6),
            dialogView.findViewById<Button>(R.id.button7),
            dialogView.findViewById<Button>(R.id.button8),
            dialogView.findViewById<Button>(R.id.button9),
            dialogView.findViewById<Button>(R.id.button10),
            dialogView.findViewById<Button>(R.id.button11),
        )

        for (button in numberButtons) {
            button.setOnClickListener {
                input.append(button.text)
            }
        }

        // Set up delete button
        val buttonDelete = dialogView.findViewById<Button>(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            val currentText = input.text.toString()
            if (currentText.isNotEmpty()) {
                input.setText(currentText.dropLast(1))
            }
        }

        val alertDialog = builder.create()
        // Set up OK button
        buttonOk.setOnClickListener {
            val enteredPassword = input.text.toString()
            if (enteredPassword == correctPassword) {
                preferenc.update("BlockState", false)
                Toast.makeText(this, "Kiosk mode stopped", Toast.LENGTH_SHORT).show()
                removeChatHeadView()
                alertDialog.dismiss()
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        }


        alertDialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        alertDialog.show()

        // Adjust the dialog size to match typical AlertDialog
        val window = alertDialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams
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