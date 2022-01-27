package com.icat.javablue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import android.widget.ProgressBar;

import com.icat.javablue.buetooth_utils.BluetoothService;

import java.util.Objects;

/**
 *  Este activity será empleado para la busqueda de dispositivos buetooth
 *  y para solicitar la conección a los mismos.
 * @author: María Alejandra Castillo Martínez
 */
public class ConnectActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "ConnectActivity";

    //Views
    private ListView lvAvailableDevices;
    private ListView lvPairedDevices;
    private ProgressBar progressBar;
    private SwitchCompat sw_enableBt;

    //Bluetooth
    private BluetoothService bluetoothService;

    //Request
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_COARSE_LOC = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Conectar");

        //Inicializar las Views
        lvPairedDevices = findViewById(R.id.lv_pairedDevices);
        lvAvailableDevices = findViewById(R.id.lv_availableDevices);
        progressBar = findViewById(R.id.progress_bar);
        sw_enableBt = findViewById(R.id.sw_enable_bt);

        //Adaptador de Bluetooth
        bluetoothService = new BluetoothService(this);
        switch (bluetoothService.bluetoothAdapterState()) {
            case BluetoothService.BLUETOOTH_ADAPTER_DOES_NOT_EXIST:
                Intent intent0 = new Intent(this, NoBluetoothActivity.class);
                startActivity(intent0);
                break;
            case BluetoothService.BLUETOOTH_ADAPTER_IS_DISABLED:
                Intent intent1 = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent1, REQUEST_ENABLE_BT);
                break;
            case BluetoothService.BLUETOOTH_ADAPTER_IS_ENABLE:
                //Obtener la lista de dispositivos sincronizados
                if (bluetoothService.getPairedDevices())
                    lvPairedDevices.setAdapter(bluetoothService.adapterPairedDevices);
                break;
        }

        //Obtener otros permisos necesarios
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOC);
        }

        //Seleccionar un dispositivo de la lista de dispositivos sincronizados
        lvPairedDevices.setOnItemClickListener((parent, view, position, id) ->
                bluetoothService.createConection(bluetoothService.getDevice(
                        BluetoothService.PAIRED_DEVICES, position)));

        //Seleccionar un dispositivo de la lista de dispositivos encontrados
        lvAvailableDevices.setOnItemClickListener((parent, view, position, id) ->
                bluetoothService.createConection(bluetoothService.getDevice(
                        BluetoothService.DISCOVERY_DEVICES, position)));

        //Registrar el broadcast del bluetoothService
        IntentFilter filter = bluetoothService.getBroadcastIntent();
        registerReceiver(bluetoothService.receiver, filter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //Obtener la lista de dispositivos sincronizados
            if (bluetoothService.getPairedDevices()) {
                lvPairedDevices.setAdapter(bluetoothService.adapterPairedDevices);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_download, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.m_download:
                intent = new Intent(this, DownloadActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickSearch(View view) {
        bluetoothService.startDiscovery();
        lvAvailableDevices.setAdapter(bluetoothService.adapterDiscoveryDevices);
    }


    public void onClickCancel(View view) {
        bluetoothService.cancelDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        //Se elimina el resgistro del broadcast al destruir el Activity
        unregisterReceiver(bluetoothService.receiver);
    }
}