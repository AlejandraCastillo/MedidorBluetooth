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

//    private ConnectedThread msgReceiver;
    private SQLiteActions actions;
    private int grupo;

    public static final int MESSAGE_READ = 0;
    public static final int CONNECTION_LOST = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_receiver);

        tvVoltage = findViewById(R.id.tv_voltaje);
        tvCorriente = findViewById(R.id.tv_corriente);
        tvPotencia = findViewById(R.id.tv_potencia);
        tvEnergia = findViewById(R.id.tv_energia);

        gaugeVoltaje = findViewById(R.id.gauge_voltaje);
        gaugeCorriente = findViewById(R.id.gauge_corriente);
        gaugePotencia = findViewById(R.id.gauge_potencia);
        gaugeEnergia = findViewById(R.id.gauge_energia);

        actions = new SQLiteActions(this);
        grupo = 0;
        Objects.requireNonNull(getSupportActionBar()).setTitle("Registro: " + grupo + "   Fecha: " + actions.getDate("dd-MM-yyyy"));

        double v = 1.1;
        double c = 2.2;
        double p = 3.3;
        double e = 4.4;

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
    }

}