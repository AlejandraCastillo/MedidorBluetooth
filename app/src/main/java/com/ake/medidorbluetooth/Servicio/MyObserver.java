package com.ake.medidorbluetooth.Servicio;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class MyObserver implements DefaultLifecycleObserver {

   private static final String TAG = "MyObserver";

   Intent serviceIntent;
   boolean isBounded;
   MyService service;
   Context context;

   public MyObserver(Context context) {
      this.context = context;
//      service = new MyService();
//      serviceIntent = new Intent(context, MyService.class);
   }

   @Override
   public void onCreate(@NonNull LifecycleOwner owner) {
      DefaultLifecycleObserver.super.onCreate(owner);
      Log.i(TAG, "onCreate");
//      service.startService(serviceIntent);
   }

   @Override
   public void onResume(@NonNull LifecycleOwner owner) {
      DefaultLifecycleObserver.super.onResume(owner);
      Log.i(TAG, "onResume");
//      service.bindService(serviceIntent, connection, BIND_AUTO_CREATE);
   }

   @Override
   public void onPause(@NonNull LifecycleOwner owner) {
      DefaultLifecycleObserver.super.onPause(owner);
      Log.i(TAG, "onPause");
//      service.unbindService(connection);
   }

   @Override
   public void onDestroy(@NonNull LifecycleOwner owner) {
      DefaultLifecycleObserver.super.onDestroy(owner);
      Log.i(TAG, "onDestroy");
//      service.stopService(serviceIntent);
   }

//   private final ServiceConnection connection = new ServiceConnection() {
//      @Override
//      public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//         MyService.MyBinder binder = (MyService.MyBinder) iBinder;
//         service = binder.getService();
//         isBounded = true;
//      }
//
//      @Override
//      public void onServiceDisconnected(ComponentName componentName) {
//         isBounded = false;
//      }
//   };

}
