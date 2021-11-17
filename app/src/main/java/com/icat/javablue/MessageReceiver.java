package com.icat.javablue;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.icat.javablue.buetooth_utils.ShareSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MessageReceiver extends AppCompatActivity {
    // Debugging
    private static final String TAG = "MessageReceiver";


    private static final float MAX_ANGLE = 180;
    private float valorMaxEntrada;
    private float valorMaxSalida;
    private float unit;
    private float step;


    public ArrayAdapter<String> adapterMessages;

    BluetoothSocket socket;

    TextView tvDeviceName;
    ListView lvMessages;



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

                    int index = readMessage.indexOf("::");
                    String aux = readMessage.substring(index+2);
                    index = aux.indexOf("::");
                    aux = aux.substring(0, index);

                    //Tiempo
                    index = aux.indexOf('-');
                    String tiempo = aux.substring(0, index);
                    aux = aux.substring(index + 1);

                    //Voltaje
                    index = aux.indexOf('-');
                    String voltaje = aux.substring(0, index);
                    aux = aux.substring(index + 1);

                    //Corriente
                    index = aux.indexOf('-');
                    String corriente = aux.substring(0, index);
                    aux = aux.substring(index + 1);

                    //Potencia
                    index = aux.indexOf('-');
                    String potencia = aux.substring(0, index);

                    //Energia
                    String energia = aux.substring(index + 1);

                    String mensaje = "t=" + tiempo +  " v=" + voltaje + " I=" +  corriente + " P=" +  potencia + " E=" + energia;
                    Log.i(TAG, mensaje);

                    adapterMessages.add(mensaje);
                    adapterMessages.notifyDataSetChanged();

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

        lvMessages = findViewById(R.id.lv_messages);

        adapterMessages = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        lvMessages.setAdapter(adapterMessages);

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
            String regex = "1?2?3::\\d*(-\\d*\\.\\d*){4}::456";

            while (mmSocket.isConnected()) {
                try {
                    if((line = mmBuffer.readLine().replaceAll("\\s","")) != null && line.matches(regex)){
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


