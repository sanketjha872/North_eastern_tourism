package com.jhainusa.netourism

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.jhainusa.netourism.UserPreferences.getUserPrefs
import com.jhainusa.netourism.ui.theme.NETourismTheme

// Assuming poppinsmedium.ttf is in res/font
val poppinsProfile = FontFamily(
    Font(R.font.manrope_medium)
)

data class ProfileEmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    @DrawableRes val avatarResId: Int
)

// Sample Data - Replace with your actual drawables
val sampleUserProfile = ProfileEmergencyContact(
    id = "user1",
    name = "Hari Shukla",
    phoneNumber = "+91 815154515",
    avatarResId = R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash // Replace with actual user avatar
)

val sampleEmergencyContactList = listOf(
    ProfileEmergencyContact(
        id = "ec1",
        name = "Kumar Vishwas",
        phoneNumber = "+91 9158716543",
        avatarResId = R.drawable.iftekhar_nibir_xkcfg_wjx8m_unsplash // Replace
    )
    // Add more contacts if needed
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController,context : Context = LocalContext.current) {
    val user = context.getUserPrefs().getUser()

    Scaffold(
        containerColor = Color.White, // White background for the screen
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Profile", 
                        fontFamily = poppinsProfile, 
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black // Black text
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black) // Black icon
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, // White TopAppBar background
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White) // Ensure column background is white
                .verticalScroll(rememberScrollState()) // Make content scrollable
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            UserImage(
                user?.image_url,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)            )
            // Profile Header
//            Image(
//                painter = painterResource(R.drawable._60_f_222851624_jfomgbjxwri5awgdpgxksabmnzcqo9rn),
//                contentDescription = "User Avatar",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(120.dp)
//                    .clip(CircleShape)
//            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user?.name.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = poppinsProfile,
                color = Color.Black
            )
            Text(
                text = "Personal Safety", // Subtitle from image
                fontSize = 16.sp,
                fontFamily = poppinsProfile,
                color = Color.DarkGray // Slightly lighter black for subtitle
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Personal Details Section
            SectionTitle(title = "Personal Details")
            Spacer(modifier = Modifier.height(12.dp))
            InfoCard(
                icon = Icons.Filled.Phone,
                title = "Phone Number",
                content = sampleUserProfile.phoneNumber
            )
            Spacer(modifier = Modifier.height(12.dp))
            InfoCard(
                icon = Icons.Filled.LocationOn,
                title = "Address",
                content = user?.country.toString() // Sample address
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Emergency Contacts Section
            SectionTitle(title = "Emergency Contacts")
            Spacer(modifier = Modifier.height(12.dp))
            sampleEmergencyContactList.forEach { contact ->
                EmergencyContactListItem(contact = contact)
                Spacer(modifier = Modifier.height(12.dp))
            }
            TravelDetailsCard(user)
            // Add more UI for adding/editing emergency contacts if needed
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = poppinsProfile,
        color = Color.Black,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun InfoCard(icon: ImageVector, title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), // Light grey card background
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // No shadow on light background
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.DarkGray, // Dark grey icon
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontFamily = poppinsProfile,
                    color = Color.Gray // Medium grey for title in card
                )
                Text(
                    text = content,
                    fontSize = 16.sp,
                    fontFamily = poppinsProfile,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black // Black for main content
                )
            }
        }
    }
}

@Composable
fun EmergencyContactListItem(contact: ProfileEmergencyContact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)), // Light grey card background
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = contact.avatarResId),
                contentDescription = "${contact.name} Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontSize = 16.sp,
                    fontFamily = poppinsProfile,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    fontFamily = poppinsProfile,
                    color = Color.DarkGray
                )
            }
            // Add a call icon or edit icon if needed
            // Icon(Icons.Filled.Call, contentDescription = "Call contact", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    NETourismTheme {
        ProfileScreen(navController = rememberNavController())
    }
}
@Composable
fun UserImage(imageUrl: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "User Image",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

// Notes:
// 1. Replace R.drawable.profile_avatar_placeholder and R.drawable.emergency_contact_avatar_placeholder
//    with actual drawable resources in your res/drawable folder.
// 2. Ensure poppinsmedium.ttf is in your res/font folder.
// 3. The cards (InfoCard, EmergencyContactListItem) are given a very light grey background (0xFFF5F5F5)
//    to distinguish them slightly from the pure white screen background. You can change this to Color.White
//    and add a border if you prefer. 
