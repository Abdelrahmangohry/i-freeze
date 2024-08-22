package com.ifreeze.applock.repository

import android.content.Intent
import android.util.Log
import com.ifreeze.applock.socket.SocketClient
import com.google.gson.Gson
import com.ifreeze.applock.utils.DataModel
import com.ifreeze.applock.utils.DataModelType
import com.ifreeze.applock.webrtc.MyPeerObserver
import com.ifreeze.applock.webrtc.WebrtcClient
import org.webrtc.*
import javax.inject.Inject


class MainRepository @Inject constructor(
    private val socketClient: SocketClient,
    private val webrtcClient: WebrtcClient,
    private val gson: Gson
) : SocketClient.Listener, WebrtcClient.Listener {

    private lateinit var username: String
    private lateinit var target: String
    private lateinit var surfaceView: SurfaceViewRenderer
    var listener: Listener? = null

    fun init(username: String, surfaceView: SurfaceViewRenderer) {
        this.username = username
        this.surfaceView = surfaceView
        initSocket()
        initWebrtcClient()

    }

    private fun initSocket() {
        socketClient.listener = this
        socketClient.init(username)
    }

    fun setPermissionIntentToWebrtcClient(intent:Intent){
        webrtcClient.setPermissionIntent(intent)
    }

    fun sendScreenShareConnection(target: String){
        socketClient.sendMessageToSocket(
            DataModel(
                type = DataModelType.StartStreaming,
                username = username,
                target = target,
                null
            )
        )
    }

    fun startScreenCapturing(surfaceView: SurfaceViewRenderer){
        webrtcClient.startScreenCapturing(surfaceView)
    }

    fun startCall(target: String){
        webrtcClient.call(target)
    }

    fun sendCallEndedToOtherPeer(){
        socketClient.sendMessageToSocket(
            DataModel(
                type = DataModelType.EndCall,
                username = username,
                target = target,
                null
            )
        )
    }

    fun restartRepository(){
        webrtcClient.restart()
    }

    fun onDestroy(){
        socketClient.onDestroy()
        webrtcClient.closeConnection()
    }

    private fun initWebrtcClient() {
        webrtcClient.listener = this
        webrtcClient.initializeWebrtcClient(username, surfaceView,
            object : MyPeerObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    p0?.let { webrtcClient.sendIceCandidate(it, target) }
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    super.onConnectionChange(newState)
                    Log.d("TAG", "onConnectionChange: $newState")
                    if (newState == PeerConnection.PeerConnectionState.CONNECTED){
                        listener?.onConnectionConnected()
                    }
                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
                    Log.d("TAG", "onAddStream: $p0")
                    p0?.let { listener?.onRemoteStreamAdded(it) }
                }
            })
    }

    override fun onNewMessageReceived(model: DataModel) {
        when (model.type) {
            DataModelType.StartStreaming -> {
                this.target = model.username
                //notify ui, conneciton request is being made, so show it
                listener?.onConnectionRequestReceived(model.username)
            }
            DataModelType.EndCall -> {
                //notify ui call is ended
                listener?.onCallEndReceived()
            }
            DataModelType.Offer -> {
                webrtcClient.onRemoteSessionReceived(
                    SessionDescription(
                        SessionDescription.Type.OFFER, model.data
                            .toString()
                    )
                )
                this.target = model.username
                webrtcClient.answer(target)
            }
            DataModelType.Answer -> {
                webrtcClient.onRemoteSessionReceived(
                    SessionDescription(SessionDescription.Type.ANSWER, model.data.toString())
                )

            }
            DataModelType.IceCandidates -> {
                val candidate = try {
                    gson.fromJson(model.data.toString(), IceCandidate::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                candidate?.let {
                    webrtcClient.addIceCandidate(it)
                }
            }
            else -> Unit
        }
    }

    override fun onTransferEventToSocket(data: DataModel) {
        socketClient.sendMessageToSocket(data)
    }

    interface Listener {
        fun onConnectionRequestReceived(target: String)
        fun onConnectionConnected()
        fun onCallEndReceived()
        fun onRemoteStreamAdded(stream: MediaStream)
    }
}