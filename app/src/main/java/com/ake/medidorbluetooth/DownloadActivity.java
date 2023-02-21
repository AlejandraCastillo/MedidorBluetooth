package com.ake.medidorbluetooth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ake.medidorbluetooth.database.SQLiteActions;
import com.ake.medidorbluetooth.database.TablaRegistro;
import com.ake.medidorbluetooth.recycleview_download.OnClickListenerDownload;
import com.ake.medidorbluetooth.recycleview_download.RecycleViewDownloadAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class DownloadActivity extends AppCompatActivity implements OnClickListenerDownload {

    private static final String TAG = "DownloadActivity";

    private SQLiteActions actions;

    ActivityResultLauncher<Intent> descargaLauncher;

//    private static final String REGISTRO_ID = "registro_id";
    private int mRegistroID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Descargas");

        RecyclerView recyclerView = findViewById(R.id.rv_descargas);

        actions = new SQLiteActions(this);
        ArrayList<TablaRegistro> listTablaRegistro;

        listTablaRegistro = actions.readTablaRegistro();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        RecycleViewDownloadAdapter downloadAdapter = new RecycleViewDownloadAdapter(listTablaRegistro, this);
        recyclerView.setAdapter(downloadAdapter);

        descargaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            Log.i(TAG, "onCreate: registro " + mRegistroID);
                            actions.createDocument(data.getData(), mRegistroID);
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(TablaRegistro registro) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/cvs");
        int registro_id = registro.getRegistroID();
        String nombre = actions.getFechaRegistro(registro_id) + "_reg" + registro_id + ".cvs";
        intent.putExtra(Intent.EXTRA_TITLE, nombre);
        mRegistroID = registro_id;

        descargaLauncher.launch(intent);
    }

}