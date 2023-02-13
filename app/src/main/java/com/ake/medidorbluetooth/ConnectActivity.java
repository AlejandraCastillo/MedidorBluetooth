package com.ake.medidorbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ake.medidorbluetooth.buetooth_utils.BluetoothUtils;

import java.util.Objects;

public class ConnectActivity extends AppCompatActivity {
    private static final String TAG = "ConnectActivity";

    private BluetoothUtils bluetoothUtils;

    private SwitchCompat swBluetooth;
    private RecyclerView rvBondedDevices;
    private ProgressBar progressBar;
    private ProgressBar pbBluetooth;
    private Button buttonBuscar;

    private boolean discoveryFlag = false;
    private boolean enableBTFlag = false; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Bluetooth");

        swBluetooth = findViewById(R.id.sw_enable_bt);
        progressBar = findViewById(R.id.progress_bar);
        pbBluetooth = findViewById(R.id.pb_buetooth);
        rvBondedDevices = findViewById(R.id.rv_bondedDevices);
        RecyclerView rvDiscoveryDevices = findViewById(R.id.rv_discoveryDevices);
        buttonBuscar = findViewById(R.id.b_Busqueda);

        bluetoothUtils = new BluetoothUtils( this, this);

        LinearLayoutManager bondedDevicesManager = new LinearLayoutManager(this);
        rvBondedDevices.setLayoutManager(bondedDevicesManager);
        rvBondedDevices.setAdapter(bluetoothUtils.adapterBondedDevices);

        LinearLayoutManager discoveryDevicesManager = new LinearLayoutManager(this);
        rvDiscoveryDevices.setLayoutManager(discoveryDevicesManager);
        rvDiscoveryDevices.setAdapter(bluetoothUtils.adapterDiscoveryDevices);

        //Verificar el estado del bluetooth
        if(bluetoothUtils.bluetoothAdapterIsEnable()){
            swBluetooth.setChecked(true);
            enableBTFlag=true; 
            bluetoothUtils.getBondedDevices();
        }

        //Switch
        swBluetooth.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked) {
                pbBluetooth.setVisibility(View.VISIBLE);
                swBluetooth.setVisibility(View.INVISIBLE);
                bluetoothUtils.enableBluetoothAdapter();
            }
            else {
                pbBluetooth.setVisibility(View.VISIBLE);
                swBluetooth.setVisibility(View.INVISIBLE);
                bluetoothUtils.disableBluetoothAdapter();
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
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i(TAG, "onReceive: Iniciar busqueda");
                    discoveryFlag = true;
                    buttonBuscar.setText(R.string.cancelar_busqueda);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        bluetoothUtils.checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
                    }
                    Log.i(TAG, "onReceive: Dispositivo " + device.getName() + " encontrado");
                    bluetoothUtils.addNewDevice(device);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i(TAG, "onReceive: Cancelar busqueda");
                    discoveryFlag = false;
                    progressBar.setVisibility(View.INVISIBLE);
                    buttonBuscar.setText(R.string.inciar_busqueda);
                    break;
            }

        }
    };

    private void bluetoothStateManager(Intent intent){
        int state = intent.getIntExtra(
                BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR);

        switch (state) {
            case BluetoothAdapter.STATE_ON:
                swBluetooth.setChecked(true); //Si el estado del BT es cambiado fuera de la app
                if(bluetoothUtils.getBondedDevices())
                    rvBondedDevices.setAdapter(bluetoothUtils.adapterBondedDevices);
                pbBluetooth.setVisibility(View.INVISIBLE);
                swBluetooth.setVisibility(View.VISIBLE);
                enableBTFlag=true; 
                break;
            case BluetoothAdapter.STATE_OFF:
                swBluetooth.setChecked(false);
                bluetoothUtils.adapterBondedDevices.clear();
                bluetoothUtils.adapterDiscoveryDevices.clear();
                pbBluetooth.setVisibility(View.INVISIBLE);
                swBluetooth.setVisibility(View.VISIBLE);
                enableBTFlag=false; 
                break;
        }

    }

    public void onClickBuscar(View view) {
        if(enableBTFlag){
            bluetoothUtils.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            bluetoothUtils.discovery(discoveryFlag);
        }
        else{
            Toast.makeText(this, "El bluetooth está apagado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        //Se elimina el resgistro del broadcast al destruir el Activity
        unregisterReceiver(receiver);
    }

}