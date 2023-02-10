package com.ake.medidorbluetooth.Servicio;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.ake.medidorbluetooth.MessageReceiverActivity;
import com.ake.medidorbluetooth.R;
import com.ake.medidorbluetooth.buetooth_utils.ShareSocket;
import com.ake.medidorbluetooth.database.SQLiteActions;
import com.ake.medidorbluetooth.database.TablaDatos;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class MyService extends Service {
    private static final String TAG = "MyService";

    private static final int NOTIFICATION_ID = 1;
    public static final int MESSAGE_READ = 2;
    public static final int CONNECTION_LOST = 3;
    private static final String CHANNEL_ID = "100";

    private final IBinder myBinder = new MyBinder();
    private boolean isBind = false;

    BluetoothSocket socket;
    private SQLiteActions actions;
    private int registro;

    ExecutorService executorService;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        socket = ShareSocket.getSocket();
        actions = new SQLiteActions(this);
        registro = actions.addNewRegister();

        startForeground(NOTIFICATION_ID, showNotification("Contenido "));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        doTask();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        ShareSocket.closeSocket();
        executorService.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        isBind = true;
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        isBind = false;
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }

    private Notification showNotification(String content){
        //Solo Android Oreo o versiones superiores
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(
                        new NotificationChannel(
                                CHANNEL_ID,
                                "ForegroundService",
                                NotificationManager.IMPORTANCE_HIGH
                        )
                );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Registro " + registro)
                .setContentText(content)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_arrow_24)
                .build();
    }

    private void updateNotification(String data){
        Notification notification = showNotification(data);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void doTask(){
        executorService = Executors.newSingleThreadExecutor();

        AtomicReference<String> line = new AtomicReference<>();
        String regex = "1?2?3::\\d*(-\\d*\\.\\d*){4}::456";

        InputStream tmpInStream = null;
        try{
            tmpInStream = socket.getInputStream();
        } catch (IOException e){
            Log.e(TAG, "Error al crear el input stream", e);
        }
        InputStream mmInStream = tmpInStream;
        BufferedReader buffer = new BufferedReader(new InputStreamReader(mmInStream));

        executorService.execute(() -> {
                while (socket.isConnected()){
                    try {
                        line.set(buffer.readLine().replaceAll("\\s", ""));
                        if(line.get().matches(regex)){
                            handler.obtainMessage(MESSAGE_READ, line)
                                    .sendToTarget();
                        }
                        Log.d(TAG, "doTask: " + line);
                    } catch (IOException e){
                        handler.obtainMessage(CONNECTION_LOST)
                                .sendToTarget();
                        break;
                    }
                }
            }
        );
    }

    private final Handler handler = new Handler(Looper.myLooper()) {
        //    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        @SuppressLint("SetTextI18n")
        public void handleMessage(@NotNull Message msg){
            switch (msg.what){
                case MESSAGE_READ:
                    String readMessage = msg.obj.toString();

                    int index = readMessage.indexOf("::");
                    String aux = readMessage.substring(index + 2);
                    index = aux.indexOf("::");
                    aux = aux.substring(0, index);

                    TablaDatos row = new TablaDatos();

                    //Tiempo
                    index = aux.indexOf('-');
                    int t = Integer.parseInt(aux.substring(0, index));
                    row.setTiempo(t);
                    aux = aux.substring(index + 1);

                    //Voltage
                    index = aux.indexOf('-');
                    double v = Double.parseDouble(aux.substring(0, index));
                    row.setVoltaje(v);
                    aux = aux.substring(index + 1);

                    //Corriente
                    index = aux.indexOf('-');
                    double c = Double.parseDouble(aux.substring(0, index));
                    row.setCorriente(c);
                    aux = aux.substring(index + 1);

                    //Potencia
                    index = aux.indexOf('-');
                    double p = Double.parseDouble(aux.substring(0, index));
                    row.setPotencia(p);

                    //Energia
                    double e = Double.parseDouble(aux.substring(index + 1));

                    row.setEnergia(e);
                    row.setRegistroID(registro);
                    actions.addnewDataRow(row);

                    updateNotification(row.printRow(TAG));

                if(isBind){
                    MessageReceiverActivity.gaugeVoltaje.setValue(v);
                    MessageReceiverActivity.gaugeCorriente.setValue(c);
                    MessageReceiverActivity.gaugePotencia.setValue(p);
                    MessageReceiverActivity.gaugeEnergia.setValue(e);

                    DecimalFormat formato = new DecimalFormat("00.00");
                    MessageReceiverActivity.tvVoltage.setText(formato.format(v) + " V");

                    formato = new DecimalFormat("0.00");
                    MessageReceiverActivity.tvCorriente.setText(formato.format(c) + " A");
                    MessageReceiverActivity.tvEnergia.setText(formato.format(e) + " Wh");

                    formato = new DecimalFormat("000.00");
                    MessageReceiverActivity.tvPotencia.setText(formato.format(p) + " W");
                }

                    break;

                case CONNECTION_LOST:
                    Log.i(TAG, "Conexión perdida");
//                    finish();
            }

        }
    };

}
























