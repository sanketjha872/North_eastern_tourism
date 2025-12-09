package com.jhainusa.netourism.MeshNetworking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.jhainusa.netourism.SupaBase.Alert
import com.jhainusa.netourism.SupaBase.ReportViewModel
import com.jhainusa.netourism.SupaBase.ReportViewModelFactory
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val TAG = "ChatViewModel"

class ChatViewModel(private val context: Context, private val prefsManager: UserPreferencesManager) : ViewModel() {

    var messages = mutableStateListOf<ChatMessage>()
        private set

    lateinit var nearby: NearbyManager

    private val json = Json { ignoreUnknownKeys = true }

    private val reportViewModel: ReportViewModel = ReportViewModelFactory(prefsManager,context
    ).create(ReportViewModel::class.java)

    fun initNearby() {
        Log.d(TAG, "initNearby")
        nearby = NearbyManager(
            context,
            onReceive = {
                receiveMessage(it)
            },
            onConnected = { addSystem("Connected!") }
        )
    }

    fun cleanup() {
        Log.d(TAG, "cleanup")
        nearby.stopAdvertising()
        nearby.stopDiscovery()
        nearby.disconnectFromAllEndpoints()
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    fun addSystem(text: String) {
        Log.d(TAG, "addSystem: $text")
        messages.add(ChatMessage(text, false, system = true))
    }

    fun sendMessage(alert: Alert) {
        val alertJson = json.encodeToString(alert)
        messages.add(ChatMessage(alert.description ?: "SOS", true))
        if (isNetworkAvailable()) {
            Log.d(TAG, "sendMessage: network available, sending to server")

            reportViewModel.uploadAlertToServer(alert)
        } else {
            Log.d(TAG, "sendMessage: network not available, sending to nearby")
            nearby.sendMessage(alertJson)
        }
    }

    fun receiveMessage(text: String) {
        Log.d(TAG, "receiveMessage: $text")
        val alert = json.decodeFromString<Alert>(text)
        messages.add(ChatMessage(alert.description ?: "SOS", false))
        if (isNetworkAvailable()) {
            Log.d(TAG, "receiveMessage: network available, forwarding to server")
            reportViewModel.uploadAlertToServer(alert)
        }
    }
}

data class ChatMessage(
    val text: String,
    val isMine: Boolean,
    val system: Boolean = false
)

@Composable
fun ChatScreen(vm: ChatViewModel) {

    var text by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(vm.messages.reversed()) { msg ->
                MessageBubble(msg)
            }
        }

        Row {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (text.isNotBlank()) {
//                        vm.sendMessage(text)
                        text = ""
                    }
                }
            ) { Text("Send") }
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
    if (msg.isMine) Alignment.End else Alignment.Start
    val color = if (msg.isMine) Color(0xFFD0F0FF) else Color(0xFFEDEDED)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            msg.text,
            Modifier
                .background(color, RoundedCornerShape(12.dp))
                .padding(10.dp)
                .widthIn(max = 260.dp)
        )
    }
}
