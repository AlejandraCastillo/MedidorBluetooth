package com.icat.javablue;

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
import android.view.View;
import android.widget.ListView;

import com.icat.javablue.buetooth_utils.BluetoothService;

public class MainActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "MainActivity";

    private BluetoothService bluetoothService;

    //Constantes
    private static final int REQUEST_ENABLE_BT = 1, REQUEST_COARSE_LOC = 2;

    private ListView lvDiscoveryDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializar las Views
        //Views
        ListView lvPairedDevices = findViewById(R.id.lv_pairedDevices);
        lvDiscoveryDevices = findViewById(R.id.lv_discoveryDevices);

        //¿El adaptador existe?
        bluetoothService = new BluetoothService(this);
        if(!bluetoothService.bluetoothAdapterExist()){
            Intent intent = new Intent(this, NoBluetooth.class);
            startActivity(intent);
        }

        //Habilitar el adaptador
        if(!bluetoothService.bluetoothAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }

        //Obtener otros permisos necesarios
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOC);

        }

        //Obtener la lista de dispositivos sincronizados
        if(bluetoothService.getPairedDevices()){
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
        IntentFilter filter = bluetoothService.getBroadcastIntent(
                true, true, true);
        registerReceiver(bluetoothService.receiver, filter);


    }

    /* Metodo onClick para iniciar la búsqueda de dispositivos
    *  Este metodo refresca la lista de dispositivos sincronizados
    *  y actualiza la lista de dispositivos encontrados */
    public void onClickSearch(View view){
        bluetoothService.startDiscovery();
//        if(bluetoothService.getPairedDevices()){
//            lvPairedDevices.setAdapter(bluetoothService.adapterPairedDevices);
//        }
        lvDiscoveryDevices.setAdapter(bluetoothService.adapterDiscoveryDevices);
    }

    /* Metodo onClick para cancelar la búsqueda de dispositivos */
    public void onClickCancel(View view){
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