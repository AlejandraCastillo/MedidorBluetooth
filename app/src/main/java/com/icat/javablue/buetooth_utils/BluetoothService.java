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
     * ArrayAdapter pata la lista de dispositivos Sincronizados
     */
    public ArrayAdapter<String> adapterPairedDevices;

    /**
     * ArrayAdapter para la lista de dispositivos encontrados
     */
    public ArrayAdapter<String> adapterDiscoveryDevices;

    //Constantes para las listas de dispositivos
    public static final int PAIRED_DEVICES = 1;
    public static final int DISCOVERY_DEVICES=2;


    /**
     * Constructor de la clase
     * @param context Es el contexto de la actividad desde la que se le manda a llamar.
     */
    public BluetoothService(Context context){
        adapterPairedDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        adapterDiscoveryDevices = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    /**
     * Broadcast para la busqueda de dispositivos
     * Es necesario registrarlo en el Activity en el que se implemente
     * */
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
     * Metodo que crea un IntentFilter para registar el broadcast
     * */
    public IntentFilter getBroadcastIntent(
//            boolean ACTION_FOUND,
//            boolean ACTION_DISCOVERY_STARTED,
//            boolean ACTION_DISCOVERY_FINISHED
    ) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

//        if (ACTION_FOUND)
//            filter.addAction(BluetoothDevice.ACTION_FOUND);
//        if(ACTION_DISCOVERY_STARTED) {
//            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        }
//        if(ACTION_DISCOVERY_FINISHED) {
//            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        }
        return filter;
    }

    /**
     * Metodo que indica el estado del Adaptador de Bluetooth
     * @return 0 - El adaptador no existe, 1 - El adaptador no esta habilitado, 2 - El adaptador esta habilitado
     */
    public int bluetoothAdapterState(){
        if(bluetoothAdapter == null)
            return 0;
        if (!bluetoothAdapter.isEnabled())
            return 1;
        return 2;
    }

    /**
     * Metodo que obtiene la lista de dispositivos sincronizados
     * Almacena los dispositivos en pairedDevices y pasa los nombres
     * de los dispositivos a adapterPairedDevices
     * @return Retorna true si se encontraron dispositivos sincronizados
     * */
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
     * Metodo que inicia la busqueda de dispositivos
     * Almacena los dispositivos encontrados en discoveryDevices
     * y pasa los nombres de los dispositivos a adapterDiscoveryDevices */
    public void startDiscovery(){
        bluetoothAdapter.cancelDiscovery();
        adapterDiscoveryDevices.clear();
        discoveryDevices.clear();
        bluetoothAdapter.startDiscovery();
    }

    /**
     * Metodo que devielve el dispositivo indicado
     * @param list Lista de la que se desea obtener el dispositivo
     * @param position posicion del dispositivo en la lista
     * @return
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
     * Metodo para cancelar la busqueda de dispositivos
     */
    public void cancelDiscovery(){
        bluetoothAdapter.cancelDiscovery();
    }

    /**
     * Metodo para conectarsea un disposotivo
     * Este metodo creara un objeto de tipo ConnectClientDevice desde el
     * cual se administrara la coneccion del dispositivo indicado
     * @param device Dispositivo al cual se pretende conectarse
     */
    public void createConection(BluetoothDevice device){
        bluetoothAdapter.cancelDiscovery();
        clientConnection = new ConnectService(device, context);
        clientConnection.execute();
    }

}

