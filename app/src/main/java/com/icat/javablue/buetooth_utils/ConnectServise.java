package com.icat.javablue.buetooth_utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.icat.javablue.MessageReceiverActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * Esta clase es la ewncargada de crear la coneccion al dispositivo deseado
 * @author: María Alejandra Castillo Martínez
 */
class ConnectService extends AsyncTask<Void, Void, Boolean> {
    // Debugging
    private static final String TAG = "ClientConnectService";
    private final BluetoothSocket socket;
    private Context context;
    private String name;

    /**
     * Es el constructor de la clase
     * @param device Dispositivo al que se desea conectar
     * @param context Contexto del activity desde el que se manda a llamar
     */
    public ConnectService(BluetoothDevice device, Context context) {
        BluetoothSocket tempSocket = null;
        this.context = context;
        this.name = device.getName();

        //Creamos el socket
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            tempSocket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(TAG, "No se pudo crear el socket", e);
        }
        socket = tempSocket;
        Log.i(TAG, "Socket creado");
    }

    /**
     *
     * @param voids
     * @return
     */
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            socket.connect();
        } catch (IOException connectException) {
            try {
                socket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "No se pudo cerrar el socket", closeException);
            }
            return false;
        }
        Log.i(TAG, "El dispositivo esta conectado");
        return true;
    }

    /**
     *
     * @param result
     */
    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if(result) {
            Intent intent = new Intent(context, MessageReceiverActivity.class);
            intent.putExtra("name", name);
            ShareSocket.setSocket(socket); // se pasa el socket para comunicarse con el dispositivo como argumento
            context.startActivity(intent);
        }
        else
            Toast.makeText(context, "No se pudeo realizar la conección", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}

