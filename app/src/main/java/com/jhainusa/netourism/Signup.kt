package com.jhainusa.netourism

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jhainusa.netourism.ui.theme.NETourismTheme

// Using the same font as Login
val poppinsSignup = FontFamily(
    Font(R.font.manrope_medium)
)

@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A5A53),
                        Color(0xFF2C3E36)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            Text(
                text = "Create a New ID",
                fontFamily = poppinsSignup,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Join us to explore the Northeast securely.",
                fontFamily = poppinsSignup,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Input Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Full Name", fontFamily = poppinsSignup, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your full name", color = Color.White.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = signupTextFieldColors(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Email", fontFamily = poppinsSignup, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your email", color = Color.White.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = signupTextFieldColors(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Password", fontFamily = poppinsSignup, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Create a strong password", color = Color.White.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(12.dp),
                    colors = signupTextFieldColors(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* TODO: Handle signup */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2CFF8D),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Generate Unique ID",
                    fontFamily = poppinsSignup,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            ClickableText(
                text = AnnotatedString("Already have an ID? Login"),
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(bottom = 32.dp),
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = poppinsSignup,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
private fun signupTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White.copy(alpha = 0.1f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
    disabledContainerColor = Color.White.copy(alpha = 0.1f),
    cursorColor = Color.White,
    focusedBorderColor = Color.White.copy(alpha = 0.3f),
    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
)

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    NETourismTheme {
        SignUpScreen(rememberNavController())
    }
}
