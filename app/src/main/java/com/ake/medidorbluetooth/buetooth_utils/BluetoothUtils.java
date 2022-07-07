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
    private ConnectAsyncTask clientConnection;
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

        adapterBondedDevices = new RecycleViewBluetoothAdapter(bondedDevices, discoveryDevices, this);
        adapterDiscoveryDevices = new RecycleViewBluetoothAdapter(discoveryDevices, bondedDevices, this);
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

    public boolean deviceAlreadyExists(BluetoothDevice device){
        for (BluetoothDevice d:bondedDevices) {
            if(device.getAddress() == d.getAddress())
                return true;
        }
        for (BluetoothDevice d:discoveryDevices) {
            if(device.getAddress() == d.getAddress())
                return true;
        }
        return false;
    }

    public void addNewDevice(BluetoothDevice device){
        discoveryDevices.add(device);
        adapterDiscoveryDevices.add(device);
        adapterDiscoveryDevices.notifyDataSetChanged();
    }

    public void createConnection(BluetoothDevice device){
        bluetoothAdapter.cancelDiscovery();
        clientConnection = new ConnectAsyncTask(device, context);
        clientConnection.execute();
    }

    public BluetoothDevice getDevice(int list, int position){
        BluetoothDevice device;
        switch (list){
            case BONDED_DEVICES:
                device = bondedDevices.get(position);
                break;
            case DISCOVERY_DEVICES:
                device = discoveryDevices.get(position);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + list);
        }
        return device;
    }

    @Override
    public void onClick(BluetoothDevice device) {
        Log.i(TAG, "onClick: Entramos!!");
        createConnection(device);
    }

}




























