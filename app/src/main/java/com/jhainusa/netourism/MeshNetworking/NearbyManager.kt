package com.jhainusa.netourism.MeshNetworking

import android.content.Context
import android.util.Log
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

private const val TAG = "NearbyManager"

class NearbyManager(
    private val context: Context,
    private val onReceive: (String) -> Unit,
    private val onConnected: () -> Unit
) {

    private val connectionsClient = Nearby.getConnectionsClient(context)
    private var currentEndpointId: String? = null

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d(TAG, "onPayloadReceived: from $endpointId")
            payload.asBytes()?.let {
                onReceive(String(it))
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    private val connLifecycle = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.d(TAG, "onConnectionInitiated: accepting connection to $endpointId")
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.statusCode == ConnectionsStatusCodes.STATUS_OK) {
                Log.d(TAG, "onConnectionResult: connected to $endpointId")
                currentEndpointId = endpointId
                onConnected()
            } else {
                Log.d(TAG, "onConnectionResult: failed to connect to $endpointId")
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "onDisconnected: from $endpointId")
            currentEndpointId = null
        }
    }

    fun startAdvertising(name: String) {
        Log.d(TAG, "startAdvertising: with name $name")
        connectionsClient.startAdvertising(
            name,
            "chat_service",
            connLifecycle,
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        )
    }

    fun startDiscovery() {
        Log.d(TAG, "startDiscovery")
        connectionsClient.startDiscovery(
            "chat_service",
            object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(id: String, info: DiscoveredEndpointInfo) {
                    Log.d(TAG, "onEndpointFound: $id")
                    connectionsClient.requestConnection("User", id, connLifecycle)
                }

                override fun onEndpointLost(id: String) {
                    Log.d(TAG, "onEndpointLost: $id")
                }
            },
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        )
    }

    fun sendMessage(msg: String) {
        Log.d(TAG, "sendMessage: to $currentEndpointId")
        currentEndpointId?.let {
            connectionsClient.sendPayload(it, Payload.fromBytes(msg.toByteArray()))
        }
    }

    fun stopAdvertising() {
        Log.d(TAG, "stopAdvertising")
        connectionsClient.stopAdvertising()
    }

    fun stopDiscovery() {
        Log.d(TAG, "stopDiscovery")
        connectionsClient.stopDiscovery()
    }

    fun disconnectFromAllEndpoints() {
        Log.d(TAG, "disconnectFromAllEndpoints")
        connectionsClient.stopAllEndpoints()
    }
}
