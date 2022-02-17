package com.ake.medidorbluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;

import java.util.Objects;

public class ConnectActivity extends AppCompatActivity {

    private static final String TAG = "ConnectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        Objects.requireNonNull(getSupportActionBar()).setTitle(TAG);
    }

    public void onClickCancelar(View view) {
    }

    public void onClickBuscar(View view) {
    }
}