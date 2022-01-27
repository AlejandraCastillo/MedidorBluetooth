package com.icat.javablue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.icat.javablue.database.SQLiteActions;
import com.icat.javablue.database.TablaGrupo;

import java.util.ArrayList;

/**
 * Este activity mostrá un menu de seleccion para poder elegir el grupo
 * de datos que se desea descargar como un documento *.cvs
 * @author: María Alejandra Castillo Martínez
 */
public class DownloadActivity extends AppCompatActivity {

    // Debugging
    private static final String TAG = "DownloadActivity";

    //Views
    private ListView lvDownload;

    //DataBase
    private SQLiteActions actions;

    //Para el ListView
    private ArrayList<TablaGrupo> listTablaGrupo;
    private ArrayAdapter<String> adapterDownload;

    //Request
    private static final int REQUEST_DOWNLOAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        getSupportActionBar().setTitle("Descargar");

        lvDownload = findViewById(R.id.lv_descargas);

        actions = new SQLiteActions(this);
        listTablaGrupo = new ArrayList<TablaGrupo>();

        adapterDownload = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);


        listTablaGrupo = actions.readTablaGrupo();

        if(!listTablaGrupo.isEmpty()){
            for(TablaGrupo grupo : listTablaGrupo){
                adapterDownload.add(grupo.getFecha() + " - " + grupo.getGrupoID().toString());
            }
        }

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

            startActivityForResult(intent, REQUEST_DOWNLOAD);

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_DOWNLOAD && resultCode == RESULT_OK){
            int grupo_id = getIntent().getExtras().getInt("grupo_id");
            if(data != null && data.getData() != null){
                actions.createDocument(data.getData(), grupo_id);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_conectar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = null;

        switch (item.getItemId()){
            case R.id.m_conectar:
                intent = new Intent(this, ConnectActivity.class);// se pasa el socket para comunicarse con el dispositivo como argumento
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}