package com.ake.medidorbluetooth.buetooth_utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class BluetoothService {
    private static final String TAG = "BluetoothService";

    private BluetoothAdapter bluetoothAdapter;
    private Context context;

    public BluetoothService(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean bluetoothAdapterExist(){
        if(bluetoothAdapter == null)
            return  false;
        return true;
    }

    public boolean bluetoothAdapterIsEnable(){
        if(!bluetoothAdapter.isEnabled())
            return false;
        return true;
    }

    public boolean disableBluetoothAdapter(){
        return bluetoothAdapter.disable();
    }

    public boolean enableBluetoothAdapter(){
        return bluetoothAdapter.enable();
    }


}
