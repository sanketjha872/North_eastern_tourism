package com.jhainusa.netourism.ML


import android.content.Context
import com.google.gson.Gson
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

data class ClassificationResult(
    val category: String,
    val confidence: Float,
    val allProbabilities: Map<String, Float>
)

data class LabelMappingJson(
    val label_map: Map<String, Int>,
    val id_to_label: Map<String, String>,
    val num_labels: Int
)

class EmergencyClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var vocabulary: Map<String, Int>? = null
    private var labelMapping: Map<Int, String>? = null
    private val maxLength = 128

    init {
        loadModel()
        loadVocabulary()
        loadLabelMapping()
    }

    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile("emergency_classifier.tflite")
            val options = Interpreter.Options().apply {
                setNumThreads(4)
                // NNAPI disabled - causes errors with DistilBERT operations
            }
            interpreter = Interpreter(modelBuffer, options)
        } catch (e: Exception) {
            throw RuntimeException("Error loading TFLite model: ${e.message}")
        }
    }

    private fun loadModelFile(filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadVocabulary() {
        try {
            val vocabText = context.assets.open("vocab.txt").bufferedReader().use { it.readText() }
            vocabulary = vocabText.lines()
                .filter { it.isNotBlank() }
                .mapIndexed { index, word -> word to index }
                .toMap()
        } catch (e: Exception) {
            throw RuntimeException("Error loading vocabulary: ${e.message}")
        }
    }

    private fun loadLabelMapping() {
        try {
            val json = context.assets.open("label_mapping.json").bufferedReader().use { it.readText() }
            val mappingData = Gson().fromJson(json, LabelMappingJson::class.java)

            // Convert id_to_label to Map<Int, String>
            labelMapping = mappingData.id_to_label.mapKeys { it.key.toInt() }
        } catch (e: Exception) {
            throw RuntimeException("Error loading label mapping: ${e.message}")
        }
    }

    private fun tokenize(text: String): IntArray {
        val cleanText = text.lowercase().trim()
        val tokens = mutableListOf<Int>()

        // Add CLS token at the beginning (typically 101 for BERT)
        val clsToken = vocabulary?.get("[CLS]") ?: 101
        tokens.add(clsToken)

        // Tokenize words
        val words = cleanText.split(Regex("\\s+")).filter { it.isNotBlank() }

        for (word in words) {
            // Try to find the word in vocabulary
            val tokenId = vocabulary?.get(word)
            if (tokenId != null) {
                tokens.add(tokenId)
            } else {
                // If word not found, try WordPiece tokenization (basic version)
                val subTokens = wordPieceTokenize(word)
                tokens.addAll(subTokens)
            }

            if (tokens.size >= maxLength - 1) break
        }

        // Add SEP token at the end (typically 102 for BERT)
        val sepToken = vocabulary?.get("[SEP]") ?: 102
        if (tokens.size < maxLength) {
            tokens.add(sepToken)
        }

        // Pad or truncate to maxLength
        val paddedTokens = IntArray(maxLength)
        for (i in 0 until minOf(tokens.size, maxLength)) {
            paddedTokens[i] = tokens[i]
        }

        return paddedTokens
    }

    private fun wordPieceTokenize(word: String): List<Int> {
        val tokens = mutableListOf<Int>()
        var start = 0

        while (start < word.length) {
            var end = word.length
            var foundToken: Int? = null

            while (start < end) {
                val substr = if (start > 0) {
                    "##${word.substring(start, end)}"
                } else {
                    word.substring(start, end)
                }

                val tokenId = vocabulary?.get(substr)
                if (tokenId != null) {
                    foundToken = tokenId
                    break
                }
                end--
            }

            if (foundToken != null) {
                tokens.add(foundToken)
                start = end
            } else {
                // Use [UNK] token if no match found
                val unkToken = vocabulary?.get("[UNK]") ?: 100
                tokens.add(unkToken)
                start++
            }
        }

        return tokens
    }

    fun classify(text: String): ClassificationResult {
        if (interpreter == null || vocabulary == null || labelMapping == null) {
            throw IllegalStateException("Classifier not properly initialized")
        }

        if (text.isBlank()) {
            throw IllegalArgumentException("Input text cannot be empty")
        }

        // Tokenize input
        val inputTokens = tokenize(text)

        // Create attention mask (1 for real tokens, 0 for padding)
        val attentionMask = IntArray(maxLength) { i ->
            if (inputTokens[i] == 0) 0 else 1
        }

        // Check number of inputs
        val numInputs = interpreter!!.inputTensorCount

        android.util.Log.d("EmergencyClassifier", "Number of model inputs: $numInputs")
        android.util.Log.d("EmergencyClassifier", "Input tokens (first 10): ${inputTokens.take(10).joinToString()}")
        android.util.Log.d("EmergencyClassifier", "Attention mask (first 10): ${attentionMask.take(10).joinToString()}")

        val outputArray = Array(1) { FloatArray(5) }

        if (numInputs == 2) {
            // Model expects both input_ids and attention_mask
            val inputIdsArray = Array(1) { inputTokens }
            val attentionMaskArray = Array(1) { attentionMask }

            // Create input/output maps
            val inputs = arrayOf(inputIdsArray, attentionMaskArray)
            val outputs = hashMapOf<Int, Any>(0 to outputArray)

            interpreter?.runForMultipleInputsOutputs(inputs, outputs)

        } else {
            // Model expects only input_ids
            val inputArray = Array(1) { inputTokens }
            interpreter?.run(inputArray, outputArray)
        }

        return processOutput(outputArray[0])
    }

    private fun processOutput(logits: FloatArray): ClassificationResult {
        // Log raw output for debugging
        val rawOutput = logits.joinToString(", ") { "%.4f".format(it) }
        android.util.Log.d("EmergencyClassifier", "Raw logits: [$rawOutput]")

        // Check if output is already probabilities (sum ≈ 1.0)
        val sum = logits.sum()
        android.util.Log.d("EmergencyClassifier", "Sum of outputs: $sum")

        val probabilities = if (sum > 0.95 && sum < 1.05) {
            // Already softmax probabilities
            android.util.Log.d("EmergencyClassifier", "Output appears to be probabilities")
            logits.toList()
        } else {
            // Apply softmax
            android.util.Log.d("EmergencyClassifier", "Applying softmax")
            val expLogits = logits.map { Math.exp(it.toDouble()).toFloat() }
            val sumExp = expLogits.sum()
            expLogits.map { it / sumExp }
        }

        // Find the category with highest probability
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
        val maxConfidence = probabilities[maxIndex]
        val predictedCategory = labelMapping?.get(maxIndex) ?: "Unknown"

        android.util.Log.d("EmergencyClassifier", "Predicted: $predictedCategory (index: $maxIndex, confidence: $maxConfidence)")

        // Format category name properly
        val formattedCategory = formatCategoryName(predictedCategory)

        // Create probability map with formatted names
        val allProbabilities = probabilities.mapIndexed { index, prob ->
            val label = labelMapping?.get(index) ?: "Unknown"
            formatCategoryName(label) to prob
        }.toMap()

        return ClassificationResult(
            category = formattedCategory,
            confidence = maxConfidence,
            allProbabilities = allProbabilities
        )
    }

    private fun formatCategoryName(category: String): String {
        return when (category.lowercase()) {
            "police" -> "Police"
            "fire" -> "Fire"
            "ambulance" -> "Ambulance"
            "women_helpline", "women helpline" -> "Women Helpline"
            "disaster", "disaster_management", "disaster management" -> "Disaster Management"
            else -> category.split("_", " ")
                .joinToString(" ") { it.capitalize() }
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }

    // Debug function to inspect model and tokenization
    fun debugInfo(text: String): String {
        val tokens = tokenize(text)
        val tokenWords = tokens.take(30).mapIndexed { idx, id ->
            val word = vocabulary?.entries?.find { it.value == id }?.key ?: "UNK"
            "$idx:$word($id)"
        }

        val numInputs = interpreter?.inputTensorCount ?: 0
        val numOutputs = interpreter?.outputTensorCount ?: 0
        val inputShape = interpreter?.getInputTensor(0)?.shape()?.joinToString(",") ?: "unknown"
        val outputShape = interpreter?.getOutputTensor(0)?.shape()?.joinToString(",") ?: "unknown"
        val inputType = interpreter?.getInputTensor(0)?.dataType()?.toString() ?: "unknown"
        val outputType = interpreter?.getOutputTensor(0)?.dataType()?.toString() ?: "unknown"

        return buildString {
            appendLine("=== DEBUG INFO ===")
            appendLine("Input: $text")
            appendLine("\nModel Info:")
            appendLine("  Number of inputs: $numInputs")
            appendLine("  Number of outputs: $numOutputs")
            appendLine("  Input shape: [$inputShape]")
            appendLine("  Output shape: [$outputShape]")
            appendLine("  Input type: $inputType")
            appendLine("  Output type: $outputType")
            appendLine("\nVocabulary size: ${vocabulary?.size ?: 0}")
            appendLine("Label mapping: ${labelMapping?.size ?: 0} classes")
            appendLine("\nTokenization (first 30):")
            tokenWords.forEach { appendLine("  $it") }
            appendLine("\nSpecial tokens:")
            appendLine("  [CLS]: ${vocabulary?.get("[CLS]")}")
            appendLine("  [SEP]: ${vocabulary?.get("[SEP]")}")
            appendLine("  [UNK]: ${vocabulary?.get("[UNK]")}")
            appendLine("  [PAD]: ${vocabulary?.get("[PAD]")}")
        }
    }
}

// Extension function for easy usage
fun ClassificationResult.toDisplayString(): String {
    return buildString {
        appendLine("Emergency Type: $category")
        appendLine("Confidence: ${String.format("%.2f%%", confidence * 100)}")
        appendLine("\nAll Probabilities:")
        allProbabilities.forEach { (label, prob) ->
            appendLine("  $label: ${String.format("%.2f%%", prob * 100)}")
        }
    }
}