package sidddharth.co.btc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    OkHttpClient okHttpClient;
    TextView webSocketConnectionStatusText;
    private static final String OP_UTX = "utx";
    private static final String OP_BLOCK = "block";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webSocketConnectionStatusText = findViewById(R.id.ws_connection_status);
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url("wss://ws.blockchain.info/inv").build();
        WebSocket webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {


            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d("WS status", "Socket connection open");
                setWebSocketConnectionStatusText("Connected");
                webSocket.send("{\"op\":\"blocks_sub\"}");
                webSocket.send("{\"op\":\"unconfirmed_sub\"}");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                setWebSocketConnectionStatusText("Disconnected");

            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.d("WS status", "Socket connection failed");
                setWebSocketConnectionStatusText("Failed");

            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Log.d("WS event", text);
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    final String messageType = jsonObject.optString("op");
                    if (OP_BLOCK.equalsIgnoreCase(messageType)) {
                        parseBlock(jsonObject);
                    } else if (OP_UTX.equalsIgnoreCase(messageType)) {
                        parseUnConfirmedTransaction(jsonObject);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                setWebSocketConnectionStatusText("Closing Connection");

            }
        });


    }

    private void parseBlock(JSONObject jsonObject) {

    }

    private void parseUnConfirmedTransaction(JSONObject jsonObject) {

    }

    void setWebSocketConnectionStatusText(final String statusText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webSocketConnectionStatusText.setText(statusText);
            }
        });
    }


}
