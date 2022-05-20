package com.ake.medidorbluetooth.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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

    ConnectionSQLiteHelper conn;
    SQLiteDatabase db;
    Context context;

    public SQLiteActions(Context context) {
        this.context = context;
        conn = new ConnectionSQLiteHelper(context, Querys.DB_DATOS, null, 1);
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

    public void addnewRow(TablaDatos row){
        db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Querys._TIEMPO, row.getTiempo());
        values.put(Querys._VOLTAJE, row.getVoltaje());
        values.put(Querys._CORRIENTE, row.getCorriente());
        values.put(Querys._POTENCIA, row.getPotencia());
        values.put(Querys._ENERGIA, row.getEnergia());
        values.put(Querys._GRUPO_ID, row.getGrupoID());

        db.insert(Querys.TABLA_DATOS, Querys._DATOS_ID, values);

        db.close();
    }

    public ArrayList<TablaGrupo> readTablaGrupo(){
        ArrayList<TablaGrupo> listaTablaGrupo;

        db = conn.getReadableDatabase();

        TablaGrupo grupo = null;
        listaTablaGrupo = new ArrayList<TablaGrupo>();

        Cursor cursor = db.rawQuery(Querys.SELECT_FROM_GRUPO, null);

        while(cursor.moveToNext()){
            grupo = new TablaDatos();
            grupo.setGrupoID(cursor.getInt(0));
            grupo.setFecha(cursor.getString(1));

            listaTablaGrupo.add(grupo);

        }

        db.close();
        return listaTablaGrupo;
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

    public ArrayList<TablaDatos> readGroupFromTablaDatos(Integer grupo_id){

        ArrayList<TablaDatos> listaTablaDatos;

        db = conn.getReadableDatabase();

        TablaDatos row = null;
        listaTablaDatos = new ArrayList<TablaDatos>();
        String args[]={grupo_id.toString()};

        Cursor cursor = db.rawQuery(Querys.SELECT_FROM_DATOS_BY_GRUPO_ID,args);

        while (cursor.moveToNext()){
            row = new TablaDatos();
            row.setTiempo(cursor.getInt(1));
            row.setVoltaje(cursor.getDouble(2));
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

        ArrayList<TablaDatos> listaTablaDatos = readGroupFromTablaDatos(grupo_id);

        for (int i = 0; i < listaTablaDatos.size(); i++) {
            contenido.append(listaTablaDatos.get(i).getTiempo().toString()).append(","); //TIEMPO
            contenido.append(listaTablaDatos.get(i).getVoltaje().toString()).append(","); //VOLTAJE
            contenido.append(listaTablaDatos.get(i).getCorriente().toString()).append(","); //CORRIENTE
            contenido.append(listaTablaDatos.get(i).getPotencia().toString()).append(","); //POTENCIA
            contenido.append(listaTablaDatos.get(i).getEnergia().toString()).append("\n"); //ENERGIA
        }

        try{
            OutputStream ops = ((Activity) context).getContentResolver().openOutputStream(uri);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops));
            bw.write(contenido.toString());
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        db.close();
        return true;
    }

}



























