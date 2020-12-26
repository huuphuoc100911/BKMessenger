package com.example.bkmessenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class ZaloAdapter extends ArrayAdapter<ZaloDisplay> {
    private Context context;
    private int resource;
    private ArrayList<ZaloDisplay> list;

    public ZaloAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ZaloDisplay> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list=list;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String username=getItem(position).getUsername();
        String message= getItem(position).getMessage();
        String id_unique= getItem(position).getId_unique();
        ZaloDisplay messageDisplay = new ZaloDisplay(username, message, id_unique);

        if(messageDisplay.getId_unique().equals(ChatActivity.uniqueId)){
            LayoutInflater inflater= LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_item_chat, parent, false);
            TextView messageText =(TextView) convertView.findViewById(R.id.edt_chat);
            TextView nameText =(TextView) convertView.findViewById(R.id.edt_login_username);


            messageText.setText(messageDisplay.getMessage());
            nameText.setText(messageDisplay.getUsername());

        }else {
            LayoutInflater inflater= LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.server_send_message, parent, false);
            TextView messageText =(TextView) convertView.findViewById(R.id.edt_chat);
            TextView nameText =(TextView) convertView.findViewById(R.id.edt_login_username);


            messageText.setText(messageDisplay.getMessage());
            nameText.setText(messageDisplay.getUsername());
        }

        return convertView;
    }
}
