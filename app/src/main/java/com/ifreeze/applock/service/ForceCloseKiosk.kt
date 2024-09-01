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
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.adapter.AdapterKiosk
import com.ifreeze.data.cash.PreferencesGateway


/**
 * A service that manages a chat head view for a kiosk mode. The service allows for showing
 * a floating chat head with options and handles password protection to stop kiosk mode.
 */
class ForceCloseKiosk : Service() {
    private var chatHeadView: View? = null
    private val myBinder = BinderForce()
    private var windowManager: WindowManager? = null
    private val correctPassword = "123"
    private lateinit var preferenc: PreferencesGateway

    /**
     * Binder class for the service to provide a reference to the service itself.
     */
    inner class BinderForce : Binder() {
        /**
         * Provides a reference to the ForceCloseKiosk service.
         *
         * @return The ForceCloseKiosk service instance.
         */
        fun getServices(): ForceCloseKiosk {
            return this@ForceCloseKiosk
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
        preferenc = PreferencesGateway(applicationContext)
        Log.d("abdo", "ForceCloseKiosk onCreate kiosk: ")
        createChatHeadView()
        super.onCreate()
    }

    /**
     * Called when the service is destroyed. Removes the chat head view and performs cleanup.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d("abdo", "ForceCloseKiosk onDestroy kiosk")
        removeChatHeadView()
    }

    /**
     * Creates and displays a chat head view as an overlay on the screen.
     *
     * Configures the view's layout parameters and sets up interaction elements such as a
     * RecyclerView for kiosk applications and a button to show a password dialog.
     */
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

        // Configure layout parameters for the chat head view
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


    /**
     * Displays a password dialog to allow the user to stop the kiosk mode.
     *
     * Sets up number buttons for password input and provides options for deleting
     * input and confirming the password. If the correct password is entered, kiosk mode is stopped.
     */
    private fun showPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_password, null)

        builder.setView(dialogView)

        val input = dialogView.findViewById<EditText>(R.id.passwordInput)
        val buttonOk = dialogView.findViewById<Button>(R.id.buttonOk)

        // Set up number buttons for password input
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

        // Set up delete button for input
        val buttonDelete = dialogView.findViewById<Button>(R.id.buttonDelete)
        // Set up OK button to verify the password
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

    /**
     * Removes the chat head view from the screen and performs cleanup.
     */
    fun removeChatHeadView() {
        Log.d("abdo", "removeChatHeadView :  location")
        chatHeadView?.let { view ->
            windowManager?.removeView(view)
            chatHeadView = null // Ensure the view is set to null to prevent memory leaks
        }
    }
}