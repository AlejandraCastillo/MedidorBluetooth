package com.ake.medidorbluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.ake.medidorbluetooth.Servicio.ConnectedThread;
import com.ake.medidorbluetooth.buetooth_utils.ShareSocket;
import com.ake.medidorbluetooth.custom_gauge.CustomGauge;
import com.ake.medidorbluetooth.database.SQLiteActions;
import com.ake.medidorbluetooth.database.TablaDatos;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Objects;

public class MessageReceiverActivity extends AppCompatActivity {
    private static final String TAG = "MessageReceiver";

    private TextView tvVoltage;
    private TextView tvCorriente;
    private TextView tvPotencia;
    private TextView tvEnergia;

    private CustomGauge gaugeVoltaje;
    private CustomGauge gaugeCorriente;
    private CustomGauge gaugePotencia;
    private CustomGauge gaugeEnergia;

    private ConnectedThread msgReceiver;
    private SQLiteActions actions;
    private int registro;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_receiver);
//        Objects.requireNonNull(getSupportActionBar()).setTitle(TAG);

        BluetoothSocket socket = ShareSocket.getSocket();

        tvVoltage = findViewById(R.id.tv_voltaje);
        tvCorriente = findViewById(R.id.tv_corriente);
        tvPotencia = findViewById(R.id.tv_potencia);
        tvEnergia = findViewById(R.id.tv_energia);

        gaugeVoltaje = findViewById(R.id.gauge_voltaje);
        gaugeCorriente = findViewById(R.id.gauge_corriente);
        gaugePotencia = findViewById(R.id.gauge_potencia);
        gaugeEnergia = findViewById(R.id.gauge_energia);

        actions = new SQLiteActions(this);
        registro = actions.addNewRegister();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Registro: " + registro + "   Fecha: " + actions.getDate("dd/MM/yy"));
        msgReceiver = new ConnectedThread(socket, handler);
        msgReceiver.start();
    }

    private final Handler handler = new Handler(Looper.myLooper()) {
//    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        @SuppressLint("SetTextI18n")
        public void handleMessage(@NotNull Message msg){
            switch (msg.what){
                case ConnectedThread.MESSAGE_READ:
                    String readMessage = (String) msg.obj;

                    int index = readMessage.indexOf("::");
                    String aux = readMessage.substring(index + 2);
                    index = aux.indexOf("::");
                    aux = aux.substring(0, index);

                    TablaDatos row = new TablaDatos();

                    //Tiempo
                    index = aux.indexOf('-');
                    int t = Integer.parseInt(aux.substring(0, index));
                    row.setTiempo(t);
                    aux = aux.substring(index + 1);

                    //Voltage
                    index = aux.indexOf('-');
                    double v = Double.parseDouble(aux.substring(0, index));
                    row.setVoltaje(v);
                    aux = aux.substring(index + 1);

                    //Corriente
                    index = aux.indexOf('-');
                    double c = Double.parseDouble(aux.substring(0, index));
                    row.setCorriente(c);
                    aux = aux.substring(index + 1);

                    //Potencia
                    index = aux.indexOf('-');
                    double p = Double.parseDouble(aux.substring(0, index));
                    row.setPotencia(p);

                    //Energia
                    double e = Double.parseDouble(aux.substring(index + 1));
                    row.setEnergia(e);

                    row.setRegistroID(registro);
                    actions.addnewDataRow(row);

                    row.printRow(TAG);

                    gaugeVoltaje.setValue(v);
                    gaugeCorriente.setValue(c);
                    gaugePotencia.setValue(p);
                    gaugeEnergia.setValue(e);

                    DecimalFormat formato = new DecimalFormat("00.00");
                    tvVoltage.setText(formato.format(v) + " V");

                    formato = new DecimalFormat("0.00");
                    tvCorriente.setText(formato.format(c) + " A");
                    tvEnergia.setText(formato.format(e) + " Wh");

                    formato = new DecimalFormat("000.00");
                    tvPotencia.setText(formato.format(p) + " W");

                    break;

                case ConnectedThread.CONNECTION_LOST:
                    Log.i(TAG, "Conexion perdida");
                    finish();
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        msgReceiver.cancel();
    }
}