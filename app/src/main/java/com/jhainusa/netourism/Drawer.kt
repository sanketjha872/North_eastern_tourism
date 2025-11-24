package com.jhainusa.netourism

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jhainusa.netourism.UserPreferences.getUserPrefs

@Preview
@Composable
fun DrawerContent(context : Context = LocalContext.current) {
     val user = context.getUserPrefs().getUser()
    Column(modifier = Modifier.background(Color(0xFFFFFFFE))) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.padding(end = 16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(painter = painterResource(R.drawable.help_alt_svgrepo_com), contentDescription = "Help")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // User Info
        LazyColumn(

        ) {
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 18.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF8AB1F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "S",
                                fontSize = 24.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = poppinsFontFamily
                            )
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(
                                "${user?.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = poppinsFontFamily
                            )
                            Text(
                                "${user?.email}",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                fontFamily = poppinsFontFamily
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
//                TextButton(onClick = { /*TODO*/ }) {
//                    Text("Manage", color = Color(0xFF55c1f6),fontFamily = poppinsFontFamily)
//                }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                // QR Code Card
                Card(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "BlockChain ID - ${user?.blockchainId}",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp),
                                fontFamily = poppinsFontFamily
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.qr_code_for_mobile_english_wikipedia), // Replace with your QR code image
                            contentDescription = "QR Code",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(200.dp)
                                .padding(vertical = 16.dp)
                        )
                        Text(
                            "View ID details",
                            color = Color(0xFF55c1f6),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = poppinsFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Menu Items
                DrawerMenuItem(
                    icon = painterResource(R.drawable.language),
                    title = "Languages",
                    subtitle = "Change your Language"
                )
                DrawerMenuItem(
                    icon = painterResource(R.drawable.help_alt_svgrepo_com),
                    title = "Help and support",
                    subtitle = null
                )
                DrawerMenuItem(
                    icon = painterResource(R.drawable.info_svgrepo_com),
                    title = "About App",
                    subtitle = null
                )
                DrawerMenuItem(
                    icon = painterResource(R.drawable.logout_svgrepo_com),
                    title = "Log Out",
                    subtitle = null
                )
            }
        }

        }
}

@Composable
fun DrawerMenuItem(icon: Painter, title: String, subtitle: String?) {
    Card(
        modifier = Modifier.padding(horizontal = 14.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontFamily = poppinsFontFamily,)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = poppinsFontFamily
                    )
                }
            }
            Icon(painter = painterResource(R.drawable.right), contentDescription = null, tint = Color.Gray)
        }
    }
}
