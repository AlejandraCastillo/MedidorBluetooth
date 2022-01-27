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

    /**
     * Adaptador con la lista de los nombres de los dispositivos sincornizados
     */
    public ArrayAdapter<String> adapterPairedDevices;

    /**
     * Adaptador con la lista de los nombres de los dispositivos disponibles
     */
    public ArrayAdapter<String> adapterDiscoveryDevices;

    /**
     * Constante para indicar que se trata de los dispositivos sincronizados
     */
    public static final int PAIRED_DEVICES = 1;

    /**
     * Constante para indicar que se trata de los dispositivos disponibles
     */
    public static final int DISCOVERY_DEVICES=2;

    /**
     * Constante para indicar que el adapatador de bluetooth no existe
     */
    public static final int BLUETOOTH_ADAPTER_DOES_NOT_EXIST = 0;

    /**
     * Constante para indicar que el adaptador de bluetooth esta deshabilitado
     */
    public static final int BLUETOOTH_ADAPTER_IS_DISABLED = 1;

    /**
     * Constante para indicar que el adaptador de bluetooth esta habilitado
     */
    public static final int BLUETOOTH_ADAPTER_IS_ENABLE=2;

    /**
     * Es el constructor de la clase
     * @param context El contexto del Activity desde el que se manda a llamar
     */
    public BluetoothService(Context context){
        adapterPairedDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        adapterDiscoveryDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    /**
     * Este broadcast administra las posibles acciones que intervienen
     * en la busqueda de dispositivos bluetooth
     */
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

    /**
     * Inicializa un IntentFilter para usar en la busqueda de dispositivos buletooth
     * @return devuelve un IntentFilter para usar con el BroadcastReceiver
     */
    public IntentFilter getBroadcastIntent() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        return filter;
    }

    /**
     * Revisa el estado del adaptador
     * @return el estado del adapatador
     */
    public int bluetoothAdapterState(){
        if(bluetoothAdapter == null)
            return BLUETOOTH_ADAPTER_DOES_NOT_EXIST;
        if (!bluetoothAdapter.isEnabled())
            return BLUETOOTH_ADAPTER_IS_DISABLED;
        return BLUETOOTH_ADAPTER_IS_ENABLE;
    }

    /**
     * Obtiene la lista de dispositivos sincronizados del adaptador de bluetooth
     * Los dispositivos obtenidos se almacenan en pairedDevices y sus nombres en
     * adapterPairedDevices
     * @return Devuelve true si se encontraron 1 o más dispositivos
     */
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

    /**
     * Inicia la busca que dispositivos bluetooth disponibles
     */
    public void startDiscovery(){
        bluetoothAdapter.cancelDiscovery();
        adapterDiscoveryDevices.clear();
        discoveryDevices.clear();
        bluetoothAdapter.startDiscovery();
    }

    /**
     * Devue el dispositivo solicitado ya sea de la lista de dispositivos
     * sincronizados o de la lista de dispositivos disponibles
     * @param list La lista de la que se desea obtener el dispositivo
     * @param position La posición del dispositivo en la lista
     * @return Devuelve el dispositivo solicitado
     */
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

    /**
     * Detiene la busqueda de dispositivos bluetooth disponibles
     */
    public void cancelDiscovery(){
        bluetoothAdapter.cancelDiscovery();
    }

    /**
     * Inicia la conección con el dispositivo dado
     * @param device el dispositivo al que desea conectarse
     */
    public void createConection(BluetoothDevice device){
        bluetoothAdapter.cancelDiscovery();
        clientConnection = new ConnectService(device, context);
        clientConnection.execute();
    }

}

