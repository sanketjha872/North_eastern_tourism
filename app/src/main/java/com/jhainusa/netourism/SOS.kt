package com.jhainusa.netourism

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jhainusa.netourism.MeshNetworking.ChatMessage
import com.jhainusa.netourism.MeshNetworking.ChatViewModel
import com.jhainusa.netourism.ui.theme.NETourismTheme

val poppinsSOS = FontFamily(
    Font(R.font.manrope_medium)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(navController: NavController,viewModel: ChatViewModel) {

    var emergencyMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency SOS", fontFamily = poppinsSOS, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Press and Hold to Send Alert",
                fontFamily = poppinsSOS,
                fontSize = 18.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            //SOSButton()

            Spacer(modifier = Modifier.weight(1f))

//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Your emergency message:",
//                    fontFamily = poppinsSOS,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium
//                )
//                TextButton(onClick = { /* TODO: Handle Edit */ }) {
//                    Icon(
//                        Icons.Default.Edit,
//                        contentDescription = "Edit",
//                        tint = Color.Red,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("Edit", color = Color.Red, fontFamily = poppinsSOS)
//                }
//            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true
            ) {
                items(viewModel.messages.reversed()) { msg ->
                    MessageBubble(msg)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = emergencyMessage,
                onValueChange = { emergencyMessage = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your issue", color = Color.Black) },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontFamily = poppinsLogin,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                ),
                singleLine = true
            )

            TextButton(onClick = {
                if (emergencyMessage.isNotBlank()) {
                    viewModel.sendMessage(emergencyMessage)
                    emergencyMessage = ""
                }
            }) {
                Text("SEND", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = poppinsSOS)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Cancel", color = Color.Gray, fontSize = 14.sp, fontFamily = poppinsSOS)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SOSButton() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(240.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        // TODO: Handle long press to send alert
                    }
                )
            }
            .clip(CircleShape)

    ) {
        val outerColor = Color(0xFFFF8A80) // Lighter red
        val innerColor = Color(0xFFFF5252) // Main red

        // Outer circle for press effect
        if (isPressed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(outerColor.copy(alpha = 0.5f))
            )
        } else {
             Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(outerColor.copy(alpha = 0.2f))
            )
        }


        // The main SOS button
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(innerColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // No ripple effect
                    onClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "SOS",
                color = Color.White,
                fontSize = 60.sp,
                fontFamily = poppinsSOS,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
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
