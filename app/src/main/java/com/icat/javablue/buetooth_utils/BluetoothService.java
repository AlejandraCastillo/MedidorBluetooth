package com.icat.javablue.buetooth_utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.icat.javablue.R;
import java.util.ArrayList;
import java.util.Set;

/**
  * Esta clase administra el servicio e Bluetooth
  * @author: María Alejandra Castillo Martínez
  * */
public class BluetoothService {
    private static final String TAG = "BluetoothService";

    //Servicios
    private BluetoothAdapter bluetoothAdapter;
    private ConnectService clientConnection;

    //Listas de dispositivos
    private final ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
    private final ArrayList<BluetoothDevice> discoveryDevices = new ArrayList<>();

    //Parametros
    private Context context;
    public ArrayAdapter<String> adapterPairedDevices;
    public ArrayAdapter<String> adapterDiscoveryDevices;

    //Constantes para las listas de dispositivos
    public static final int PAIRED_DEVICES = 1;
    public static final int DISCOVERY_DEVICES=2;

    //Estados del adaptador de BT
    public static final int BLUETOOTH_ADAPTER_DONT_EXIST = 0;
    public static final int BLUETOOTH_ADAPTER_IS_DISABLE = 1;
    public static final int BLUETOOTH_ADAPTER_IS_ENABLE=2;


    public BluetoothService(Context context){
        adapterPairedDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        adapterDiscoveryDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    public final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    discoveryDevices.add(device);
                    adapterDiscoveryDevices.add(device.getName());
                    adapterDiscoveryDevices.notifyDataSetChanged();
                    Log.i(TAG, "Dispositivo " + device.getName() + " encontrado");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.i(TAG, "Start Discovery");
                    Toast.makeText(context, R.string.startSearch, Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i(TAG, "Finish Discovery");
                    Toast.makeText(context, R.string.finishSearch, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public IntentFilter getBroadcastIntent() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        return filter;
    }

    public int bluetoothAdapterState(){
        if(bluetoothAdapter == null)
            return BLUETOOTH_ADAPTER_DONT_EXIST;
        if (!bluetoothAdapter.isEnabled())
            return BLUETOOTH_ADAPTER_IS_DISABLE;
        return BLUETOOTH_ADAPTER_IS_ENABLE;
    }

    public boolean getPairedDevices(){
        Set<BluetoothDevice> sPairedDevices = bluetoothAdapter.getBondedDevices();
        adapterPairedDevices.clear();
        pairedDevices.clear();
        for(BluetoothDevice device : sPairedDevices){
            pairedDevices.add(device);
            adapterPairedDevices.add(device.getName());
            adapterPairedDevices.notifyDataSetChanged();
        }
        //Si existen dispositivos
        return pairedDevices.size() > 0;
    }

    public void startDiscovery(){
        bluetoothAdapter.cancelDiscovery();
        adapterDiscoveryDevices.clear();
        discoveryDevices.clear();
        bluetoothAdapter.startDiscovery();
    }

    public BluetoothDevice getDevice(int list, int position){
        BluetoothDevice device;
        switch (list){
            case PAIRED_DEVICES:
                device = pairedDevices.get(position);
                break;
            case DISCOVERY_DEVICES:
                device = discoveryDevices.get(position);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + list);
        }
        return device;
    }

    public void cancelDiscovery(){
        bluetoothAdapter.cancelDiscovery();
    }

    public void createConection(BluetoothDevice device){
        bluetoothAdapter.cancelDiscovery();
        clientConnection = new ConnectService(device, context);
        clientConnection.execute();
    }

}

