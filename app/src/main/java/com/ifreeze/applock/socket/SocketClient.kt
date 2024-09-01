package com.ifreeze.applock.socket

import android.util.Log
import com.google.gson.Gson
import com.ifreeze.applock.utils.DataModel
import com.ifreeze.applock.utils.DataModelType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception


/**
 * A singleton class that manages a WebSocket connection for real-time communication.
 * Uses Gson for JSON serialization and deserialization.
 *
 * @property gson The Gson instance used for JSON operations.
 */
@Singleton
class SocketClient @Inject constructor(
    private val gson:Gson
){
    private var username:String?=null
    companion object {
        private var webSocket:WebSocketClient?=null
    }

    /**
     * Listener interface for handling incoming messages from the WebSocket.
     */
    var listener: Listener?=null

    /**
     * Initializes the WebSocket connection with the specified username.
     * Sends a sign-in message to the WebSocket server upon connection.
     *
     * @param username The username to authenticate with the WebSocket server.
     */
    fun init(username:String){
        this.username = username

        webSocket = object : WebSocketClient(URI("ws://192.168.1.250:8080")){
            override fun onOpen(handshakedata: ServerHandshake?) {
                // Send a sign-in message when the connection is opened
                sendMessageToSocket(
                    DataModel(
                        type = DataModelType.SignIn,
                        username = username,
                        null,
                        null
                    )
                )
            }

            override fun onMessage(message: String?) {
                // Parse and handle incoming messages
                val model = try {
                    gson.fromJson(message.toString(),DataModel::class.java)
                }catch (e:Exception){
                    null
                }
                Log.d("TAG", "onMessage: $model")
                model?.let {
                    listener?.onNewMessageReceived(it)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                // Attempt to reconnect after a delay when the connection is closed
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    init(username)
                }
            }

            override fun onError(ex: Exception?) {
            }

        }
        webSocket?.connect()
    }


    /**
     * Sends a message to the WebSocket server.
     *
     * @param message The message to send, which will be serialized to JSON.
     */
    fun sendMessageToSocket(message:Any?){
        try {
            webSocket?.send(gson.toJson(message))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    /**
     * Closes the WebSocket connection and cleans up resources.
     */
    fun onDestroy(){
        webSocket?.close()
    }

    /**
     * Interface for listening to new messages received from the WebSocket.
     */
    interface Listener {
        fun onNewMessageReceived(model:DataModel)
    }
}