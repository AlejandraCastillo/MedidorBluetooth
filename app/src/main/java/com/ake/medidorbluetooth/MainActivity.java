package com.ake.medidorbluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ake.medidorbluetooth.buetooth_utils.BluetoothService;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothService bluetoothService = new BluetoothService(this);

        if (!bluetoothService.bluetoothAdapterExist()){
            Button connectButton = findViewById(R.id.b_conectar);
            connectButton.setVisibility(View.INVISIBLE);
            Button downloadButton = findViewById(R.id.b_descargas);
            downloadButton.setVisibility(View.INVISIBLE);
            TextView textView = findViewById(R.id.tv_bienvenido);
            textView.setText("¡Ups! Parece que no tienes bluetooth :c");
        }

    }

    public void onClickConectar(View view) {
        Intent intent = new Intent(this, ConnectActivity.class);
        startActivity(intent);
    }

    public void onClickDescargar(View view) {
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }

}