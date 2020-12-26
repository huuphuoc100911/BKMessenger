package com.example.bkmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.UUID;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ChatActivity extends AppCompatActivity {
    EditText edt_chat;
    Button btn_chat, btn_friends;
    ListView lv_chat;
    ZaloAdapter messageAdapter;
    ZaloDisplay messageDisplay;
    public static String uniqueId;

    ArrayList<ZaloDisplay> list_message;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.31.162:4000/");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSocket.connect();
        mSocket.on("ServerSendMessageToAll",onChat);

        edt_chat=(EditText)findViewById(R.id.edt_chat);

        btn_chat= (Button)findViewById(R.id.btn_chat);
        uniqueId = UUID.randomUUID().toString();

       list_message= new ArrayList<ZaloDisplay>();
        lv_chat=(ListView)findViewById(R.id.lv_chat);

        Intent intent= getIntent();
        final String username_extra= intent.getStringExtra(EXTRA_MESSAGE);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data_message = new JSONObject();
                try {
                    data_message.put("name_friend",username_extra);
                    data_message.put("message",edt_chat.getText().toString());
                    data_message.put("unique_id", uniqueId);
                    data_message.put("id_socket",mSocket.id());
                    mSocket.emit("SendMessageToServer",data_message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private Emitter.Listener onChat= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject content;
                    try {
                        content = data.getJSONObject("contentChat");

                        String name = content.getString("name");
                        String message = content.getString("message");
                        String id = content.getString("unique_id");

                        //lv_chat=(ListView)findViewById(R.id.lv_chat);
                        messageDisplay= new ZaloDisplay(name, message, id);

                        list_message.add(messageDisplay);
                        messageAdapter= new ZaloAdapter(getApplicationContext(), R.layout.item_message, list_message);
                        lv_chat.setAdapter(messageAdapter);

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}