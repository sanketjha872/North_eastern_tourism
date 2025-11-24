package com.jhainusa.netourism.MeshNetworking

import android.content.Context

object MeshCore {
    lateinit var chatViewModel: ChatViewModel

    fun init(context: Context) {
        if (!::chatViewModel.isInitialized) {
            chatViewModel = ChatViewModel(context)
            chatViewModel.initNearby()
            chatViewModel.nearby.startAdvertising("UserA")
            chatViewModel.nearby.startDiscovery()
        }
    }
}
