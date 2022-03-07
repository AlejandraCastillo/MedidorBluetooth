package com.ake.medidorbluetooth.buetooth_utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothService {
    private static final String TAG = "BluetoothService";

    private BluetoothAdapter bluetoothAdapter;
    private Context context;

    public ArrayAdapter<String> adapterBondedDevices;
    public ArrayAdapter<String> adapterDiscoveryDevices;

    //Listas de dispositivos
    private final ArrayList<BluetoothDevice> bondedDevices = new ArrayList<>();
    private final ArrayList<BluetoothDevice> discoveryDevices = new ArrayList<>();

    public BluetoothService(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        adapterBondedDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        adapterDiscoveryDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
    }

    public boolean bluetoothAdapterExist(){
        return bluetoothAdapter != null;
    }

    public boolean bluetoothAdapterIsEnable(){
        return bluetoothAdapter.isEnabled();
    }

    public void disableBluetoothAdapter(){
        bluetoothAdapter.disable();
    }

    public void enableBluetoothAdapter(){
        bluetoothAdapter.enable();
    }

    public boolean getBondedDevices(){
        Set<BluetoothDevice> sBondedDevices = bluetoothAdapter.getBondedDevices();
        adapterBondedDevices.clear();
        bondedDevices.clear();
        for(BluetoothDevice device : sBondedDevices){
            bondedDevices.add(device);
            adapterBondedDevices.add(device.getName());
            adapterBondedDevices.notifyDataSetChanged();
        }
        //True si existen dispositivos
        return bondedDevices.size() > 0;
    }

    public void discovery(boolean discoveryFlag){
        if(!discoveryFlag){
            discoveryDevices.clear();
            adapterDiscoveryDevices.clear();
            bluetoothAdapter.startDiscovery();
        }
        else{
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public void addNewDevice(BluetoothDevice device){
        discoveryDevices.add(device);
        adapterDiscoveryDevices.add(device.getName());
        adapterDiscoveryDevices.notifyDataSetChanged();
    }


}




























