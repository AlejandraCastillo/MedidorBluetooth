package com.ake.medidorbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import com.ake.medidorbluetooth.Servicio.MyService;
import com.ake.medidorbluetooth.custom_gauge.CustomGauge;
import com.ake.medidorbluetooth.database.SQLiteActions;
import java.util.Objects;

public class MessageReceiverActivity extends AppCompatActivity {
    private static final String TAG = "MessageReceiver";

    public static TextView tvVoltage;
    public static TextView tvCorriente;
    public static TextView tvPotencia;
    public static TextView tvEnergia;

    public static CustomGauge gaugeVoltaje;
    public static CustomGauge gaugeCorriente;
    public static CustomGauge gaugePotencia;
    public static CustomGauge gaugeEnergia;

    Intent serviceIntent;
    boolean isBounded;
    MyService myService;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_receiver);

        Log.d(TAG, "onCreate");

        tvVoltage = findViewById(R.id.tv_voltaje);
        tvCorriente = findViewById(R.id.tv_corriente);
        tvPotencia = findViewById(R.id.tv_potencia);
        tvEnergia = findViewById(R.id.tv_energia);

        gaugeVoltaje = findViewById(R.id.gauge_voltaje);
        gaugeCorriente = findViewById(R.id.gauge_corriente);
        gaugePotencia = findViewById(R.id.gauge_potencia);
        gaugeEnergia = findViewById(R.id.gauge_energia);

        SQLiteActions actions = new SQLiteActions(this);
        int registro = actions.getLastRegister() + 1;
        Objects.requireNonNull(getSupportActionBar()).setTitle("Registro: " + registro + "   Fecha: " + actions.getDate("dd/MM/yy"));

        myService = new MyService();
        serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unbindService(connection);
        stopService(serviceIntent);
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
            myService = binder.getService();
            isBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBounded = false;
        }
    };

}