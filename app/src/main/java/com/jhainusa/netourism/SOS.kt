package com.jhainusa.netourism

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jhainusa.netourism.MeshNetworking.ChatMessage
import com.jhainusa.netourism.MeshNetworking.ChatViewModel
import com.jhainusa.netourism.SupaBase.Alert
import com.jhainusa.netourism.SupaBase.ReportViewModel
import com.jhainusa.netourism.SupaBase.ReportViewModelFactory
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import com.jhainusa.netourism.UserPreferences.getUserPrefs
import com.jhainusa.netourism.ui.theme.NETourismTheme

val poppinsSOS = FontFamily(
    Font(R.font.manrope_medium)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    navController: NavController, viewModel: ChatViewModel,
    context: Context = LocalContext.current
) {

    var message by remember { mutableStateOf(
        "I am in an emergency situation and need help. " +
                "This is my current location. Please contact authorities immediately."
    )}
    val user = context.getUserPrefs().getUser()

    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    val prefsManager = remember { UserPreferencesManager(context) }

    val reportViewModel: ReportViewModel = viewModel(
        factory = ReportViewModelFactory(prefsManager)
    )

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
            spokenText?.let {
                userInput = TextFieldValue(it)
            }
        }
    }
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN") // Correct format for Hindi
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
        putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
        putExtra(RecognizerIntent.EXTRA_PROMPT, stringResource(R.string.ask_anything))
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true) // Helps improve accuracy
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.emergency_sos), fontFamily = poppinsSOS, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.close))
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
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.press_and_hold_to_send_alert),
                fontFamily = poppinsSOS,
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(20.dp))
            SOSButton(onTap = {}, onLongPress = { speechRecognizerLauncher.launch(intent)
            })

            Spacer(modifier = Modifier.height(32.dp))
            EmergencyMessageCard(message, onMessageChange = {message = it})

            Spacer(modifier = Modifier.weight(1f))

//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = stringResource(R.string.your_emergency_message),
//                    fontFamily = poppinsSOS,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium
//                )
//                TextButton(onClick = { /* TODO: Handle Edit */ }) {
//                    Icon(
//                        Icons.Default.Edit,
//                        contentDescription = stringResource(R.string.edit),
//                        tint = Color.Red,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text(stringResource(R.string.edit), color = Color.Red, fontFamily = poppinsSOS)
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

//            OutlinedTextField(
//                value = userInput,
//                onValueChange = { userInput = it },
//                modifier = Modifier.fillMaxWidth(),
//                placeholder = { Text(stringResource(R.string.enter_your_issue), color = Color.Black) },
//                textStyle = TextStyle(
//                    color = Color.Black,
//                    fontFamily = poppinsLogin,
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 16.sp
//                ),
//                shape = RoundedCornerShape(12.dp),
//                colors = OutlinedTextFieldDefaults.colors(),
//                singleLine = true
//            )
            val alert = Alert(
                tourist_id = user?.touristId.toString(),
                alert_type = "Ambulance",
                severity = "medium",
                description = message,
                location_name = "North Eastern",
                latitude = 21.5562,
                longitude = 78.1010
            )

            TextButton(onClick = {
                if (message.isNotBlank()) {
                    if (viewModel.isNetworkAvailable()) {
                        reportViewModel.uploadAlertToServer(
                            alert = alert
                        )
                    } else {
                        viewModel.sendMessage("${message} \n $alert")
                    }
                    message = "MESSAGE SENT SUCCESSFULLY"
                }
            }) {
                Text(stringResource(R.string.send), color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = poppinsSOS)
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.cancel), color = Color.Gray, fontSize = 14.sp, fontFamily = poppinsSOS)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SOSButton(onLongPress : () -> Unit ,onTap : () -> Unit ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(240.dp)
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
                .size(200.dp)
                .clip(CircleShape)
                .background(innerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.sos),
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
    val align = if (msg.isMine) Alignment.End else Alignment.Start
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
@Composable
fun EmergencyMessageCard(message : String, onMessageChange : (String) -> Unit ) {
    var isEditing by remember { mutableStateOf(false) }

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().imePadding()
        ) {
            Text(
                text = stringResource(R.string.your_emergency_message),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontFamily = poppinsSOS
            )
            TextButton(onClick = {isEditing = !isEditing }) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if(!isEditing) stringResource(R.string.edit) else "Save" , color = Color.Red, fontFamily = poppinsSOS)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            TextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F1F1),
                    unfocusedContainerColor = Color(0xFFF1F1F1),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                textStyle = TextStyle(
                    fontFamily = poppinsSOS,
                    color = Color.Black,
                )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF1F1F1))
                    .padding(16.dp)
                    .clickable(onClick ={isEditing = !isEditing} )
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontFamily = poppinsSOS
                )
            }
        }
    }
}


