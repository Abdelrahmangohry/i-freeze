package com.ifreeze.applock.presentation.activity

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ifreeze.applock.R

class CustomLauncherActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_launcher)

        // Handle the Overview button (Recent Apps button)
        findViewById<View>(R.id.overview_button).setOnClickListener {
            // Custom action when Overview button is pressed
            openRecentApps()
        }
    }

    private fun openRecentApps() {
        // Custom action to handle recent apps
        Toast.makeText(this, "Overview button pressed", Toast.LENGTH_SHORT).show()
    }
}