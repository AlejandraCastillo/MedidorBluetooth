package com.ake.medidorbluetooth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ake.medidorbluetooth.database.SQLiteActions;
import com.ake.medidorbluetooth.database.TablaRegistro;
import com.ake.medidorbluetooth.recycleview_download.OnClickListenerDownload;
import com.ake.medidorbluetooth.recycleview_download.RecycleViewDownloadAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class DownloadActivity extends AppCompatActivity implements OnClickListenerDownload {

    private static final String TAG = "DownloadActivity";

    private RecyclerView recyclerView;
    private RecycleViewDownloadAdapter downloadAdapter;

    private SQLiteActions actions;

    private ArrayList<TablaRegistro> listTablaRegistro;

    ActivityResultLauncher<Intent> descargaLauncher;

    private static final int REQUEST_DOWNLOAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Descargas");

        recyclerView = findViewById(R.id.rv_descargas);

        actions = new SQLiteActions(this);
        listTablaRegistro = new ArrayList<TablaRegistro>();

        listTablaRegistro = actions.readTablaRegistro();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        downloadAdapter = new RecycleViewDownloadAdapter(listTablaRegistro, this);
        recyclerView.setAdapter(downloadAdapter);

        descargaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            int registro_id = getIntent().getExtras().getInt("registro_id");
                            if(data != null && data.getData() != null){
                                actions.createDocument(data.getData(), registro_id);
                            }
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
        String nombre = actions.getFechaRegistro(registro_id) + "_g" + registro_id + ".cvs";
        intent.putExtra(Intent.EXTRA_TITLE, nombre);
        intent.putExtra("registro_id", registro_id);

        descargaLauncher.launch(intent);
    }

}