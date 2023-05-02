package com.ake.medidorbluetooth;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ake.medidorbluetooth.buetooth_utils.BluetoothUtils;
import java.util.Objects;
import com.ake.medidorbluetooth.database.SQLiteActions;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setTitle(" ");

        BluetoothUtils bluetoothUtils = new BluetoothUtils(this, this);

        if (!bluetoothUtils.bluetoothAdapterExist()){
            Button connectButton = findViewById(R.id.b_conectar);
            connectButton.setVisibility(View.INVISIBLE);
            Button downloadButton = findViewById(R.id.b_descargas);
            downloadButton.setVisibility(View.INVISIBLE);
            TextView tvBienvenida = findViewById(R.id.tv_bienvenido);
            tvBienvenida.setVisibility(View.INVISIBLE);
            TextView tvNoBluetooth = findViewById(R.id.tv_no_bluetooth);
            tvNoBluetooth.setVisibility(View.VISIBLE);
        }

//        SQLiteActions actions = new SQLiteActions(this);
//        actions.borrarBD();
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