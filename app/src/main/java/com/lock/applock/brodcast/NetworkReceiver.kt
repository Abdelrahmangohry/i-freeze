package com.lock.applock.brodcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast

class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connMgr: ConnectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.getActiveNetworkInfo()
        if (networkInfo != null && networkInfo.isConnected()){
            Toast.makeText(context,"have a good time to play what you need  " , Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context,"your network is too  slow " , Toast.LENGTH_LONG).show()
        }

    }

}
