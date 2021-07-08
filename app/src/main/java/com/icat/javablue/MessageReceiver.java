package com.icat.javablue;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
public class MessageReceiver extends AppCompatActivity {
    // Debugging
    private static final String TAG = "MessageReceiver";


    private static final float MAX_NUM = 24;
    private static final float MAX_ANGLE = 180;
    private static final float STEP = MAX_ANGLE/MAX_NUM;

    View manecilla;
    BluetoothSocket socket;
    TextView tvDeviceName;
    TextView tvMessages;

    ConnectedThread msgReceiver;

    //Constantes para los mensajes
    public static final int MESSAGE_READ = 0;
    public static final int CONNECTION_LOST = 1;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MESSAGE_READ:
                    String readMessage = (String) msg.obj;
                    String numeros = readMessage.substring(0, readMessage.indexOf('-'));
//                    adapterMessages.add(numeros);
//                    String letras = readMessage.substring(readMessage.indexOf('-') + 1,readMessage.length());
//                    adapterMessages.add(letras);

                    int num1 = Integer.parseInt(numeros.substring(0,1));
                    int num2 = Integer.parseInt(numeros.substring(1,2));
                    int num3 = Integer.parseInt(numeros.substring(2,3));
                    int num = num1+num2+num3;
                    manecilla.setRotation(num*STEP);
                    tvMessages.setText(String.valueOf(num));

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_receiver);

        tvDeviceName = findViewById(R.id.tv_deviceName);
        tvDeviceName.setText(getIntent().getStringExtra("name"));

        tvMessages = findViewById(R.id.tv_messages);
        manecilla = findViewById(R.id.v_manecilla);

        socket = ShareSocket.getSocket();

        msgReceiver = new ConnectedThread(socket, handler);
        msgReceiver.start();



    }

    @Override
    public void finish() {
        super.finish();
        msgReceiver.cancel();
    }

    public void onClickDisconnect(View view) {
        finish();
//        msgReceiver.cancel();
    }


    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final Handler mHandler;
        private final InputStream mmInStream;
        private final BufferedReader mmBuffer;

        public ConnectedThread(BluetoothSocket socket, Handler handler) {
            mmSocket = socket;
            mHandler = handler;
            InputStream tmpIn = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error al crear el input stream", e);
            }

            mmInStream = tmpIn;
            mmBuffer = new BufferedReader(new InputStreamReader(mmInStream));
        }

        public void run() {
            String line;
            String regex = "[0-9]{3}-[a-zA-Z]{3}";

            while (mmSocket.isConnected()) {
                try {
                    if((line = mmBuffer.readLine()) != null && line.matches(regex)){
                        mHandler.obtainMessage(MESSAGE_READ, line)
                                .sendToTarget();
                    }
                } catch (IOException e) {
                    Log.d(TAG, "Conexion perdida", e);
                    mHandler.obtainMessage(CONNECTION_LOST)
                            .sendToTarget();
                    break;
                }
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
                Log.i(TAG, "Se ha cerrado el socket");
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSocket.closeSocket();
    }
}


