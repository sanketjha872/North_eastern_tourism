package com.jhainusa.netourism

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jhainusa.netourism.SupaBase.ReportViewModel
import com.jhainusa.netourism.SupaBase.ReportViewModelFactory
import com.jhainusa.netourism.UserPreferences.UserPreferencesManager
import com.jhainusa.netourism.ui.theme.NETourismTheme

// Assuming poppinsmedium.ttf is in res/font
val poppinsLogin = FontFamily(
    Font(R.font.manrope_medium)
)

@Composable
fun SecureLoginScreen(navController: NavController, context: Context = LocalContext.current,
                      viewModel: ReportViewModel) {
    var uniqueId by remember { mutableStateOf(TextFieldValue("")) }
    val prefsManager = remember { UserPreferencesManager(context) }

    var openScanner by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.sceneimage),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.8f

        )
        Column(
            modifier = Modifier
                .imePadding()
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Your Secure Guide to the Northeast",
                fontFamily = poppinsLogin,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Verifying Your Identity.",
                    fontFamily = poppinsLogin,
                    fontSize = 16.sp,
                    color = Color.White,
                )

            Spacer(modifier = Modifier.height(48.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Unique Blockchain ID",
                    fontFamily = poppinsLogin,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uniqueId,
                    onValueChange = { uniqueId = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your unique ID", color = Color.White) },
                    leadingIcon = {
                        IconButton(
                            onClick = { openScanner = true}
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.qr_code_svgrepo_com),
                                contentDescription = "QR Code Icon",
                                tint = Color.White
                            )
                        }
                    },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontFamily = poppinsLogin,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        disabledContainerColor = Color.White.copy(alpha = 0.1f),
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (uniqueId != null) {
                    viewModel.loadReportsFromNetwork(uniqueId.text)
                }
                    navController.navigate("AllScreenNav")
                          },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF55c1f6),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Secure Login",
                    fontFamily = poppinsLogin,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

        }
        if (openScanner) {
            QRScanner(
                onScan = { value ->
                    uniqueId = TextFieldValue(value)
                    openScanner = false   // close camera after scan
                },
                onClose = { openScanner = false }
            )
        }
    }
}
