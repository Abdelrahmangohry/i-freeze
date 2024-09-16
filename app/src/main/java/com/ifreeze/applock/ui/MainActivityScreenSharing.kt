package com.ifreeze.applock.ui

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ifreeze.applock.databinding.ActivityMainScreenSharingBinding
import com.ifreeze.applock.repository.MainRepository
import com.ifreeze.applock.service.WebrtcService
import com.ifreeze.applock.service.WebrtcServiceRepository
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.MediaStream
import javax.inject.Inject


/**
 * Main activity for handling screen sharing functionality.
 * This activity manages the screen sharing process, including requesting connections, handling incoming connection requests,
 * and displaying remote video streams.
 */
@AndroidEntryPoint
class MainActivityScreenSharing : AppCompatActivity(), MainRepository.Listener {

    private var username:String?=null
    lateinit var views:ActivityMainScreenSharingBinding

    @Inject lateinit var webrtcServiceRepository: WebrtcServiceRepository
    private val capturePermissionRequestCode = 1


    /**
     * Called when the activity is first created.
     * Initializes the view binding and sets up necessary components for screen sharing.
     *
     * @param savedInstanceState The saved instance state bundle, or null if there is no saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views= ActivityMainScreenSharingBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()

    }

    /**
     * Initializes the activity, sets up the WebRTC service, and configures the request button.
     */
    private fun init(){
        username = intent.getStringExtra("username")
        if (username.isNullOrEmpty()){
            finish()
        }
        WebrtcService.surfaceView = views.surfaceView
        WebrtcService.listener = this
        webrtcServiceRepository.startIntent(username!!)
        views.requestBtn.setOnClickListener {
            startScreenCapture()
        }

    }


    /**
     * Handles the result of the screen capture permission request.
     *
     * @param requestCode The request code that was used to start the activity.
     * @param resultCode The result code returned by the activity.
     * @param data The intent containing the result data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != capturePermissionRequestCode) return
        WebrtcService.screenPermissionIntent = data
        webrtcServiceRepository.requestConnection(
            views.targetEt.text.toString()
        )
    }

    /**
     * Starts the screen capture process by requesting user permission.
     */
    private fun startScreenCapture(){
        val mediaProjectionManager = application.getSystemService(
            Context.MEDIA_PROJECTION_SERVICE
        ) as MediaProjectionManager

        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(), capturePermissionRequestCode
        )
    }


    /**
     * Called when a connection request is received.
     *
     * @param target The username of the user requesting the connection.
     */
    override fun onConnectionRequestReceived(target: String) {
        runOnUiThread{
            views.apply {
                notificationTitle.text = "$target is requesting for connection"
                notificationLayout.isVisible = true
                notificationAcceptBtn.setOnClickListener {
                    webrtcServiceRepository.acceptCAll(target)
                    notificationLayout.isVisible = false
                }
                notificationDeclineBtn.setOnClickListener {
                    notificationLayout.isVisible = false
                }
            }
        }
    }

    /**
     * Called when a connection is successfully established.
     */
    override fun onConnectionConnected() {
        runOnUiThread {
            views.apply {
                requestLayout.isVisible = false
                disconnectBtn.isVisible = true
                disconnectBtn.setOnClickListener {
                    webrtcServiceRepository.endCallIntent()
                    restartUi()
                }
            }
        }
    }

    /**
     * Called when a call end notification is received.
     */
    override fun onCallEndReceived() {
        runOnUiThread {
            restartUi()
        }
    }

    /**
     * Called when a remote video stream is added.
     *
     * @param stream The remote media stream.
     */
    override fun onRemoteStreamAdded(stream: MediaStream) {
        runOnUiThread {
            views.surfaceView.isVisible = true
            stream.videoTracks[0].addSink(views.surfaceView)
        }
    }

    /**
     * Restarts the UI to its initial state, hiding or showing appropriate views.
     */
    private fun restartUi(){
        views.apply {
            disconnectBtn.isVisible=false
            requestLayout.isVisible = true
            notificationLayout.isVisible = false
            surfaceView.isVisible = false
        }
    }

}