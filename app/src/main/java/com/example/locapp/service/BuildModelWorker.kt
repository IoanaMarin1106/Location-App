package com.example.locapp.service

import android.os.Environment
import android.util.Log
import com.example.locapp.tflite.TFLiteModelManager
import org.tensorflow.lite.Interpreter
import java.io.File


class BuildModelWorker(val evenType: String): Thread() {
    companion object {
        const val TAG = "BUILD_MODEL_WORKER"
        const val SLEEP_TIME: Long = 1000
    }

    var isRunning = true
    var lastLoss = 0f

    override fun run() {
        val modelPath = Environment.getExternalStorageDirectory().path + "/Download/mobility_model_v2.tflite"
        val checkpointPath = Environment.getExternalStorageDirectory().path + "/Download/"
        while(!File(modelPath).exists()) {
            sleep(SLEEP_TIME)
        }

        when(evenType) {
            "start_train" ->
                print("x")
            else ->
                print("Unsupported event type.")
        }

//        while(isRunning) {
            Log.i(TAG,"${currentThread()} is running.")

            // running
            val interpreter = Interpreter(File(modelPath))
            val outputFile = File(checkpointPath, "checkpoint.ckpt")
            val inputs: MutableMap<String, Any> = HashMap()
            inputs["checkpoint_path"] = outputFile.absolutePath
            val outputs: Map<String, Any> = HashMap()
            interpreter.runSignature(inputs, outputs, "save")

            Log.d(TAG, outputs["checkpoint_path"].toString())

//            val fb1 = FloatArray(347) {0f}
//            fb1[3] = 1f
//            val fb2 = FloatArray(347) {0f}
//            fb2[0] = 1f
//            val fb3 = FloatArray(347) {0f}
//            fb3[65] = 1f
//            val fb4 = FloatArray(347) {0f}
//            fb4[2] = 1f
//            val labels = mutableListOf(
//                fb1,fb2,fb3,fb4
//            )
//
//            val features = mutableListOf(
//                floatArrayOf(40.78636129f, -72.95981346f, 3.0f, 13.0f),
//                floatArrayOf(41.78382549f, -72.96214703f, 2.0f, 15.0f),
//                floatArrayOf(40.75249644f, -72.92982953f, 1.0f, 12.0f),
//                floatArrayOf(40.72249953f, -73.98478486f, 4.0f, 21.0f)
//            )
//
//            for (i in 0..3) {
//                val f = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder())
//                features[i].forEach { f.putFloat(it) }
//                f.rewind()
//
//                val l = ByteBuffer.allocate(4 * 347).order(ByteOrder.nativeOrder())
//                labels[i].forEach { l.putFloat(it) }
//                l.rewind()
//
//                val inputs: MutableMap<String, Any> = HashMap()
//                inputs["x"] = f
////                inputs["label"] = l
//
//                val loss = FloatBuffer.allocate(1)
//                val output = LongBuffer.allocate(1)
//
//
//                val outputs: MutableMap<String, Any> = HashMap()
////                outputs["loss"] = loss
//                outputs["output"] = output
//
//                interpreter.runSignature(inputs, outputs, "infer")
//
////                if (i == 3) {
//                    output.rewind()
//                    Log.d(TAG, ">>>Step:" + i + " output:" + output.get())
////                    loss.rewind()
////                    lastLoss = loss.get()
////                }
//            }

//            sleep(SLEEP_TIME)
//        }
    }

    fun stopWorker() {
        isRunning = false
    }

}