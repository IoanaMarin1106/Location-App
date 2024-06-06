package com.example.locapp.tflite

import android.util.Log
import com.example.locapp.room.entity.Location
import com.example.locapp.room.repository.Repository
import com.example.locapp.service.SocketService
import com.example.locapp.socket.SdkManager
import com.example.locapp.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class TFLiteModelManager @Inject constructor(
    private val repository: Repository,
) {

    private val utils: Utils = Utils()
    private val TAG = "TFLiteModelManager"
    private var interpreter: Interpreter

    companion object {
        const val NUMBER_OF_FEATURES = 3
        const val NUMBER_OF_KNOWN_PLACES = 3159


        const val INFER = "infer"
        const val RESTORE = "restore"
        const val SAVE = "save"
        const val TRAIN = "train"

        const val FEATURES = "features"
        const val LABEL = "label"
        const val LOSS = "loss"
        const val OUTPUT = "output"
    }

    init {
        while(!File("${SocketService.modelDirectory}/mobility_model.tflite").exists()) {
            Log.d(TAG, "Mobility model does not exists.")
            Thread.sleep(100)
        }

        interpreter = Interpreter(File("${SocketService.modelDirectory}/mobility_model.tflite"))
    }

    fun trainModel(): Float {
        val (features, labels) = SdkManager.getTrainingDataProvider()!!.getTrainingData()

        var lastLoss = 0f

        for (i in 0 until features.size) {
            val f = ByteBuffer.allocateDirect(Float.SIZE_BYTES * features[i].size).order(ByteOrder.nativeOrder())
            features[i].forEach { f.putFloat(it) }
            f.rewind()

            val l = ByteBuffer.allocate(Float.SIZE_BYTES * labels[i].size).order(ByteOrder.nativeOrder())
            labels[i].forEach { l.putFloat(it) }
            l.rewind()

            val inputs: MutableMap<String, Any> = HashMap()
            val outputs: MutableMap<String, Any> = HashMap()

            inputs[FEATURES] = f
            inputs[LABEL] = l

            val loss = FloatBuffer.allocate(1)

            outputs[LOSS] = loss

            interpreter.runSignature(inputs, outputs, TRAIN)

            if (i == features.size - 1) {
                loss.rewind()
                lastLoss = loss.get()
            }
        }

        Log.d(TAG, "Training lastLoss: $lastLoss")
        return lastLoss
    }

    fun saveModel(checkpointDir: String): String {
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSSS"))
        utils.createDirectoryIfNotExists(checkpointDir)

        val outputFile = File(checkpointDir, "checkpoint-$time.ckpt")

        val inputs: MutableMap<String, Any> = HashMap()
        inputs["checkpoint_path"] = outputFile.absolutePath

        val outputs: Map<String, Any> = HashMap()

        interpreter.runSignature(inputs, outputs, SAVE)
        Log.d(TAG, outputs.toString())

        return outputFile.absolutePath
    }

    fun restoreModel(checkpointPath: String): Boolean {
        val checkpointFile = File(checkpointPath)

        val inputs: MutableMap<String, Any> = HashMap()
        inputs["checkpoint_path"] = checkpointFile.absolutePath

        val outputs: MutableMap<String, Any> = HashMap()

        interpreter.runSignature(inputs, outputs, RESTORE)

        return outputs.isNotEmpty()
    }

    fun predict(day: Int, hour: Int, rating: Int = 5): Map<Int, Float> {
        val features = ByteBuffer.allocateDirect(NUMBER_OF_FEATURES * Float.SIZE_BYTES).order(ByteOrder.nativeOrder())
        features.apply {
            putFloat(day.toFloat())
            putFloat(hour.toFloat())
            putFloat(rating.toFloat())
        }
        features.rewind()

        val probabilities = FloatBuffer.allocate(NUMBER_OF_KNOWN_PLACES)

        val inputs: MutableMap<String, Any> = HashMap()
        val outputs: MutableMap<String, Any> = HashMap()

        inputs[FEATURES] = features
        outputs[OUTPUT] = probabilities

        interpreter.runSignature(inputs, outputs, INFER)

        probabilities.rewind()

        val probabilitiesMap: MutableMap<Int, Float> = HashMap()

        for (i in 0 until NUMBER_OF_KNOWN_PLACES) {
            probabilitiesMap[i] = probabilities.get()
        }

        return probabilitiesMap
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getDataForTraining(): Pair<MutableList<FloatArray>, MutableList<FloatArray>> {
        val features = mutableListOf<FloatArray>()
        val labels = mutableListOf<FloatArray>()

        var locations = emptyList<Location>()

        runBlocking {
            locations = repository.getLocationsForTrainingAndMarkAsUsed()
        }

        locations.forEach {
            features.add(floatArrayOf(it.hour.toFloat(), it.day.toFloat(), it.rating.toFloat()))

            val placeLabel = FloatArray(NUMBER_OF_KNOWN_PLACES) {0f}
            placeLabel[it.place_id] = 1f
            labels.add(placeLabel)
        }

        Log.d(TAG, "Features: $features")
        Log.d(TAG, "Labels: $labels")

        return features to labels
    }

    private fun generateFakeDataForTraining(): Pair<MutableList<FloatArray>, MutableList<FloatArray>> {
        val features = mutableListOf(
            floatArrayOf(3.0f, 13.0f, 5.0f),
            floatArrayOf(2.0f, 15.0f, 4.0f),
            floatArrayOf(1.0f, 12.0f, 5.0f),
            floatArrayOf(4.0f, 21.0f, 4.0f)
        )

        val fb1 = FloatArray(NUMBER_OF_KNOWN_PLACES) {0f}
        fb1[3] = 1f
        val fb2 = FloatArray(NUMBER_OF_KNOWN_PLACES) {0f}
        fb2[0] = 1f
        val fb3 = FloatArray(NUMBER_OF_KNOWN_PLACES) {0f}
        fb3[65] = 1f
        val fb4 = FloatArray(NUMBER_OF_KNOWN_PLACES) {0f}
        fb4[2] = 1f
        val labels = mutableListOf(
            fb1,fb2,fb3,fb4
        )

        return features to labels
    }
}