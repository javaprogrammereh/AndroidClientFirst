package com.example.androidclientfirst;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView text ;
    private EditText input;
    private Button btnSend;
    private Handler handler = new Handler();
    WifiManager wifiManager;
    private Socket socket;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    /*Server*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        text = findViewById(R.id.txt);
        input = findViewById(R.id.edtinput);
        btnSend = findViewById(R.id.btnsend);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(9000);

                    log("Waiting for Client !!!");
                   //
                    wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    int a = wifiInfo.getIpAddress();
                    String sa = Formatter.formatIpAddress(a) + "";
                    log(sa);
                    //
                   socket = serverSocket.accept();
                   log("A New Client Connected !!!");

                    outputStream = new DataOutputStream(socket.getOutputStream());
                    inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    log("Buffers initialized !!!");
//                    outputStream.write("Hello my client :))\n".getBytes());

                    while (true){
                        String message = inputStream.readLine();
                        log(message);
                    }

                } catch (IOException e) {
                    log("Error: IO Exeption !!!");
                    e.printStackTrace();
                }
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(outputStream == null){
                    return;
                }
                    try {
                        String message = input.getText().toString()+"\n";
                        outputStream.write(message.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        });
        thread.start();
    }
    private void log(final String message){
        Long timeStamp = System.currentTimeMillis();
        final Long time = timeStamp %100000;
        handler.post(new Runnable() {
            @Override
            public void run() {
                text.setText(text.getText()+"\n@"+time+":"+message);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
