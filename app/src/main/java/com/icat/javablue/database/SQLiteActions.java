package com.icat.javablue.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SQLiteActions {

    private static final String TAG = "SQLiteActions";


    ConexionSQLiteHelper conn;
    SQLiteDatabase db;
    Context context;

    public SQLiteActions(Context context) {
        this.context = context;
        conn = new ConexionSQLiteHelper(context, Querys.DB_DATOS, null, 1);
    }

    public String getFechaGrupo(Integer grupo_id){
        db = conn.getReadableDatabase();
        String fecha = "";
        String[] parametros = {grupo_id.toString()};

        try{
            Cursor cursor = db.rawQuery(Querys.SELECT_FROM_GRUPO_BY_ID, parametros);

            cursor.moveToFirst();
            fecha = cursor.getString(0);

        }
        catch (Exception e){
            Log.i(TAG, "getGroupByID: Operacion fallida");
        }

        db.close();
        return fecha;
    }

    private static String getDate(){
        long ahora = System.currentTimeMillis();
        Date fecha = new Date(ahora);
        DateFormat df = new SimpleDateFormat("yy-MM-dd");
        return df.format(fecha);
    }

    public int addNewGroup(){
        db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Querys._FECHA, getDate());

        int grupo = (int) db.insert(Querys.TABLA_GRUPO, Querys._GRUPO_ID, values);

        db.close();
        return grupo;
    }

    public void addNewRow(TablaDatos row){
        db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Querys._TIEMPO, row.getTiempo());
        values.put(Querys._VOLTAJE, row.getVolaje());
        values.put(Querys._CORRIENTE, row.getCorriente());
        values.put(Querys._POTENCIA, row.getPotencia());
        values.put(Querys._ENERGIA, row.getEnergia());
        values.put(Querys._GRUPO_ID, row.getGrupoID());

        db.insert(Querys.TABLA_DATOS, Querys._DATOS_ID, values);

        db.close();
    }

    public ArrayList<TablaDatos>  readDataTable(){

        ArrayList<TablaDatos> listaTablaDatos;

        db = conn.getReadableDatabase();

        TablaDatos row = null;
        listaTablaDatos = new ArrayList<TablaDatos>();

        Cursor cursor = db.rawQuery(Querys.SELECT_FROM_DATOS,null);

        while (cursor.moveToNext()){
            row = new TablaDatos();
            row.setTiempo(cursor.getInt(1));
            row.setVolaje(cursor.getDouble(2));
            row.setCorriente(cursor.getDouble(3));
            row.setPotencia(cursor.getDouble(4));
            row.setEnergia(cursor.getDouble(5));

            int grupo = cursor.getInt(6);
            row.setGrupoID(grupo);
            row.setFecha(getFechaGrupo(grupo));

            listaTablaDatos.add(row);
        }

        db.close();
        return listaTablaDatos;
    }

    public boolean createDocument(Uri uri, Integer grupo_id){
        db = conn.getWritableDatabase();
        StringBuilder contenido = new StringBuilder("Tiempo,Voltaje,Corriente,Potencia,Energia\n");

        /* Obtencion de datos */
        String[] parametros = {grupo_id.toString()};
        Cursor cursor = db.rawQuery(Querys.SELECT_FROM_DATOS_BY_GRUPO_ID,parametros);

        while (cursor.moveToNext()){
            contenido.append(cursor.getDouble(1)).append(","); //TIEMPO
            contenido.append(cursor.getDouble(2)).append(","); //VOLTAJE
            contenido.append(cursor.getDouble(3)).append(","); //CORRIENTE
            contenido.append(cursor.getDouble(4)).append(","); //POTENCIA
            contenido.append(cursor.getDouble(5)).append("\n"); //ENERGIA
        }

        Log.i(TAG, contenido.toString());

        try{
            OutputStream ops = ((Activity) context).getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops));
            bw.write(contenido.toString());
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db.close();
        return true;
    }

    public int getLastGroup(){
        db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery(Querys.SELECT_FROM_GRUPO,null);
        int grupo_id = 0;

        try{
            cursor.moveToLast();
            grupo_id = cursor.getInt(0);
        }
        catch (Exception e){
            Log.i(TAG, "getLastGroup: operacion fallida");
        }

        db.close();
        return grupo_id;
    }


    public void deleteTable() {
        db = conn.getWritableDatabase();
        db.execSQL(Querys.DELETE_TABLA_DATOS);
        db.execSQL(Querys.DELETE_TABLA_GRUPO);
        Toast.makeText(context, "La BD ha sido vaciada! D:", Toast.LENGTH_SHORT).show();
        db.close();
    }



}

































