package com.jhainusa.netourism

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class SOSAccessibilityService : AccessibilityService() {

    private val TAG = "SOSAccessibilityService"

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Log.d(TAG, "onKeyEvent: $event")
        val intent = Intent("com.jhainusa.netourism.VOLUME_KEY_EVENT")
        intent.putExtra("key_event", event)
        sendBroadcast(intent)
        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility service connected")
    }
}
