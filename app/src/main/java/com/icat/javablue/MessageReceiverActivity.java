package com.icat.javablue;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.icat.javablue.buetooth_utils.ShareSocket;
import com.icat.javablue.custom_gauge.CustomGauge;
import com.icat.javablue.database.SQLiteActions;
import com.icat.javablue.database.TablaDatos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * En esta Activity se reciben los datos mandados desde un dispositivo
 * Bluetooth externo, los datos se reflejan en la pantalla y, a su vez,
 * se almacenan en la base de datos.
 * @author: María Alejandra Castillo Martínez
 */
public class MessageReceiverActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "MessageReceiver";

    //Views
    private CustomGauge gauge1;
    private CustomGauge gauge2;
    private CustomGauge gauge3;
    private TextView text1;
    private TextView text2;
    private TextView text3;

    //Bluetooth
    private BluetoothSocket socket;

    //DataBase
    private SQLiteActions actions;
    private int grupo;

    //Hilo
    private ConnectedThread msgReceiver;

    //Constantes
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

                    TablaDatos row = new TablaDatos();

                    //Tiempo
                    index = aux.indexOf('-');
                    row.setTiempo(Integer.valueOf(aux.substring(0, index)));
                    aux = aux.substring(index + 1);

                    //Voltaje
                    index = aux.indexOf('-');
                    row.setVoltaje(Double.valueOf(aux.substring(0, index)));
                    aux = aux.substring(index + 1);

                    //Corriente
                    index = aux.indexOf('-');
                    row.setCorriente(Double.valueOf(aux.substring(0, index)));
                    aux = aux.substring(index + 1);

                    //Potencia
                    index = aux.indexOf('-');
                    row.setPotencia(Double.valueOf(aux.substring(0, index)));

                    //Energia
                    row.setEnergia(Double.valueOf(aux.substring(0, index)));

                    row.setGrupoID(grupo);

                    actions.addNewRow(row);
//                    row.printRow(TAG);

                    DecimalFormat formato = new DecimalFormat("00.00");

                    gauge1.setValue(row.getVoltaje());
                    text1.setText(formato.format(row.getVoltaje()));
                    gauge2.setValue(row.getCorriente());
                    text2.setText(formato.format(row.getCorriente()));

                    formato = new DecimalFormat("000.00");
                    gauge3.setValue(row.getPotencia());
                    text3.setText(formato.format(row.getPotencia()));


                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_receiver);

        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        socket = ShareSocket.getSocket();

        gauge1 = findViewById(R.id.gauge1);
        text1  = findViewById(R.id.textView1);
        gauge2 = findViewById(R.id.gauge2);
        text2  = findViewById(R.id.textView2);
        gauge3 = findViewById(R.id.gauge3);
        text3  = findViewById(R.id.textView3);

        String ceros = "000.00";
        gauge1.setValue(0);
        text1.setText(ceros);
        gauge2.setValue(0);
        text2.setText(ceros);
        gauge3.setValue(0);
        text3.setText(ceros);

        actions = new SQLiteActions(this);

        grupo = actions.addNewGroup();

        msgReceiver = new ConnectedThread(socket, handler);
        msgReceiver.start();

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
    public void finish() {
        super.finish();
        msgReceiver.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSocket.closeSocket();
    }
}


