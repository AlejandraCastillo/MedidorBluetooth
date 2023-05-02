package com.ake.medidorbluetooth.buetooth_utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.ake.medidorbluetooth.recycleview_bluetooth.OnClickListenerBluetooth;
import com.ake.medidorbluetooth.recycleview_bluetooth.RecycleViewBluetoothAdapter;
import java.util.Set;

public class BluetoothUtils implements OnClickListenerBluetooth {
    private static final String TAG = "BluetoothUtils";

    private final BluetoothAdapter bluetoothAdapter;
    private final Context context;
    private final Activity activity;

    public RecycleViewBluetoothAdapter adapterBondedDevices;
    public RecycleViewBluetoothAdapter adapterDiscoveryDevices;

    private static final int REQUEST_PERMISSION = 1;

    public BluetoothUtils(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        adapterBondedDevices = new RecycleViewBluetoothAdapter(this, context, activity);
        adapterDiscoveryDevices = new RecycleViewBluetoothAdapter(this, context, activity);
    }

    public boolean bluetoothAdapterExist() {
        return bluetoothAdapter != null;
    }

    public boolean bluetoothAdapterIsEnable() {
        return bluetoothAdapter.isEnabled();
    }

    public void disableBluetoothAdapter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
        }
        bluetoothAdapter.disable();
    }

    public void enableBluetoothAdapter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
        }
        bluetoothAdapter.enable();
    }

    public boolean getBondedDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
        }
        Set<BluetoothDevice> sBondedDevices = bluetoothAdapter.getBondedDevices();
        adapterBondedDevices.clear();
        for (BluetoothDevice device : sBondedDevices) {
            adapterBondedDevices.add(device);
        }
        //True si existen dispositivos
        return adapterBondedDevices.getItemCount() > 0;
    }

    public void discovery(boolean discoveryFlag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermission(Manifest.permission.BLUETOOTH_SCAN);
        }
        if (!discoveryFlag) {
            adapterDiscoveryDevices.clear();
            bluetoothAdapter.startDiscovery();
        } else {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public boolean deviceAlreadyExists(BluetoothDevice device) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
        }
        String name = device.getName();
        for (BluetoothDevice bondedD : adapterBondedDevices.list) {
            if (device.getAddress().equals(bondedD.getAddress())) {
                Log.i(TAG, "onReceive: El dispositivo " + name + " ya existe en adapterBondedDevices");
                return true;
            }
        }
        for (BluetoothDevice discoveryD : adapterDiscoveryDevices.list) {
            if (device.getAddress().equals(discoveryD.getAddress())) {
                Log.i(TAG, "onReceive: El dispositivo " + name + " ya existe en adapterDiscoveryDevices");
                return true;
            }
        }
        return false;
    }

    public void addNewDevice(BluetoothDevice device) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermission(Manifest.permission.BLUETOOTH_CONNECT);
        }
        if (!deviceAlreadyExists(device) && device.getName()!=null) {
            adapterDiscoveryDevices.add(device);
        }
    }

    @Override
    public void onClick(BluetoothDevice device) {
        //Crear conexión
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermission(Manifest.permission.BLUETOOTH_SCAN);
        }
        bluetoothAdapter.cancelDiscovery();
        ConnectAsyncTask connectAsyncTask = new ConnectAsyncTask(device, context, activity);
        connectAsyncTask.execute();
    }

    public void checkPermission(String permiso){
        if (ContextCompat.checkSelfPermission(context, permiso)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "onClickBuscar: Solicitando el permiso " + permiso);
            ActivityCompat.requestPermissions(activity,
                    new String[]{permiso}, REQUEST_PERMISSION);
        }
        else
            Log.i(TAG, "getPermission: Permiso " + permiso + " obtenido");
    }

}