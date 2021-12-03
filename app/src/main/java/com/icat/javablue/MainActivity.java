package com.icat.javablue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.icat.javablue.buetooth_utils.BluetoothService;

public class MainActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "MainActivity";

    //Views
    private ListView lvDiscoveryDevices;
    private ListView lvPairedDevices;

    //Bluetooth
    private BluetoothService bluetoothService;

    //Request
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_COARSE_LOC = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Conectar");

        //Inicializar las Views
        lvPairedDevices = findViewById(R.id.lv_pairedDevices);
        lvDiscoveryDevices = findViewById(R.id.lv_discoveryDevices);

        //Adaptador de Bluetooth
        bluetoothService = new BluetoothService(this);
        switch (bluetoothService.bluetoothAdapterState()) {
            case BluetoothService.BLUETOOTH_ADAPTER_DONT_EXIST:
                Intent intent0 = new Intent(this, NoBluetooth.class);
                startActivity(intent0);
                break;
            case BluetoothService.BLUETOOTH_ADAPTER_IS_DISABLE:
                Intent intent1 = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent1, REQUEST_ENABLE_BT);
                break;
        }

        //Obtener otros permisos necesarios
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOC);
        }

        //Obtener la lista de dispositivos sincronizados
        if (bluetoothService.getPairedDevices()) {
            lvPairedDevices.setAdapter(bluetoothService.adapterPairedDevices);
        }

        //Seleccionar un dispositivo de la lista de dispositivos sincronizados
        lvPairedDevices.setOnItemClickListener((parent, view, position, id) ->
                bluetoothService.createConection(bluetoothService.getDevice(
                        BluetoothService.PAIRED_DEVICES, position)));

        //Seleccionar un dispositivo de la lista de dispositivos encontrados
        lvDiscoveryDevices.setOnItemClickListener((parent, view, position, id) ->
                bluetoothService.createConection(bluetoothService.getDevice(
                        BluetoothService.DISCOVERY_DEVICES, position)));

        //Registrar el broadcast del bluetoothService
        IntentFilter filter = bluetoothService.getBroadcastIntent();
        registerReceiver(bluetoothService.receiver, filter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_descargar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.m_descargar:
                intent = new Intent(this, Download.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickSearch(View view) {
        bluetoothService.startDiscovery();
        lvDiscoveryDevices.setAdapter(bluetoothService.adapterDiscoveryDevices);
    }


    public void onClickCancel(View view) {
        bluetoothService.cancelDiscovery();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "OnResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        //Se elimina el resgistro del broadcast al destruir el Activity
        unregisterReceiver(bluetoothService.receiver);
    }
}