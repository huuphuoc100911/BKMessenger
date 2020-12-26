package com.example.bkmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    EditText edt_username, edt_password;
    Button btn_register, btn_login;
    ListView lv_chat;
    ArrayList<String>ListChat;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.31.162:4000/");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();
        mSocket.on("ResultRegister",onNewMessage);




        edt_username=(EditText)findViewById(R.id.edt_username);
        edt_password=(EditText)findViewById(R.id.edt_password);
        btn_register=(Button)findViewById(R.id.btn_register);
        btn_login=(Button)findViewById(R.id.btn_login);


       btn_register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String txt_username=edt_username.getText().toString();

               String txt_password=edt_password.getText().toString();

               if(TextUtils.isEmpty(txt_username)||TextUtils.isEmpty(txt_password)){
                   Toast.makeText(MainActivity.this,"Nhập tất cả các trường",Toast.LENGTH_SHORT).show();
               }else if(txt_password.length()<6){
                   Toast.makeText(MainActivity.this,"Mật khẩu ít nhất 6 ký tự",Toast.LENGTH_SHORT).show();
               }else {
                   JSONObject post_data= new JSONObject();
                   try{
                       post_data.put("username",txt_username);
                       post_data.put("password", txt_password);
                       post_data.put("IDSocket", mSocket.id());
                       mSocket.emit("ClientRegister",post_data);
                   }catch (JSONException e){
                       e.printStackTrace();
                   }

               }
           }
       });


      btn_login.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(MainActivity.this, LoginActivity.class));
          }
      });

    }

    private Emitter.Listener onNewMessage= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String content;
                    try {
                        content = data.getString("content");
                        if (content == "true") {
                            Toast.makeText(getApplicationContext(), "Register Succeed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,ListFriends.class));
                        } else if (content == "false"){
                            Toast.makeText(getApplicationContext(), "Account existed!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };



}