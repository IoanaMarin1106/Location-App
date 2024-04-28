package com.example.locapp.tflite

import android.os.Environment
import android.util.Log
import com.example.locapp.MainActivity
import com.example.locapp.collector.DataCollector
import com.example.locapp.utils.Utils
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TFLiteModelManager {

    private val utils: Utils = Utils()
    private val TAG = "TFLiteModelManager"
    private var interpreter: Interpreter

    init {
        while(!File("${MainActivity.modelDirectory}/mobility_model.tflite").exists()) {
            Thread.sleep(100)
        }

        interpreter = Interpreter(File("${MainActivity.modelDirectory}/mobility_model.tflite"))
    }
    fun trainModel(): Float {
        val (features, labels) = getDataForTraining()

        // remove places file
        File(DataCollector.placesFilePath).delete();

        var lastLoss = 0f

        for (i in 0 until features.size) {
            val f = ByteBuffer.allocateDirect(4 * features[i].size).order(ByteOrder.nativeOrder())
            features[i].forEach { f.putFloat(it) }
            f.rewind()

            val l = ByteBuffer.allocate(4 * labels[i].size).order(ByteOrder.nativeOrder())
            labels[i].forEach { l.putFloat(it) }
            l.rewind()

            val inputs: MutableMap<String, Any> = HashMap()
            val outputs: MutableMap<String, Any> = HashMap()

            inputs["features"] = f
            inputs["label"] = l

            val loss = FloatBuffer.allocate(1)

            outputs["loss"] = loss

            interpreter.runSignature(inputs, outputs, "train")

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

        interpreter.runSignature(inputs, outputs, "save")

        return outputFile.absolutePath
    }

    fun restoreModel(checkpointPath: String): Boolean {
        val checkpointFile = File(checkpointPath)

        val inputs: MutableMap<String, Any> = HashMap()
        inputs["checkpoint_path"] = checkpointFile.absolutePath

        val outputs: Map<String, Any> = HashMap()

        interpreter.runSignature(inputs, outputs, "restore")

        return outputs.isNotEmpty()
    }

    fun predict(lat: Float, long: Float, day: Int, hour: Int): Map<Int, Float> {
        val features = ByteBuffer.allocateDirect(4 * Float.SIZE_BYTES).order(ByteOrder.nativeOrder())
        features.apply {
            putFloat(lat)
            putFloat(long)
            putFloat(day.toFloat())
            putFloat(hour.toFloat())
        }
        features.rewind()

        val probabilities = FloatBuffer.allocate(3426)

        val inputs: MutableMap<String, Any> = HashMap()
        val outputs: MutableMap<String, Any> = HashMap()

        inputs["features"] = features
        outputs["output"] = probabilities

        interpreter.runSignature(inputs, outputs, "infer")

        probabilities.rewind()

        val probabilitiesMap: MutableMap<Int, Float> = HashMap()

        for (i in 0 until 3426) {
            probabilitiesMap[i] = probabilities.get()
        }

        return probabilitiesMap
    }

    private fun getDataForTraining(): Pair<MutableList<FloatArray>, MutableList<FloatArray>> {
        val features = mutableListOf<FloatArray>()
        val labels = mutableListOf<FloatArray>()

        val placesFilePath = Environment.getExternalStorageDirectory().path + "/Download/LocationData/locations.txt"
        val content = File(placesFilePath).readLines()

        content.forEach {
            val data = it.split(',')
            features.add(floatArrayOf(data[0].toFloat(), data[1].toFloat(), data[2].toFloat(), data[3].toFloat()))

            val placeLabel = FloatArray(3426) {0f}
            placeLabel[data[4].toInt()] = 1f
            labels.add(placeLabel)
        }

        Log.d(TAG, "Features: $features")
        Log.d(TAG, "Labels: $labels")

        return features to labels
    }

    private fun generateFakeDataForTraining(): Pair<MutableList<FloatArray>, MutableList<FloatArray>> {
        val features = mutableListOf(
            floatArrayOf(40.78636129f, -72.95981346f, 3.0f, 13.0f),
            floatArrayOf(41.78382549f, -72.96214703f, 2.0f, 15.0f),
            floatArrayOf(40.75249644f, -72.92982953f, 1.0f, 12.0f),
            floatArrayOf(40.72249953f, -73.98478486f, 4.0f, 21.0f)
        )

        val fb1 = FloatArray(3426) {0f}
        fb1[3] = 1f
        val fb2 = FloatArray(3426) {0f}
        fb2[0] = 1f
        val fb3 = FloatArray(3426) {0f}
        fb3[65] = 1f
        val fb4 = FloatArray(3426) {0f}
        fb4[2] = 1f
        val labels = mutableListOf(
            fb1,fb2,fb3,fb4
        )

        return features to labels
    }
}