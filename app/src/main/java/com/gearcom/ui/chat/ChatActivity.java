package com.gearcom.ui.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gearcom.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {
    Button btnSend;
    EditText txtMessage;
    ListView listView;
    ArrayList<String> listChat;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.9:3000");
        } catch (URISyntaxException e) {}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSocket.connect();
        mSocket.on("server-gui-client", onNewMessage);

        txtMessage = findViewById(R.id.txtMessage);
        btnSend = findViewById(R.id.btnSend);
        listView = findViewById(R.id.listView);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("client-gui-server", txtMessage.getText());
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("server-gui-client", onNewMessage);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray noidung;
                    try {
                        noidung = data.getJSONArray("danhsach");
                        listView = findViewById(R.id.listView);
                        listChat = new ArrayList<>();
                        listChat.add("Teo");
                        for (int i=0; i<noidung.length();i++){
                            listChat.add( noidung.get(i).toString() );
                        }
                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, listChat);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

}