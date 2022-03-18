package com.ake.medidorbluetooth.buetooth_utils;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ShareSocket {
    private static final String TAG = "ShareSocket";

    private static BluetoothSocket socket;

    public static synchronized BluetoothSocket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(BluetoothSocket socket){
        ShareSocket.socket = socket;
    }

    public static synchronized void closeSocket(){
        try{
            socket.close();
        }catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
