package sidddharth.co.btc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import sidddharth.co.btc.models.NewBlock;
import sidddharth.co.btc.models.UTX;

public class MainActivity extends AppCompatActivity {

    OkHttpClient okHttpClient;
    TextView webSocketConnectionStatusText;
    private static final String OP_UTX = "utx";
    private static final String OP_BLOCK = "block";
    TextView blockHash;
    TextView btcSent;
    TextView blockSize;
    TextView reward;
    TextView blockHeight;
    RecyclerView utxList;
    List<UTX> utxes;
    UtxListAdapter utxListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webSocketConnectionStatusText = findViewById(R.id.ws_connection_status);
        setupNewBlockInfoUI();
        utxList = findViewById(R.id.utx_list);
        utxListAdapter = new UtxListAdapter(this);
        utxList.setLayoutManager(new LinearLayoutManager(this));
        utxList.setAdapter(utxListAdapter);
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
                Log.d("WS status", "Socket connection failed" + t.getMessage());

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
                        final NewBlock newBlock = parseBlock(jsonObject);
                        setNewBlockInfo(newBlock);
                    } else if (OP_UTX.equalsIgnoreCase(messageType)) {
                        final UTX utx = parseUnConfirmedTransaction(jsonObject);
                        addUTX(utx);
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

    private void addUTX(final UTX utx) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (utxListAdapter != null) {
                    utxListAdapter.addUTX(utx);
                }

            }
        });
    }

    private void setupNewBlockInfoUI() {
        blockHash = findViewById(R.id.block_hash);
        blockHeight = findViewById(R.id.block_height);
        reward = findViewById(R.id.reward);
        btcSent = findViewById(R.id.btc_sent);
        blockSize = findViewById(R.id.block_size);
    }

    private void setNewBlockInfo(final NewBlock newBlock) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                blockHash.setText("Block Hash: " + newBlock.getHash());
                blockHeight.setText("Block Height: " + String.valueOf(newBlock.getHeight()));
                blockSize.setText("Block size: " + String.valueOf(newBlock.getSize()));
                reward.setText("Reward: " + String.valueOf(newBlock.getReward()));
                btcSent.setText("BTC Sent: " + String.valueOf(newBlock.getTotalBTCSent()));
            }
        });

    }

    private NewBlock parseBlock(JSONObject jsonObject) {
        NewBlock newBlock = new NewBlock();
        JSONObject info = jsonObject.optJSONObject("x");
        newBlock.setHash(info.optString("hash"));
        newBlock.setHeight(info.optInt("height"));
        newBlock.setReward(info.optDouble("reward"));
        newBlock.setSize(info.optDouble("size"));
        newBlock.setTotalBTCSent(info.optDouble("totalBTCSent"));
        return newBlock;
    }

    private UTX parseUnConfirmedTransaction(JSONObject jsonObject) {
        UTX utx = new UTX();
        final JSONObject info = jsonObject.optJSONObject("x");
        utx.setTransactionHash(info.optString("hash"));
        utx.setTimestamp(info.optLong("time") * 1000);
        JSONArray inputsArray = info.optJSONArray("inputs");
        double totalAmount = 0;
        for (int i = 0; i < inputsArray.length(); i++) {
            JSONObject input = inputsArray.optJSONObject(i);
            if (input != null) {
                double value = input.optJSONObject("prev_out").optDouble("value");
                totalAmount += value;
            }
        }
        utx.setTransactionAmnt(totalAmount);

        return utx;

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
