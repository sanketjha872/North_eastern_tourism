package com.jhainusa.netourism.SupaBase

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jhainusa.netourism.R
import com.jhainusa.netourism.UserImage
import com.jhainusa.netourism.UserPreferences.getUserPrefs

val poppinsFamily = FontFamily(
    Font(R.font.manrope_medium),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouristProfileScreen(onBack: () -> Unit, onEdit: () -> Unit,
                         navController: NavController,context : Context = LocalContext.current) {
    val user = context.getUserPrefs().getUser()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontFamily = poppinsFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        contentWindowInsets = WindowInsets(0.dp,0.dp,0.dp,0.dp),
        containerColor = Color(0xFFFBFBF9)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item{ ProfileHeader(user) }
            item{ QrCodeCard()}
            item{ BlockchainIdSection(user?.blockchainId)}
            item{ UserDetailsCard(user) }
        }
    }
}

@Composable
fun ProfileHeader(user: User?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        UserImage(
            user?.image_url,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user?.name ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = poppinsFamily
            )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Tourist ID: ${user?.touristId}",
            fontSize = 14.sp,
            color = Color.Gray,
            fontFamily = poppinsFamily
        )
    }
}

@Composable
fun QrCodeCard() {
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.qr_code_for_mobile_english_wikipedia), // Replace with actual QR code
                    contentDescription = "QR Code",
                    modifier = Modifier.size(150.dp)
                )
    }
}

@Composable
fun BlockchainIdSection(blockchainId: String?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.link_1_svgrepo_com),
                contentDescription = "Blockchain ID",
                tint = Color.Gray,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Blockchain ID", fontWeight = FontWeight.Black, fontFamily = poppinsFamily)

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = blockchainId?.let { "${it.take(4)}...${it.takeLast(4)}" } ?: "N/A",
                fontSize = 14.sp,
                color = Color.Gray,
                fontFamily = poppinsFamily
            )
            IconButton(onClick = { /* Copy to clipboard */ }) {
                Icon(
                     painter = painterResource(R.drawable.copy_svgrepo_com),
                    contentDescription = "Copy Blockchain ID",
                    tint = Color(0xFF007AFF)
                )
            }
        }
    }
}

@Composable
fun UserDetailsCard(user: User?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow("Itinerary Dates", "${user?.itineraryStartDate ?: "N/A"} - ${user?.itineraryEndDate ?: "N/A"}")
            Divider(modifier = Modifier.padding(vertical = 8.dp).background(Color.LightGray))
            InfoRow("Country", user?.country ?: "N/A")
            Divider(modifier = Modifier.padding(vertical = 8.dp).background(Color.LightGray))
            InfoRow("State", user?.state ?: "N/A")
            Divider(modifier = Modifier.padding(vertical = 8.dp).background(Color.LightGray))
            InfoRow("Email Address", user?.email ?: "")
            Divider(modifier = Modifier.padding(vertical = 8.dp).background(Color.LightGray))
            InfoRow("Document Type", user?.documentType ?: "")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp, fontFamily = poppinsFamily)
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, fontFamily = poppinsFamily)
    }
}


@Preview(showBackground = true)
@Composable
fun TouristProfileScreenPreview() {
    val dummyUser = User(
        id = "1",
        touristId = "987-654-3210",
        blockchainId = "0x4a...f3e1",
        email = "alex.chen@email.com",
        name = "Alexandra Chen",
        documentType = "Passport",
        documentNumber = "L899...9",
        country = "India",
        state = "Maharashtra",
        itineraryStartDate = "15 Aug 2024",
        itineraryEndDate = "30 Aug 2024",
        image_url = "https://example.com/image.png"
    )
}
