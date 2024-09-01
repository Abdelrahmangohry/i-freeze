package com.ifreeze.applock.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ifreeze.applock.R
import com.ifreeze.applock.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.MediaStream
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject


/**
 * A foreground service that handles WebRTC-related operations such as managing calls,
 * screen sharing, and notifications. Implements `MainRepository.Listener` to receive
 * updates from the `MainRepository`.
 */
@AndroidEntryPoint
class WebrtcService @Inject constructor() : Service() , MainRepository.Listener {


    companion object {
        var screenPermissionIntent : Intent ?= null
        var surfaceView:SurfaceViewRenderer?=null
        var listener: MainRepository.Listener?=null
    }

    @Inject lateinit var mainRepository: MainRepository

    private lateinit var notificationManager: NotificationManager
    private lateinit var username:String

    /**
     * Called when the service is created. Initializes the notification manager and sets
     * the listener for the `MainRepository`.
     */
    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(
            NotificationManager::class.java
        )
        mainRepository.listener = this
    }

    /**
     * Called when the service is started. Handles various intents to manage WebRTC calls
     * and screen sharing.
     *
     * @param intent The Intent that started this service.
     * @param flags Additional data about the service start request.
     * @param startId An identifier for this specific start request.
     * @return START_STICKY to indicate that the service should be restarted if it is killed.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent!=null){
            when(intent.action){
                "StartIntent"->{
                    this.username = intent.getStringExtra("username").toString()
                    surfaceView?.let { mainRepository.init(username ?: "", it) }
                    startServiceWithNotification()
                }
                "StopIntent"->{
                    stopMyService()
                }
                "EndCallIntent"->{
                    mainRepository.sendCallEndedToOtherPeer()
                    mainRepository.onDestroy()
                    stopMyService()
                }
                "AcceptCallIntent"->{
                    val target = intent.getStringExtra("target")
                    target?.let {
                        mainRepository.startCall(it)
                    }
                }
                "RequestConnectionIntent"->{
                    val target= intent.getStringExtra("target")
                    target?.let {
                        screenPermissionIntent?.let { it1 ->
                            mainRepository.setPermissionIntentToWebrtcClient(
                                it1
                            )
                        }
                        surfaceView?.let { it1 -> mainRepository.startScreenCapturing(it1) }
                        mainRepository.sendScreenShareConnection(it)
                    }
                }
            }
        }

        return START_STICKY
    }

    /**
     * Stops the service, cleans up resources, and cancels all notifications.
     */
    private fun stopMyService(){
        mainRepository.onDestroy()
        stopSelf()
        notificationManager.cancelAll()
    }

    /**
     * Starts the service in the foreground with a notification.
     * Creates a notification channel if the Android version is Oreo or higher.
     */
    private fun startServiceWithNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                "channel1","foreground",NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
            val notification = NotificationCompat.Builder(this,"channel1")
                .setSmallIcon(R.mipmap.ic_launcher)

            startForeground(1,notification.build())
        }

    }

    /**
     * Called when a connection request is received.
     *
     * @param target The target of the connection request.
     */
    override fun onConnectionRequestReceived(target: String) {
        listener?.onConnectionRequestReceived(target)
    }


    /**
     * Called when a connection is successfully established.
     */
    override fun onConnectionConnected() {
        listener?.onConnectionConnected()
    }


    /**
     * Called when a call end signal is received.
     */
    override fun onCallEndReceived() {
        listener?.onCallEndReceived()
        stopMyService()
    }

    /**
     * Called when a remote media stream is added.
     *
     * @param stream The media stream that was added.
     */
    override fun onRemoteStreamAdded(stream: MediaStream) {
        listener?.onRemoteStreamAdded(stream)
    }

    /**
     * Called when a client binds to this service. This service does not provide binding, so returns null.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return null since this service does not support binding.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}