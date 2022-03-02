package com.ake.medidorbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.ake.medidorbluetooth.buetooth_utils.BluetoothService;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Objects;

public class ConnectActivity extends AppCompatActivity {
    private static final String TAG = "ConnectActivity";

    private BluetoothService bluetoothService;

    private SwitchCompat swBluettoth;
    private ProgressBar progressBar;

    private static final int REQUEST_COARSE_LOC = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Objects.requireNonNull(getSupportActionBar()).setTitle(TAG);

        swBluettoth = findViewById(R.id.sw_enable_bt);
        progressBar = findViewById(R.id.progress_bar);

        bluetoothService = new BluetoothService(this);

        //Pedir permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOC);
        }

        //Verificar el estado del bluetppth
        if(bluetoothService.bluetoothAdapterIsEnable()){
            swBluettoth.setChecked(true);
        }
        else{
            swBluettoth.setChecked(false);
        }

        //Switch
        swBluettoth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    bluetoothService.enableBluetoothAdapter();
                }
                else{
                    bluetoothService.disableBluetoothAdapter();
                }
            }
        });

        //Registrar el broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);


    }

    public final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    bluetoothStateManager(intent);
                    break;
            }

        }
    };

    private void bluetoothStateManager(Intent intent){
        int state = intent.getIntExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR);

        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                progressBar.setVisibility(View.INVISIBLE);
                swBluettoth.setChecked(false);
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case BluetoothAdapter.STATE_ON:
                progressBar.setVisibility(View.INVISIBLE);
                swBluettoth.setChecked(true);
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                progressBar.setVisibility(View.VISIBLE);
                break;
        }

    }

    public void onClickCancelar(View view) {
//        usar banderas dentro del broadcast
    }

    public void onClickBuscar(View view) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        //Se elimina el resgistro del broadcast al destruir el Activity
        unregisterReceiver(receiver);
    }

}