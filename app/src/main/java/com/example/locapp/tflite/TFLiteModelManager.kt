package com.example.locapp.tflite

import android.os.Environment
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class TFLiteModelManager {

    private val TAG = "TFLiteModelManager"
    private var interpreter: Interpreter

    init {
        val modelPath = Environment.getExternalStorageDirectory().path + "/Download/mobility_model_v2.tflite"
        while(!File(modelPath).exists()) {
            Thread.sleep(100)
        }

        interpreter = Interpreter(File(modelPath))
    }
    fun trainModel(): Float {
        val (features, labels) = generateFakeDataForTraining()
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
        val outputFile = File(checkpointDir, "checkpoint.ckpt")

        val inputs: MutableMap<String, Any> = HashMap()
        inputs["checkpoint_path"] = outputFile.absolutePath

        val outputs: Map<String, Any> = HashMap()

        interpreter.runSignature(inputs, outputs, "save")

        return outputFile.absolutePath
    }

    private fun generateFakeDataForTraining(): Pair<MutableList<FloatArray>, MutableList<FloatArray>> {
        val features = mutableListOf(
            floatArrayOf(40.78636129f, -72.95981346f, 3.0f, 13.0f),
            floatArrayOf(41.78382549f, -72.96214703f, 2.0f, 15.0f),
            floatArrayOf(40.75249644f, -72.92982953f, 1.0f, 12.0f),
            floatArrayOf(40.72249953f, -73.98478486f, 4.0f, 21.0f)
        )

        val fb1 = FloatArray(347) {0f}
        fb1[3] = 1f
        val fb2 = FloatArray(347) {0f}
        fb2[0] = 1f
        val fb3 = FloatArray(347) {0f}
        fb3[65] = 1f
        val fb4 = FloatArray(347) {0f}
        fb4[2] = 1f
        val labels = mutableListOf(
            fb1,fb2,fb3,fb4
        )

        return features to labels
    }
}