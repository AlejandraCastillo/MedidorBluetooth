package com.ake.medidorbluetooth.buetooth_utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

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

    //Listas de dispositivos
    private final ArrayList<BluetoothDevice> bondedDevices = new ArrayList<>();
    private final ArrayList<BluetoothDevice> discoveryDevices = new ArrayList<>();

    public static final int BONDED_DEVICES = 1;
    public static final int DISCOVERY_DEVICES=2;

    public BluetoothUtils(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        adapterBondedDevices = new RecycleViewBluetoothAdapter(bondedDevices, this);
        adapterDiscoveryDevices = new RecycleViewBluetoothAdapter(discoveryDevices, this);
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
            adapterBondedDevices.add(device);
        }
        adapterBondedDevices.notifyDataSetChanged();
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

    public boolean deviceAlreadyExists(BluetoothDevice device){
        for (BluetoothDevice d:bondedDevices) {
            if(device.getAddress().equals(d.getAddress()))
                return true;
        }
        for (BluetoothDevice d:discoveryDevices) {
            if(device.getAddress().equals(d.getAddress()))
                return true;
        }
        return false;
    }

    public void addNewDevice(BluetoothDevice device){
        if(deviceAlreadyExists(device)) {
            discoveryDevices.add(device);
            adapterDiscoveryDevices.add(device);
            adapterDiscoveryDevices.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(BluetoothDevice device) {
        //Crear conexión
        bluetoothAdapter.cancelDiscovery();
        connectAsyncTask = new ConnectAsyncTask(device, context);
        connectAsyncTask.execute();
    }

}




























