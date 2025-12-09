package com.jhainusa.netourism

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jhainusa.netourism.Gemini.GeminiViewModel
import com.jhainusa.netourism.Map.LocationRepository
import com.jhainusa.netourism.MeshNetworking.ChatMessage
import com.jhainusa.netourism.MeshNetworking.ChatViewModel
import com.jhainusa.netourism.SupaBase.Alert
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import com.jhainusa.netourism.UserPreferences.getUserPrefs
import com.jhainusa.netourism.ui.theme.blue

val poppinsSOS = FontFamily(
    Font(R.font.manrope_medium)
)

private const val TAG = "SOSScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    navController: NavController, viewModel: ChatViewModel,
    context: Context = LocalContext.current
) {
    val currentLocation by LocationRepository.currentPlaceName.collectAsState()
    val location by LocationRepository.lastKnownLocation.collectAsState()
    var message by remember {
        mutableStateOf(
            "I am in an emergency situation and need help. " +
                    "This is my current location. Please contact authorities immediately."
        )
    }
    val user = context.getUserPrefs().getUser()
    var sosSent by remember { mutableStateOf(false) }

    val geminiViewModel: GeminiViewModel = viewModel()
    val authority by geminiViewModel.authority.collectAsState()

    remember { UserPreferencesManager(context) }

    DisposableEffect(key1 = true) {
        Log.d(TAG, "SOSScreen: launching advertising and discovery")
        viewModel.initNearby()
        viewModel.nearby.startAdvertising("SOS")
        viewModel.nearby.startDiscovery()

        onDispose {
            Log.d(TAG, "SOSScreen: cleaning up nearby connections")
            viewModel.cleanup()
        }
    }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            spokenText?.let {
                message = it
                geminiViewModel.getAuthority(it)
            }
        }
    }
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // English language
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
        putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
        putExtra(RecognizerIntent.EXTRA_PROMPT, stringResource(R.string.ask_anything))
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Helps improve accuracy
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.emergency_sos),
                        fontFamily = poppinsSOS,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.close)
                        )
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
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = stringResource(R.string.press_and_hold_to_send_alert),
                fontFamily = poppinsSOS,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(20.dp))
            val alert = Alert(
                tourist_id = user?.touristId.toString(),
                alert_type = authority,
                severity = "medium",
                description = message,
                location_name = currentLocation.toString(),
                latitude = location?.latitude ?: 00.0000,
                longitude = location?.longitude ?: 00.0000
            )
            SOSButton(onTap = {
                if (message.isNotBlank()) {
                    geminiViewModel.getAuthority(message)
                    viewModel.sendMessage(alert)
                    sosSent = true
                }
            }, onLongPress = {
                speechRecognizerLauncher.launch(intent)
            })

            Spacer(modifier = Modifier.height(15.dp))
            if (sosSent) {
                TextButton(onClick = {
                    val safeMessage = "I am safe."
                    val safeAlert = alert.copy(description = safeMessage)
                    viewModel.sendMessage(safeAlert)
                    sosSent = false
                }) {
                    Text(
                        "I am safe",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontFamily = poppinsSOS,
                        modifier = Modifier.background(Color(0xFFFFF5F5), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))
            EmergencyMessageCard(message, onMessageChange = { 
                message = it 
                geminiViewModel.getAuthority(it)
            })

            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                reverseLayout = true
            ) {
                items(viewModel.messages.reversed()) { msg ->
                    MessageBubble(msg,authority)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text(
                    stringResource(R.string.cancel),
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontFamily = poppinsSOS
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SOSButton(onLongPress: () -> Unit, onTap: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .combinedClickable(
                onClick = { onTap() },
                onLongClick = { onLongPress() },
                indication = LocalIndication.current,        // Ripple effect
                interactionSource = remember { MutableInteractionSource() }
            )

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
                .size(180.dp)
                .clip(CircleShape)
                .background(innerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.sos),
                color = Color.White,
                fontSize = 40.sp,
                fontFamily = poppinsSOS,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage,authority : String) {
    if (msg.isMine) Alignment.End else Alignment.Start
    val color = if (msg.isMine) Color(0xFFD0F0FF) else Color(0xFFEDEDED)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = color,
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Text(
                text = msg.text + " ($authority)",
                modifier = Modifier.padding(10.dp),
                fontFamily = poppinsSOS
            )
        }
    }
}

@Composable
fun EmergencyMessageCard(message: String, onMessageChange: (String) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var editedMessage by remember { mutableStateOf(message) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF5F5), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.your_emergency_message),
                fontFamily = poppinsSOS,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            IconButton(onClick = { isEditing = !isEditing }) {
                Icon(
                    if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Close" else stringResource(id = R.string.edit),
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            TextField(
                value = editedMessage,
                onValueChange = { editedMessage = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    fontFamily = poppinsSOS,
                    fontSize = 14.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Red,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                onMessageChange(editedMessage)
                isEditing = false
            }) {
                Text(
                    text = "save",
                    color = Color.Red,
                    fontFamily = poppinsSOS,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = message,
                fontFamily = poppinsSOS,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}
