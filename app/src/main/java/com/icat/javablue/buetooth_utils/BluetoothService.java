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

public class BluetoothService {
    // Debugging
    private static final String TAG = "BluetoothService";

    public BluetoothAdapter bluetoothAdapter;
    public ConnectService clientConnection;
    private Context context;

    //Listas de dispositivos
    public final ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>();
    public final ArrayList<BluetoothDevice> discoveryDevices = new ArrayList<>();

    //Adaptadores
    public ArrayAdapter<String> adapterPairedDevices;
    public ArrayAdapter<String> adapterDiscoveryDevices;

    //Constantes para las listas de dispositivos
    public static final int PAIRED_DEVICES = 1;
    public static final int DISCOVERY_DEVICES=2;

    //Constructor de la clase
    public BluetoothService(Context context){
        adapterPairedDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        adapterDiscoveryDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    /* Broadcast para la busqueda de dispositivos
    *  Es necesario registrarlo en el Activity en el que se implemente */
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

    /* Metodo que crea y devielve un IntentFilter para registar el broadcast
    *  Es necesario indicar cuales de las acciones disponibles van a emplearse*/
    public IntentFilter getBroadcastIntent(
            boolean ACTION_FOUND,
            boolean ACTION_DISCOVERY_STARTED,
            boolean ACTION_DISCOVERY_FINISHED
    ) {
        IntentFilter filter = new IntentFilter();
        if (ACTION_FOUND)
            filter.addAction(BluetoothDevice.ACTION_FOUND);
        if(ACTION_DISCOVERY_STARTED) {
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        }
        if(ACTION_DISCOVERY_FINISHED) {
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        }
        return filter;
    }

    /* Metodo que indica si se encontró un adaptador de bluetooth */
    public boolean bluetoothAdapterExist(){
        return bluetoothAdapter != null;
    }

    /* Metodo que obtiene la lista de dispositivos sincronizados
    *  Almacena los dispositivos en pairedDevices
    *  y pasa los nombres de los dispositivos a adapterPairedDevices */
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

    /* Metodo que inicia la busqueda de dispositivos
     * Almacena los dispositivos encontrados en discoveryDevices
     * y pasa los nombres de los dispositivos a adapterDiscoveryDevices */
    public void startDiscovery(){
        bluetoothAdapter.cancelDiscovery();
        adapterDiscoveryDevices.clear();
        discoveryDevices.clear();
        bluetoothAdapter.startDiscovery();
    }

    /* Metodo que devielve el dispositivo indicado
    *  Es necesario especificar la lista de la que se quiere obetener
    *  el ispositivo y su posicion en la misma
    *  La lista 1 es para los dispositivos sicronizados
    *  La lista 2 es para los dispositivos encontrados */
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

    /* Metodo para cancelar la busqueda de dispositivos */
    public void cancelDiscovery(){
        bluetoothAdapter.cancelDiscovery();
    }

    /* Metodo para conectarsea un disposotivo
     *  Esta clase creara un objeto de tipo ConnectClientDevice desde el cual se
     *  administrara la coneccion del dispositivo indicado */
    public void createConection(BluetoothDevice device){
        bluetoothAdapter.cancelDiscovery();
        clientConnection = new ConnectService(device, context);
        clientConnection.execute();
    }

}

