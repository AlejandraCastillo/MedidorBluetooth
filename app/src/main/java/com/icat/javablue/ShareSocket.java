package com.icat.javablue;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ShareSocket {
    // Debugging
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
