package com.ifreeze.applock.service

import android.content.Context
import android.content.Intent
import android.os.Build
import javax.inject.Inject

/**
 * A repository class that handles starting and managing WebRTC-related intents for the `WebrtcService`.
 * Uses background threads to start the service with various actions.
 *
 * @property context The application context used to start the service.
 */
class WebrtcServiceRepository @Inject constructor(
    private val context:Context
) {

    /**
     * Starts the `WebrtcService` with the action "StartIntent" to initialize a WebRTC session.
     * Runs the operation on a background thread.
     *
     * @param username The username to pass to the service.
     */
    fun startIntent(username:String){
        val thread = Thread {
            val startIntent = Intent(context, WebrtcService::class.java)
            startIntent.action = "StartIntent"
            startIntent.putExtra("username",username)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(startIntent)
            } else {
                context.startService(startIntent)
            }
        }
        thread.start()
    }

    /**
     * Requests a connection to the specified target by starting the `WebrtcService` with the action
     * "RequestConnectionIntent".
     * Runs the operation on a background thread.
     *
     * @param target The target to request a connection to.
     */
    fun requestConnection(target: String){
        val thread = Thread {
            val startIntent = Intent(context, WebrtcService::class.java)
            startIntent.action = "RequestConnectionIntent"
            startIntent.putExtra("target",target)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(startIntent)
            } else {
                context.startService(startIntent)
            }
        }
        thread.start()
    }

    /**
     * Accepts a call from the specified target by starting the `WebrtcService` with the action
     * "AcceptCallIntent".
     * Runs the operation on a background thread.
     *
     * @param target The target of the call to accept.
     */
    fun acceptCAll(target:String){
        val thread = Thread {
            val startIntent = Intent(context, WebrtcService::class.java)
            startIntent.action = "AcceptCallIntent"
            startIntent.putExtra("target",target)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(startIntent)
            } else {
                context.startService(startIntent)
            }
        }
        thread.start()
    }

    /**
     * Ends the current call by starting the `WebrtcService` with the action "EndCallIntent".
     * Runs the operation on a background thread.
     */
    fun endCallIntent() {
        val thread = Thread {
            val startIntent = Intent(context, WebrtcService::class.java)
            startIntent.action = "EndCallIntent"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(startIntent)
            } else {
                context.startService(startIntent)
            }
        }
        thread.start()
    }

    /**
     * Stops the `WebrtcService` by starting it with the action "StopIntent".
     * Runs the operation on a background thread.
     */
    fun stopIntent() {
        val thread = Thread {

            val startIntent = Intent(context, WebrtcService::class.java)
            startIntent.action = "StopIntent"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(startIntent)
            } else {
                context.startService(startIntent)
            }
        }
        thread.start()
    }

}

