package com.example.bkmessenger;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ListFriends extends AppCompatActivity {
    ListView list_friends;
    ArrayList<String> arrayList;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.31.162:4000/");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friends);
        mSocket.connect();
        mSocket.emit("friends", "list_friends");
        mSocket.on("server-send-username",listAccount);

        list_friends= (ListView) findViewById(R.id.lv_friends);

        list_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(ListFriends.this).setTitle("Gửi tin nhắn")
                        .setMessage("Bạn có muốn gửi tin nhắn không? ").setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ListFriends.this, ChatActivity.class)
                                .putExtra(EXTRA_MESSAGE,list_friends.getItemAtPosition(position).toString()));
                       // mSocket.emit("request_send_message", list_friends.getItemAtPosition(position).toString());
                    }
                }).setNegativeButton("Không", null).show();
            }
        });
    }

    private Emitter.Listener listAccount= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray list_username;
                    try {
                        list_username = data.getJSONArray("list_username");

                        arrayList= new ArrayList<>();
                        for(int i=0 ; i<list_username.length() ; i++){
                            arrayList.add(list_username.get(i).toString());
                        }
                        ArrayAdapter adapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,arrayList);
                        list_friends.setAdapter(adapter);

                    } catch (JSONException e) {

                    }
                }
            });
        }
    };
}