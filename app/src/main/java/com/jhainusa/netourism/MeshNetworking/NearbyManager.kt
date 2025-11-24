package com.jhainusa.netourism.MeshNetworking

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy

class NearbyManager(
    private val context: Context,
    private val onReceive: (String) -> Unit,
    private val onConnected: () -> Unit
) {

    private val connectionsClient = Nearby.getConnectionsClient(context)
    private var currentEndpointId: String? = null

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let {
                onReceive(String(it))
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    private val connLifecycle = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                currentEndpointId = endpointId
                onConnected()
            }
        }

        override fun onDisconnected(endpointId: String) {
            currentEndpointId = null
        }
    }

    fun startAdvertising(name: String) {
        connectionsClient.startAdvertising(
            name,
            "chat_service",
            connLifecycle,
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        )
    }

    fun startDiscovery() {
        connectionsClient.startDiscovery(
            "chat_service",
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(id: String, info: DiscoveredEndpointInfo) {
                    connectionsClient.requestConnection("User", id, connLifecycle)
                }

                override fun onEndpointLost(id: String) {}
            },
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        )
    }

    fun sendMessage(msg: String) {
        currentEndpointId?.let {
            connectionsClient.sendPayload(it, Payload.fromBytes(msg.toByteArray()))
        }
    }
}
