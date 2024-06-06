package com.example.locapp.service

import SocketManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.locapp.R
import com.example.locapp.downloader.AndroidDownloader
import com.example.locapp.room.repository.Repository
import com.example.locapp.socket.SdkManager
import com.example.locapp.tflite.TFLiteModelManager
import com.example.locapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@AndroidEntryPoint
class SocketService: Service() {
    private val socketManager = SocketManager()
    private val utils = Utils()

    @Inject
    lateinit var repository: Repository

    private val modelManager: TFLiteModelManager by lazy {
        TFLiteModelManager(repository = repository)
    }

    private val okHttpClient = OkHttpClient()

    private val TAG = "SocketService"
    companion object {
        val checkpointsDirectoryPath = Environment.getExternalStorageDirectory().path + "/Download/Checkpoints"
        val modelDirectory = Environment.getExternalStorageDirectory().path + "/Download/Model"
    }

    override fun onCreate() {
        super.onCreate()

        // create checkpoints directory & model if not exist
        var result = utils.createDirectoryIfNotExists(checkpointsDirectoryPath)
        Log.d(TAG, "Checkpoints directory creation result: $result")

        result = utils.createDirectoryIfNotExists(modelDirectory)
        Log.d(TAG, "Model directory creation result: $result")


        Log.d(TAG, "onCreateCommand has been called")
        SdkManager.setModelManager(modelManager)

        // Connecting to Socket
        socketManager.connect()

        val fileExists = File("${modelDirectory}/mobility_model.tflite").exists()
        val eventType =  if (fileExists) "reconnect" else "initial_connect"
        val message = if (fileExists) "reconnected" else "connected"

        socketManager.sendMessage(eventType, message)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        onMessageReceived()
        Log.d(TAG, "onStartCommand has been called")
        return START_STICKY
    }


    private fun showToastInIntentService(sText: String?) {
        val context: Context = this
        Handler(Looper.getMainLooper()).post {
            val toast = Toast.makeText(
                context,
                Html.fromHtml("<font color='#5a4796'><b>$sText</b></font>", Html.FROM_HTML_MODE_LEGACY),
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.TOP, 0, 200)
            toast.show()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onMessageReceived() {
        val startTrainListener: (String) -> Unit = { subfolder ->
            GlobalScope.launch {
                showToastInIntentService("Training started")
                // TODO: Remove this
                Thread.sleep(2000)

                val date = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                val downloadPath = "${checkpointsDirectoryPath}/$date/"
                Log.d(TAG, subfolder)

                modelManager.trainModel()

                showToastInIntentService("Save model started")
                // TODO: Remove this
                Thread.sleep(2000)
                modelManager.saveModel(downloadPath)

                // This does: emit(get_checkpoint_upload_url, subfolder) to server
                socketManager.sendMessage(SocketManager.GET_CHECKPOINT_UPLOAD_URL_EVENT, subfolder)
            }
        }

        val uploadCheckpointListener: (JSONObject) -> Unit = {presignedDataRequest ->
            GlobalScope.launch {
                showToastInIntentService("Upload checkpoint started")

                val url = presignedDataRequest["url"].toString()
                val fields = mapOf(
                    "key" to (presignedDataRequest["fields"] as JSONObject).get("key").toString(),
                    "x-amz-algorithm" to (presignedDataRequest["fields"] as JSONObject).get("x-amz-algorithm").toString(),
                    "x-amz-credential" to (presignedDataRequest["fields"] as JSONObject).get("x-amz-credential").toString(),
                    "x-amz-date" to (presignedDataRequest["fields"] as JSONObject).get("x-amz-date").toString(),
                    "policy" to (presignedDataRequest["fields"] as JSONObject).get("policy").toString(),
                    "x-amz-signature" to (presignedDataRequest["fields"] as JSONObject).get("x-amz-signature").toString()
                )

                val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                for ((key, value) in fields) {
                    requestBody.addFormDataPart(key, value)
                }

                // Add file to the request
                val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                val file = utils.getLastFileFromDirectory("${checkpointsDirectoryPath}/$currentDate/")

                val mediaType = "application/octet-stream".toMediaTypeOrNull()
                val fileRequestBody = file.asRequestBody(mediaType)
                requestBody.addFormDataPart("file", file.name, fileRequestBody)

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody.build())
                    .build()

                try {
                    Log.d(TAG, "URL: ${request.url}")
                    Log.d(TAG, "")
                    val response = okHttpClient.newCall(request).execute()
                    Log.d(TAG, "Response: $response")
                } catch (e: IOException) {
                    Log.e(TAG, e.message, e)
                }
            }
        }

        val downloadModelListener: (String) -> Unit = { url ->
            GlobalScope.launch {
                showToastInIntentService("Download model started")

                val androidDownloader = AndroidDownloader(baseContext)
                androidDownloader.downloadFile(url, "/Model/mobility_model.tflite")
            }
        }

        val getCheckpointListener: (String) -> Unit = { url ->
            GlobalScope.launch {
                showToastInIntentService("Get checkpoint started")

                val androidDownloader = AndroidDownloader(baseContext)
                val date = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSSS"))
                val checkpointPath = "$date/checkpoint-$time.ckpt"
                androidDownloader.downloadFile(url, "Checkpoints/$checkpointPath")

                // Restore model weights from checkpoint
                Log.d(TAG, "Restoring model weights from checkpoint...")
                Thread.sleep(1000)
                val restored = modelManager.restoreModel("${checkpointsDirectoryPath}/$checkpointPath")

                if (restored) {
                    Log.d(TAG, "Successfully restored model weights from $checkpointPath")
                } else {
                    Log.e(TAG, "Failed to restore model weights from $checkpointPath")
                }
            }
        }

        socketManager.onMessageReceived(
            startTrainListener,
            uploadCheckpointListener,
            downloadModelListener,
            getCheckpointListener
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        // Disconnecting from Socket
        socketManager.disconnect()
        // Broadcast to restart the service
        val broadcastIntent = Intent("com.example.sdk.RESTART_SERVICE")
        sendBroadcast(broadcastIntent)

        Log.d(TAG, "onDestroyCommand has been called")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundService() {
        val channelId = "SocketServiceChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Socket Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Socket Service")
            .setContentText("Socket Service is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }
}