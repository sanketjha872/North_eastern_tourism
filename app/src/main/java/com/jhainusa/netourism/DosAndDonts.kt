package com.jhainusa.netourism

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jhainusa.netourism.Gemini.DosAndDontsViewModel
import com.jhainusa.netourism.Gemini.GeminiState
import com.jhainusa.netourism.Map.LocationRepository
import kotlinx.serialization.Serializable

@Serializable
data class Advice(val title: String, val description: String)

val tribes = listOf("Shillong Meghalaya")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosAndDontsScreen(viewModel: DosAndDontsViewModel = viewModel()) {
    var selectedTribe by remember { mutableStateOf("Shillong Meghalaya") }
    val placeName by LocationRepository.currentPlaceName.collectAsState()
    val geminiState by viewModel.geminiState.collectAsState()

    LaunchedEffect(placeName) {
        placeName?.let {
            if (it.contains("Garo", ignoreCase = true)) {
                selectedTribe = "Garo Hills"
            } else if (it.contains("Khasi", ignoreCase = true)) {
                selectedTribe = "Khasi Hills"
            } else if (it.contains("Jaintia", ignoreCase = true)) {
                selectedTribe = "Jaintia villages"
            }
        }
    }

    LaunchedEffect(selectedTribe) {
        viewModel.generateAdvice(selectedTribe)
    }

    Scaffold(
        modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text("Do's & Don'ts", fontWeight = FontWeight.Bold,
                    fontFamily = manropeMedium) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back press */ }) {
                        Icon(painterResource(R.drawable.left), contentDescription = "Back",
                            tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.generateAdvice(selectedTribe) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6))
            ) {
                Text("Generate New Advice", fontSize = 16.sp, color = Color.White,
                    fontFamily = manropeMedium)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F8FA))
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mapimage), // Placeholder for map
                        contentDescription = "Map",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
//            item{
//                Text(
//                    text = "Tribes in Shillong",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 16.sp,
//                    fontFamily = manropeMedium
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(tribes) { tribe ->
                        val isSelected = tribe == selectedTribe
                        Button(
                            onClick = { selectedTribe = tribe },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF0077B6) else Color.White,
                                contentColor = if (isSelected) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(tribe)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            when (val state = geminiState) {
                is GeminiState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is GeminiState.Success -> {
                    item {
                        Text(
                            text = "DO'S ($selectedTribe)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = manropeMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(state.dos) { advice ->
                        AdviceCard(advice = advice, isDo = true)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "DON'TS ($selectedTribe)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = manropeMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(state.donts) { advice ->
                        AdviceCard(advice = advice, isDo = false)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                is GeminiState.Error -> {
                    item {
                        Text(state.message, color = Color.Red,
                            fontFamily = manropeMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun AdviceCard(advice: Advice, isDo: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isDo) Color(0x3366BB6A) else Color(0x33FF6B6B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(if (isDo) R.drawable.tick else R.drawable.cancel),
                    contentDescription = null,
                    tint = if (isDo) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = advice.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = manropeMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = advice.description,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = manropeMedium
                )
            }
        }
    }
}
