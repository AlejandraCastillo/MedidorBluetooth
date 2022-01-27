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

/**
 * Clase que daministra todas las acciones que es posible realizar
 * sobre la base de datos
 * @author: María Alejandra Castillo Martínez
 */
public class SQLiteActions {

    private static final String TAG = "SQLiteActions";


    ConexionSQLiteHelper conn;
    SQLiteDatabase db;
    Context context;

    /**
     * Es el constructor de la clase
     * @param context
     */
    public SQLiteActions(Context context) {
        this.context = context;
        conn = new ConexionSQLiteHelper(context, Querys.DB_DATOS, null, 1);
    }

    /**
     * Metodo empleado para obetner la fecha de creación de un Grupo de datos
     * @param grupo_id El id del grupo que se desea consultar
     * @return La fecha en que se creó el grupo consultado
     */
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

    /**
     * Crea un nuevo Grupo en la tabla Grupo
     * @return El grupo creado
     */
    public int addNewGroup(){
        db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Querys._FECHA, getDate());

        int grupo = (int) db.insert(Querys.TABLA_GRUPO, Querys._GRUPO_ID, values);

        db.close();
        return grupo;
    }

    /**
     * Crea una nueva fila en la Tabla datos
     * @param row Un objeto de tipo TablaDatos con los datos a almacenar
     */
    public void addNewRow(TablaDatos row){
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

    /**
     * Obtene los datos de un grupo especifico de la Tabla Datos
     * @return Una lista de objetso tipo TablaDatos con los datos obtenidos
     */
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

    /**
     * Obtiene todos los datos de la tabla Grupo
     * @return Una lista de objetso tipo TablaGrupo con los datos obtenidos
     */
    public ArrayList<TablaGrupo> readTablaGrupo(){

        ArrayList<TablaGrupo> listaTablaGrupo;

        db = conn.getReadableDatabase();

        TablaGrupo grupo = null;
        listaTablaGrupo = new ArrayList<TablaGrupo>();

        Cursor cursor = db.rawQuery(Querys.SELECT_FROM_GRUPO,null);

        while (cursor.moveToNext()){
            grupo = new TablaDatos();
            grupo.setGrupoID(cursor.getInt(0));
            grupo.setFecha(cursor.getString(1));

            listaTablaGrupo.add(grupo);
        }

        db.close();
        return listaTablaGrupo;
    }

    /**
     * Crea un documento *.cvs con los datos de un Grupo de la tabla Datos
     * @param uri
     * @param grupo_id El id del grupo solicitado
     * @return true si la operacion fue exitosa
     */
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

































