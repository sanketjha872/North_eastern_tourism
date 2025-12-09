package com.jhainusa.netourism

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.KeyEvent

class SOSService : Service() {

    private var volumeUpPressCount = 0
    private val VOLUME_UP_PRESS_THRESHOLD = 3 // Press volume up 3 times to trigger SOS
    private val TAG = "SOSService"

    private val keyEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.jhainusa.netourism.VOLUME_KEY_EVENT") {
                Log.d(TAG, "Volume key event broadcast received")
                val event = intent.getParcelableExtra<KeyEvent>("key_event")
                if (event != null) {
                    handleKeyEvent(event)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "SOSService started")
        val filter = IntentFilter("com.jhainusa.netourism.VOLUME_KEY_EVENT")
        registerReceiver(keyEventReceiver, filter, RECEIVER_NOT_EXPORTED)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SOSService destroyed")
        unregisterReceiver(keyEventReceiver)
    }

    fun handleKeyEvent(event: KeyEvent) {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                volumeUpPressCount++
                Log.d(TAG, "Volume Up Pressed. Count: $volumeUpPressCount")
                if (volumeUpPressCount >= VOLUME_UP_PRESS_THRESHOLD) {
                    Log.d(TAG, "SOS sequence triggered")
                    startSOSSequence()
                    volumeUpPressCount = 0 // Reset count
                }
            } else {
                Log.d(TAG, "Another key pressed, resetting count.")
                volumeUpPressCount = 0 // Reset if another key is pressed
            }
        }
    }

    private fun startSOSSequence() {
        Log.d(TAG, "Starting SOS sequence. Launching MainActivity.")
         val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("start_sos", true)
            putExtra("sos_action", "start_recording")
        }
        startActivity(intent)
    }
}
