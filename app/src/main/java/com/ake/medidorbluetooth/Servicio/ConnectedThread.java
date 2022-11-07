package com.ake.medidorbluetooth.Servicio;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConnectedThread extends Thread{

    private static final String TAG = "ConnectedThread";

    public static final int MESSAGE_READ = 0;
    public static final int CONNECTION_LOST = 1;

    private final BluetoothSocket mmSocket;
    private final Handler mmHandler;
    private final BufferedReader mmBuffer;

    public ConnectedThread(BluetoothSocket socket, Handler handler){
        mmSocket = socket;
        mmHandler = handler;
        InputStream tmpInStream = null;

        try{
            tmpInStream = socket.getInputStream();
        } catch (IOException e){
            Log.e(TAG, "Error al crear el input stream", e);
        }

        InputStream mmInStream = tmpInStream;
        mmBuffer = new BufferedReader(new InputStreamReader(mmInStream));
    }

    public void run(){
        String line;
        String regex = "1?2?3::\\d*(-\\d*\\.\\d*){4}::456";

        while (mmSocket.isConnected()){
            try {
                line = mmBuffer.readLine().replaceAll("\\s", "");
                if(line.matches(regex)){
                    mmHandler.obtainMessage(MESSAGE_READ, line)
                            .sendToTarget();
                }
            } catch (IOException e){
                mmHandler.obtainMessage(CONNECTION_LOST)
                        .sendToTarget();
                break;
            }
        }
    }

    public void cancel(){
        try {
            mmSocket.close();
            Log.i(TAG, "Se ha cerrado el socket");
        } catch (IOException e){
            Log.e(TAG, "No se pudo cerrar el socket", e);
        }
    }

}


