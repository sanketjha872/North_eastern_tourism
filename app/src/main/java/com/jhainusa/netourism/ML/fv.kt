package com.jhainusa.netourism.ML

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyClassifierScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var classifier by remember { mutableStateOf<EmergencyClassifier?>(null) }
    var inputText by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<ClassificationResult?>(null) }
    var debugInfo by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var initError by remember { mutableStateOf<String?>(null) }

    // Initialize classifier
    LaunchedEffect(Unit) {
        try {
            classifier = EmergencyClassifier(context)
        } catch (e: Exception) {
            initError = "Error initializing classifier: ${e.message}"
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            classifier?.close()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "🚨 Emergency Classifier",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input field
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Describe the emergency...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            enabled = classifier != null && !isLoading,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Classify button
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        debugInfo = null
                        try {
                            val classificationResult = withContext(Dispatchers.Default) {
                                classifier?.classify(inputText.trim())
                            }
                            result = classificationResult
                        } catch (e: Exception) {
                            result = null
                            debugInfo = "Error: ${e.message}\n${e.stackTraceToString()}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = classifier != null && !isLoading && inputText.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Classify", fontSize = 16.sp)
                }
            }

            // Debug button
            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            val info = withContext(Dispatchers.Default) {
                                classifier?.debugInfo(inputText.trim())
                            }
                            debugInfo = info
                            result = null
                        } catch (e: Exception) {
                            debugInfo = "Debug Error: ${e.message}"
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                enabled = classifier != null && !isLoading && inputText.isNotBlank()
            ) {
                Text("🔍 Debug", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Error message
        if (initError != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = initError!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Result display
        result?.let { classificationResult ->
            ResultCard(classificationResult)
        }

        // Debug info display
        debugInfo?.let { info ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Debug Information",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = info,
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Example test cases
        if (result == null && initError == null) {
            ExampleTestCases(
                onExampleClick = { example ->
                    inputText = example
                }
            )
        }
    }
}

@Composable
fun ResultCard(result: ClassificationResult) {
    val emoji = when (result.category.lowercase()) {
        "police" -> "🚔"
        "fire" -> "🚒"
        "ambulance" -> "🚑"
        "women helpline" -> "👩"
        "disaster management" -> "⛰️"
        else -> "❓"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Main category
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = result.category.uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Confidence: ${String.format("%.1f%%", result.confidence * 100)}",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            // Detailed breakdown
            Text(
                text = "Detailed Breakdown:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            result.allProbabilities
                .toList()
                .sortedByDescending { it.second }
                .forEach { (category, prob) ->
                    ProbabilityBar(category, prob)
                }
        }
    }
}

@Composable
fun ProbabilityBar(category: String, probability: Float) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = String.format("%.1f%%", probability * 100),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        LinearProgressIndicator(
            progress = probability,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 2.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun ExampleTestCases(onExampleClick: (String) -> Unit) {
    val examples = listOf(
        "Someone following me with knife help",
        "Hotel room caught fire smoke everywhere",
        "Tourist fell from cliff bleeding heavily",
        "Man stalking me feeling unsafe",
        "Landslide blocked road people trapped"
    )

    Column {
        Text(
            text = "Try Example Cases:",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        examples.forEach { example ->
            OutlinedButton(
                onClick = { onExampleClick(example) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = example,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}