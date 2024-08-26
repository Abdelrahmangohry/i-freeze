package com.ifreeze.applock.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ifreeze.applock.databinding.ActivityLoginScreenSharingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivityScreenSharing : AppCompatActivity() {
    lateinit var views: ActivityLoginScreenSharingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityLoginScreenSharingBinding.inflate(layoutInflater)
        setContentView(views.root)

        views.enterBtn.setOnClickListener {
            if (views.usernameEt.text.isNullOrEmpty()){
                Toast.makeText(this, "please fill the username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(
                Intent(this,MainActivityScreenSharing::class.java).apply {
                    putExtra("username",views.usernameEt.text.toString())
                }
            )
        }

    }
}