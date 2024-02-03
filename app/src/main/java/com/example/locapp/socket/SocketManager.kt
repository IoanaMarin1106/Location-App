import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

class SocketManager {
    private var socket: Socket? = null

    companion object {
        private val TAG = "SOCKET_MANAGER"
        val START_TRAIN_EVENT = "start_train"
        val GET_CHECKPOINT_UPLOAD_URL_EVENT = "get_checkpoint_upload_url"
        val UPLOAD_CHECKPOINT_EVENT = "upload_checkpoint"
        val DOWNLOAD_MODEL_EVENT = "download_model"
        val GET_CHECKPOINT_EVENT = "get_checkpoint"
    }

    init {
        try {
            socket = IO.socket("http://10.0.2.2:5000")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun connect() {
        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }

    fun onMessageReceived(startTrainListener: (String) -> Unit,
                          uploadCheckpointListener: (JSONObject) -> Unit,
                          downloadModelListener: (String) -> Unit,
                          getCheckpointListener: (String) -> Unit) {
        socket?.on(DOWNLOAD_MODEL_EVENT) { args ->
            val arg = args[0] as String
            Log.d("$TAG $DOWNLOAD_MODEL_EVENT", "Found arg=$arg")
            downloadModelListener.invoke(arg)
        }

        socket?.on(START_TRAIN_EVENT) { args ->
            val arg = args[0] as String
            Log.d("$TAG $START_TRAIN_EVENT", "Found arg=$arg")
            startTrainListener.invoke(arg)
        }

        socket?.on(UPLOAD_CHECKPOINT_EVENT) { args ->
            val arg = args[0] as JSONObject
            Log.d("$TAG $UPLOAD_CHECKPOINT_EVENT", "Found arg=$arg")
            uploadCheckpointListener.invoke(arg)
        }

        socket?.on(GET_CHECKPOINT_EVENT) { args ->
            val arg = args[0] as String
            Log.d("$TAG $GET_CHECKPOINT_EVENT", "Found arg=$arg")
            getCheckpointListener.invoke(arg)
        }
    }

    fun sendMessage(eventType: String, message: String) {
        socket?.emit(eventType, message)
    }
}