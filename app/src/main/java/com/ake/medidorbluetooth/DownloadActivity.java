package com.ake.medidorbluetooth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ake.medidorbluetooth.database.SQLiteActions;
import com.ake.medidorbluetooth.database.TablaGrupo;

import java.util.ArrayList;
import java.util.Objects;

public class DownloadActivity extends AppCompatActivity {

    private static final String TAG = "DownloadActivity";

    private ListView lvDownload;

    private SQLiteActions actions;

    private ArrayList<TablaGrupo> listTablaGrupo;
    private ArrayAdapter<String> adapterDownload;

    ActivityResultLauncher<Intent> descargaLauncher;

    private static final int REQUEST_DOWNLOAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        getSupportActionBar().setTitle(TAG);

        lvDownload = findViewById(R.id.lv_descargas);

        actions = new SQLiteActions(this);
        listTablaGrupo = new ArrayList<TablaGrupo>();

        listTablaGrupo = actions.readTablaGrupo();

        adapterDownload = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, listTablaGrupo) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText("Grupo" + listTablaGrupo.get(position).getGrupoID());
                text2.setText(listTablaGrupo.get(position).getFecha());
                return view;
            }
        };

        lvDownload.setAdapter(adapterDownload);

        lvDownload.setOnItemClickListener((parent, view, position, id) -> {

            TablaGrupo grupo = listTablaGrupo.get(position);

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/cvs");
            int grupo_id = grupo.getGrupoID();
            String nombre = actions.getFechaGrupo(grupo_id) + "_g" + grupo_id + ".cvs";
            intent.putExtra(Intent.EXTRA_TITLE, nombre);
            intent.putExtra("grupo_id", grupo_id);

            descargaLauncher.launch(intent);
        });

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



}