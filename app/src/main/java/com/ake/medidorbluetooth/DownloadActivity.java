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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ake.medidorbluetooth.database.SQLiteActions;
import com.ake.medidorbluetooth.database.TablaGrupo;
import com.ake.medidorbluetooth.recycleview_download.OnClickListenerDownload;
import com.ake.medidorbluetooth.recycleview_download.RecycleViewDownloadAdapter;

import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity implements OnClickListenerDownload {

    private static final String TAG = "DownloadActivity";

    private RecyclerView recyclerView;
    private RecycleViewDownloadAdapter downloadAdapter;

        private SQLiteActions actions;

    private ArrayList<TablaGrupo> listTablaGrupo;

    ActivityResultLauncher<Intent> descargaLauncher;

    private static final int REQUEST_DOWNLOAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        getSupportActionBar().setTitle(TAG);

        recyclerView = findViewById(R.id.rv_descargas);

        actions = new SQLiteActions(this);
        listTablaGrupo = new ArrayList<TablaGrupo>();

        listTablaGrupo = actions.readTablaGrupo();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        downloadAdapter = new RecycleViewDownloadAdapter(listTablaGrupo, this);
        recyclerView.setAdapter(downloadAdapter);

        descargaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            int grupo_id = getIntent().getExtras().getInt("grupo_id");
                            if(data != null && data.getData() != null){
                                actions.createDocument(data.getData(), grupo_id);
                            }
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(TablaGrupo grupo) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/cvs");
        int grupo_id = grupo.getGrupoID();
        String nombre = actions.getFechaGrupo(grupo_id) + "_g" + grupo_id + ".cvs";
        intent.putExtra(Intent.EXTRA_TITLE, nombre);
        intent.putExtra("grupo_id", grupo_id);

        descargaLauncher.launch(intent);
    }

}