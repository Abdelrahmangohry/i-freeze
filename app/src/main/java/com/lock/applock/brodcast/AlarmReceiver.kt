package com.lock.applock.brodcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("islam", "onReceive : start appmointoring ")
//        p0?.startActivity(intent)
//        val serviceIntent = Intent(p0, AppMonitoringService::class.java)
//        p0?.startService(serviceIntent)
    }
}