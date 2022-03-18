package com.ake.medidorbluetooth;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.ake.medidorbluetooth.buetooth_utils.ShareSocket;
import com.ake.medidorbluetooth.custom_gauge.CustomGauge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Objects;

public class MessageReceiverActivity extends AppCompatActivity {
    private static final String TAG = "MessageReceiver";

    private CustomGauge gaugeVoltaje;
    private CustomGauge gaugeCorriente;
    private CustomGauge gaugePotencia;
    private CustomGauge gaugeEnergia;

    private TextView tvVoltage;
    private TextView tvCorriente;
    private TextView tvPotencia;
    private TextView tvEnergia;

    private ConnectedThread msgReceiver;

    public static final int MESSAGE_READ = 0;
    public static final int CONNECTION_LOST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_receiver);
        Objects.requireNonNull(getSupportActionBar()).hide();

        BluetoothSocket socket = ShareSocket.getSocket();

        gaugeVoltaje = findViewById(R.id.gauge_voltaje);
        gaugeCorriente = findViewById(R.id.gauge_corriente);
        gaugePotencia = findViewById(R.id.gauge_potencia);
        gaugeEnergia = findViewById(R.id.gauge_energia);

        tvVoltage = findViewById(R.id.tv_voltaje);
        tvCorriente = findViewById(R.id.tv_corriente);
        tvPotencia = findViewById(R.id.tv_potencia);
        tvEnergia = findViewById(R.id.tv_energia);

        msgReceiver = new ConnectedThread(socket, handler);
        msgReceiver.start();

    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg){
            switch (msg.what){
                case MESSAGE_READ:
                    String readMessage = (String) msg.obj;

                    int index = readMessage.indexOf("::");
                    String aux = readMessage.substring(index + 2);
                    index = aux.indexOf("::");
                    aux = aux.substring(0, index);

                    //Tiempo
                    index = aux.indexOf('-');
                    int t = Integer.parseInt(aux.substring(0, index));
                    aux = aux.substring(index + 1);

                    //Voltage
                    index = aux.indexOf('-');
                    double v = Double.parseDouble(aux.substring(0, index));
                    aux = aux.substring(index + 1);

                    //Corriente
                    index = aux.indexOf('-');
                    double c = Double.parseDouble(aux.substring(0, index));
                    aux = aux.substring(index + 1);

                    //Potencia
                    index = aux.indexOf('-');
                    double p = Double.parseDouble(aux.substring(0, index));

                    //Energia
                    double e = Double.parseDouble(aux.substring(index + 1));

                    Log.i(TAG, "t = " + t + " v = " + v + " c = " + c + " p = " + p + " e = " + e);

                    gaugeVoltaje.setValue(v);
                    gaugeCorriente.setValue(c);
                    gaugePotencia.setValue(p);
                    gaugeEnergia.setValue(e);

                    DecimalFormat formato = new DecimalFormat("00.00");
                    tvVoltage.setText(formato.format(v) + " V");

                    formato = new DecimalFormat("0.00");
                    tvCorriente.setText(formato.format(c) + " A");
                    tvEnergia.setText(formato.format(e) + " J");

                    formato = new DecimalFormat("000.00");
                    tvPotencia.setText(formato.format(p) + " W");

                    break;

                case CONNECTION_LOST:
                    Log.i(TAG, "Conexion perdida");
                    finish();
            }

        }
    };

    public static class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final Handler mmHandler;
        private final BufferedReader mmBuffer;

        public ConnectedThread(BluetoothSocket socket, Handler handler){
            mmSocket = socket;
            mmHandler = handler;
            InputStream tmpInStream = null;

            try{
                tmpInStream = socket.getInputStream();
            } catch (IOException e){
                Log.e(TAG, "Error al crear el input stream", e);
            }

            InputStream mmInStream = tmpInStream;
            mmBuffer = new BufferedReader(new InputStreamReader(mmInStream));
        }

        public void run(){
            String line;
            String regex = "1?2?3::\\d*(-\\d*\\.\\d*){4}::456";

            while (mmSocket.isConnected()){
                try {
                    line = mmBuffer.readLine().replaceAll("\\s", "");
                    if(line.matches(regex)){
                        mmHandler.obtainMessage(MESSAGE_READ, line)
                                 .sendToTarget();
                    }
                } catch (IOException e){
//                    Log.e(TAG, "Conexion perdida", e);
                    mmHandler.obtainMessage(CONNECTION_LOST)
                             .sendToTarget();
                    break;
                }
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
                Log.i(TAG, "Se ha cerrado el socket");
            } catch (IOException e){
                Log.e(TAG, "No se pudo cerrar el socket", e);
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