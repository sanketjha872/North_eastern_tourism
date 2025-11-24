package com.jhainusa.netourism

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhainusa.netourism.SupaBase.User

// Assuming poppinsmedium.ttf is in res/font
@Composable
fun TravelDetailsCard(user: User?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "Current Trip",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD))
                        .padding(8.dp),
                    tint = Color(0xFF55c1f6)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = "Current Trip", fontSize = 14.sp, color = Color.Gray, fontFamily = poppinsProfile)
                    Text(
                        text = "Sikkim Adventure Trip",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = poppinsProfile
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                InfoColumn(icon = Icons.Outlined.Face, title = "Dates", value = "${user?.itineraryStartDate} - ${user?.itineraryEndDate}")
                InfoColumn(icon = Icons.Outlined.Face, title = "Mode", value = "Flight + Cab")
            }
            Spacer(modifier = Modifier.height(16.dp))
            InfoColumn(icon = Icons.Outlined.Face, title = "Places", value = "Gangtok, Tsomgo Lake, Nathula Pass")
            Spacer(modifier = Modifier.height(16.dp))
            InfoColumn(icon = Icons.Outlined.Face, title = "Trip ID", value  = user?.touristId.toString())
        }
    }
}

@Composable
fun InfoColumn(icon: ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = title, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray, fontFamily = poppinsProfile)
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = poppinsProfile)
        }
    }
}
