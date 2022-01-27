package com.icat.javablue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

/**
 * Este Activity Se mostrara en caso de que no exista un adaptador de Bluetooth
 * @author: María Alejandra Castillo Martínez
 */
public class NoBluetoothActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "NoBluetoothActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_bluetooth);
        Log.i(TAG, "El adaptador de Bluetooth no existe, es imposible usar la aplicacion");
    }
}