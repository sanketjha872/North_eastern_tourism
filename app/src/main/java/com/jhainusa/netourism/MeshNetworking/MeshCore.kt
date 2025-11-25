package com.jhainusa.netourism.MeshNetworking

import android.content.Context
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager

object MeshCore {
    lateinit var chatViewModel: ChatViewModel

    fun init(context: Context,preferencesManager: UserPreferencesManager) {
        if (!::chatViewModel.isInitialized) {
            chatViewModel = ChatViewModel(context,preferencesManager)
            chatViewModel.initNearby()
            chatViewModel.nearby.startAdvertising("UserA")
            chatViewModel.nearby.startDiscovery()
        }
    }
}
