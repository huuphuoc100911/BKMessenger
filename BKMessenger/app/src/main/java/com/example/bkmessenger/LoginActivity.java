package com.example.bkmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class LoginActivity extends AppCompatActivity {
    EditText edt_username, edt_password;
    Button btn_login;
    private Socket mSocket;
    {
        try{
            mSocket = IO.socket("http://192.168.31.162:4000/");
        }catch (URISyntaxException e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSocket.connect();
        mSocket.on("ResultLogin", CheckLogin);

        edt_username= (EditText)findViewById(R.id.edt_login_username);
        edt_password= (EditText)findViewById(R.id.edt_login_password);
        btn_login= (Button)findViewById(R.id.btn_login_form);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username= edt_username.getText().toString();
                String txt_password= edt_password.getText().toString();

                if(TextUtils.isEmpty(txt_username)|| TextUtils.isEmpty(txt_password)){
                    Toast.makeText(LoginActivity.this, "Nhập tất cả các trường.", Toast.LENGTH_SHORT).show();
                }else if(txt_password.length() < 6){
                    Toast.makeText(LoginActivity.this, "Mật khẩu có ít nhất 6 ký tự.",Toast.LENGTH_SHORT).show();
                }else {
                    JSONObject post_data = new JSONObject();
                    try{
                        post_data.put("Username",txt_username);
                        post_data.put("Password", txt_password);
                        post_data.put("id_sk", mSocket.id());
                        mSocket.emit("ClientLogin", post_data);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private Emitter.Listener CheckLogin= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String content_login;
                    try{
                        content_login = data.getString("content_login");
                        if (content_login == "true") {
                            Toast.makeText(getApplicationContext(), "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,ListFriends.class));
                        } else if (content_login == "false"){
                            Toast.makeText(getApplicationContext(), "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e){
                        return;
                    }
                }
            });

        }
    };
}