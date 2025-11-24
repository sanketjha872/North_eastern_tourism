package com.jhainusa.netourism.MeshNetworking

import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

class ChatViewModel(private val context: Context) : ViewModel() {

    var messages = mutableStateListOf<ChatMessage>()
        private set

    lateinit var nearby: NearbyManager

    fun initNearby() {
        nearby = NearbyManager(
            context,
            onReceive = { receiveMessage(it) },
            onConnected = { addSystem("Connected!") }
        )
    }

    fun addSystem(text: String) {
        messages.add(ChatMessage(text, false, system = true))
    }

    fun sendMessage(text: String) {
        messages.add(ChatMessage(text, true))
        nearby.sendMessage(text)
    }

    fun receiveMessage(text: String) {
        messages.add(ChatMessage(text, false))
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
                        vm.sendMessage(text)
                        text = ""
                    }
                }
            ) { Text("Send") }
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
    val align  = if (msg.isMine) Alignment.End else Alignment.Start
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



