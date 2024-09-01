package com.ifreeze.applock.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifreeze.applock.databinding.ActivityLoginScreenSharingBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity for user login in the screen sharing feature of the application.
 * Handles user input and navigates to the main activity upon successful login.
 */
@AndroidEntryPoint
class LoginActivityScreenSharing : AppCompatActivity() {
    // View binding instance for accessing the layout views
    lateinit var views: ActivityLoginScreenSharingBinding


    /**
     * Called when the activity is first created.
     * Initializes the view binding and sets up the click listener for the enter button.
     *
     * @param savedInstanceState The saved instance state bundle, or null if there is no saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        views = ActivityLoginScreenSharingBinding.inflate(layoutInflater)
        setContentView(views.root)

        // Set up the click listener for the enter button
        views.enterBtn.setOnClickListener {
            // Check if the username field is empty
            if (views.usernameEt.text.isNullOrEmpty()){
                // Show a toast message if the username is not provided
                Toast.makeText(this, "please fill the username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Start MainActivityScreenSharing and pass the username as an extra
            startActivity(
                Intent(this,MainActivityScreenSharing::class.java).apply {
                    putExtra("username",views.usernameEt.text.toString())
                }
            )
        }

    }
}