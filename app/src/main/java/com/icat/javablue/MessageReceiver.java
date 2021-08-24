package com.icat.javablue;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.icat.javablue.buetooth_utils.ShareSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessageReceiver extends AppCompatActivity {
    // Debugging
    private static final String TAG = "MessageReceiver";


    private static final float MAX_ANGLE = 180;
    private float valorMaxEntrada;
    private float valorMaxSalida;
    private float unit;
    private float step;

    View manecilla;
    BluetoothSocket socket;
    TextView tvDeviceName;
    TextView tvMessages;
    TextView tvValorMaximo;
    EditText edValMaxEnt;
    EditText edValMaxSal;



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
                    String numeros = readMessage.substring(0, readMessage.indexOf(' '));

//                    String letras = readMessage.substring(readMessage.indexOf('-') + 1,readMessage.length());

                    Log.v(TAG, readMessage);

                    float num = unit*(Float.valueOf(numeros));
                    if(num>valorMaxSalida) num=valorMaxSalida;
                    manecilla.setRotation(num* step);
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

        edValMaxEnt = findViewById(R.id.et_valMaxEnt);
        edValMaxSal = findViewById(R.id.et_valMaxSal);
        tvValorMaximo = findViewById(R.id.tv_valorMaximo);

        valorMaxEntrada = 127;
        valorMaxSalida = 5;
        unit = valorMaxSalida/valorMaxEntrada;
        step = MAX_ANGLE/valorMaxSalida;

        tvValorMaximo.setText(String.valueOf(valorMaxSalida));

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

    public void onClickSend(View view) {
        //Declarar las views
        edValMaxEnt = findViewById(R.id.et_valMaxEnt);
        edValMaxSal = findViewById(R.id.et_valMaxSal);
        tvValorMaximo = findViewById(R.id.tv_valorMaximo);

        //Obtener la informacion de los Edit Text
        String vme_aux = edValMaxEnt.getText().toString();
        String vms_aux = edValMaxSal.getText().toString();

        //Si no estan vacios
        if(!vme_aux.isEmpty())
            valorMaxEntrada = Float.parseFloat(vme_aux);
        if(!vms_aux.isEmpty())
            valorMaxSalida = Float.parseFloat(vms_aux);


        //Calcular valores necesarios
        unit = valorMaxSalida/valorMaxEntrada;
        step = MAX_ANGLE/valorMaxSalida;

        //Reajustar la numeracion
        tvValorMaximo.setText(String.valueOf(valorMaxSalida));

        //Limpiar los campos
        edValMaxEnt.setText("");
        edValMaxSal.setText("");
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
//            String regex = "[0-9]{3}-[a-zA-Z]{3}";

            while (mmSocket.isConnected()) {
                try {
//                    if((line = mmBuffer.readLine()) != null && line.matches(regex)){
//                        mHandler.obtainMessage(MESSAGE_READ, line)
//                                .sendToTarget();
//                    }
                    line = mmBuffer.readLine();
                    mHandler.obtainMessage(MESSAGE_READ, line).sendToTarget();

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


