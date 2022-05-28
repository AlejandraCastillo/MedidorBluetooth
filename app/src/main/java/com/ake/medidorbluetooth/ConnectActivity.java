package com.ake.medidorbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.ake.medidorbluetooth.buetooth_utils.BluetoothService;

import java.util.Objects;

public class ConnectActivity extends AppCompatActivity {
    private static final String TAG = "ConnectActivity";

    private BluetoothService bluetoothService;

    private SwitchCompat swBluettoth;
    private RecyclerView rvBondedDevices;
    private ProgressBar progressBar;
    private ProgressBar pbBluetooth;
    private Button buttonBucar;

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;

    private boolean discoveryFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Objects.requireNonNull(getSupportActionBar()).setTitle(TAG);

        swBluettoth = findViewById(R.id.sw_enable_bt);
        progressBar = findViewById(R.id.progress_bar);
        pbBluetooth = findViewById(R.id.pb_buetooth);
        rvBondedDevices = findViewById(R.id.rv_bondedDevices);
        RecyclerView rvDiscoveryDevices = findViewById(R.id.rv_discoveryDevices);
        buttonBucar = findViewById(R.id.b_Busqueda);

        bluetoothService = new BluetoothService(this);

        LinearLayoutManager bondedDevicesmanager = new LinearLayoutManager(this);
        rvBondedDevices.setLayoutManager(bondedDevicesmanager);
        rvBondedDevices.setAdapter(bluetoothService.adapterBondedDevices);

        LinearLayoutManager discoveryDevicesmanager = new LinearLayoutManager(this);
        rvDiscoveryDevices.setLayoutManager(discoveryDevicesmanager);
        rvDiscoveryDevices.setAdapter(bluetoothService.adapterDiscoveryDevices);

        //Verificar el estado del bluetooth
        if(bluetoothService.bluetoothAdapterIsEnable()){
            swBluettoth.setChecked(true);
            bluetoothService.getBondedDevices();
        }

        //Switch
        swBluettoth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    pbBluetooth.setVisibility(View.VISIBLE);
                    swBluettoth.setVisibility(View.INVISIBLE);
                    bluetoothService.enableBluetoothAdapter();
                }
                else {
                    pbBluetooth.setVisibility(View.VISIBLE);
                    swBluettoth.setVisibility(View.INVISIBLE);
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
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i(TAG, "onReceive: Iniciar busqueda");
                    discoveryFlag = true;
                    buttonBucar.setText(R.string.cancelar_busqueda);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "onReceive: Dispositivo " + device.getName() + device.getAddress() + " encontrado");
                    if(!bluetoothService.deviceAlreadyExists(device))
                        bluetoothService.addNewDevice(device);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i(TAG, "onReceive: Cancelar busqueda");
                    discoveryFlag = false;
                    progressBar.setVisibility(View.INVISIBLE);
                    buttonBucar.setText(R.string.inciar_busqueda);
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
                swBluettoth.setChecked(true);
                if(bluetoothService.getBondedDevices())
                    rvBondedDevices.setAdapter(bluetoothService.adapterBondedDevices);
                pbBluetooth.setVisibility(View.INVISIBLE);
                swBluettoth.setVisibility(View.VISIBLE);
                break;
            case BluetoothAdapter.STATE_OFF:
                swBluettoth.setChecked(false);
                bluetoothService.adapterBondedDevices.clear();
                bluetoothService.adapterDiscoveryDevices.clear();
                pbBluetooth.setVisibility(View.INVISIBLE);
                swBluettoth.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void askForPermission(){
        //Permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void onClickBuscar(View view) {
        askForPermission();
        bluetoothService.discovery(discoveryFlag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        //Se elimina el resgistro del broadcast al destruir el Activity
        unregisterReceiver(receiver);
    }

}