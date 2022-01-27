package com.icat.javablue.buetooth_utils;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Esta clase se utiliza para compartir el Socket
 * de un dispositivo bluetooth entre Activities
 * @author: María Alejandra Castillo Martínez
 */
public class ShareSocket {
    private static final String TAG = "ShareSocket";

    private static BluetoothSocket socket;

    /**
     * Retorna el Socket que almacena
     * @return socket
     */
    public static synchronized BluetoothSocket getSocket(){
        return socket;
    }

    /**
     * Pide un Socket para guardarlo
     * @param socket
     */
    public static synchronized void setSocket(BluetoothSocket socket){
        ShareSocket.socket = socket;
    }

    /**
     * Cierra el Socket guardado
     */
    public static synchronized void closeSocket(){
        try{
            socket.close();
        }catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
