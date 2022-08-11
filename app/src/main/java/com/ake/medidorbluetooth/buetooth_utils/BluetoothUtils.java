package com.ake.medidorbluetooth.buetooth_utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ake.medidorbluetooth.MessageReceiverActivity;
import com.ake.medidorbluetooth.recycleview_bluetooth.OnClickListenerBluetooth;
import com.ake.medidorbluetooth.recycleview_bluetooth.RecycleViewBluetoothAdapter;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothUtils implements OnClickListenerBluetooth {
    private static final String TAG = "BluetoothUtils";

    private BluetoothAdapter bluetoothAdapter;
    private ConnectAsyncTask connectAsyncTask;
    private Context context;

    public RecycleViewBluetoothAdapter adapterBondedDevices;
    public RecycleViewBluetoothAdapter adapterDiscoveryDevices;

    public static final int BONDED_DEVICES = 1;
    public static final int DISCOVERY_DEVICES=2;

    public BluetoothUtils(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        adapterBondedDevices = new RecycleViewBluetoothAdapter(this);
        adapterDiscoveryDevices = new RecycleViewBluetoothAdapter(this);
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
        for(BluetoothDevice device : sBondedDevices){
            adapterBondedDevices.add(device);
        }
//        adapterBondedDevices.notifyDataSetChanged();
        //True si existen dispositivos
        return adapterBondedDevices.getItemCount() > 0;
    }

    public void discovery(boolean discoveryFlag){
        if(!discoveryFlag){
            adapterDiscoveryDevices.clear();
            bluetoothAdapter.startDiscovery();
        }
        else{
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public boolean deviceAlreadyExists(BluetoothDevice device){
        for (BluetoothDevice bondedD:adapterBondedDevices.list) {
            if(device.getAddress().equals(bondedD.getAddress())) {
                Log.i(TAG, "onReceive: El dispositivo " + device.getName() + " ya existe en adapterBondedDevices");
                return true;
            }
        }
        for (BluetoothDevice discoveryD: adapterDiscoveryDevices.list) {
            if(device.getAddress().equals(discoveryD.getAddress())) {
                Log.i(TAG, "onReceive: El dispositivo " + device.getName() + " ya existe en adapterDiscoveryDevices");
                return true;
            }
        }
        return false;
    }

    public void addNewDevice(BluetoothDevice device){
        if(!deviceAlreadyExists(device)) {
            adapterDiscoveryDevices.add(device);
//            adapterDiscoveryDevices.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(BluetoothDevice device) {
        //Crear conexión
        bluetoothAdapter.cancelDiscovery();
        Intent intent = new Intent(context, MessageReceiverActivity.class);
        context.startActivity(intent);
    }

}




























