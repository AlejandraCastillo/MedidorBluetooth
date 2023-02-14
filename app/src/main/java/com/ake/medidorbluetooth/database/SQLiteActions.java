package com.ake.medidorbluetooth.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

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
        conn = new ConnectionSQLiteHelper(context, Querys.DB_DATOS, null, 5);
    }

    public static @NotNull String getDate(String formato){
        long ahora = System.currentTimeMillis();
        Date fecha = new Date(ahora);
        DateFormat df = new SimpleDateFormat(formato);
        return df.format(fecha);
    }

    public int addNewRegister(){
        db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Querys._FECHA, getDate("yy-MM-dd"));

        int registro = (int) db.insert(Querys.TABLA_REGISTRO, Querys._REGISTRO_ID, values);

        db.close();
        return registro;
    }

    public int getLastRegister(){
        db = conn.getReadableDatabase();

        Cursor cursor = db.rawQuery(Querys.SELECT_FROM_REGISTRO, null);

        cursor.moveToLast();
        int registro;
        try{
            registro = cursor.getInt(0);
        }catch (CursorIndexOutOfBoundsException e){
            registro = 0;
        }

        db.close();
        return registro;
    }

    public void addnewDataRow(@NotNull TablaDatos row){
        db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Querys._TIEMPO, row.getTiempo());
        values.put(Querys._VOLTAJE, row.getVoltaje());
        values.put(Querys._CORRIENTE, row.getCorriente());
        values.put(Querys._POTENCIA, row.getPotencia());
        values.put(Querys._ENERGIA, row.getEnergia());
        values.put(Querys._REGISTRO_ID, row.getRegistroID());

        db.insert(Querys.TABLA_DATOS, Querys._DATOS_ID, values);

        db.close();
    }

    public ArrayList<TablaRegistro> readTablaRegistro(){
        ArrayList<TablaRegistro> listaTablaRegistro;

        db = conn.getReadableDatabase();

        TablaRegistro registro = null;
        listaTablaRegistro = new ArrayList<TablaRegistro>();

        Cursor cursor = db.rawQuery(Querys.SELECT_FROM_REGISTRO, null);

        cursor.moveToLast();

        try{
            do {
                registro = new TablaDatos();
                registro.setRegistroID(cursor.getInt(0));
                registro.setFecha(cursor.getString(1));

                listaTablaRegistro.add(registro);

            } while (cursor.moveToPrevious());
        }catch(CursorIndexOutOfBoundsException e){
            Log.e(TAG, "La base de datos está vacia");
        }

        db.close();
        return listaTablaRegistro;
    }

    public String getFechaRegistro(@NotNull Integer registro_id){
        db = conn.getReadableDatabase();
        String fecha = "";
        String[] parametros = {registro_id.toString()};

        try{
            Cursor cursor = db.rawQuery(Querys.SELECT_FROM_REGISTRO_BY_ID, parametros);

            cursor.moveToFirst();
            fecha = cursor.getString(0);

        }
        catch (Exception e){
            Log.i(TAG, "getRegisterByID: Operacion fallida");
        }

        db.close();
        return fecha;
    }

    private @NotNull ArrayList<TablaDatos> readRegisterFromTablaDatos(@NotNull Integer registro_id){

        ArrayList<TablaDatos> listaTablaDatos;

        db = conn.getReadableDatabase();

        TablaDatos row = null;
        listaTablaDatos = new ArrayList<TablaDatos>();
        String args[]={registro_id.toString()};

        try {
            Cursor cursor = db.rawQuery(Querys.SELECT_FROM_DATOS_BY_REGISTRO_ID, args);

            while (cursor.moveToNext()) {
                row = new TablaDatos();
                row.setTiempo(cursor.getLong(1));
                row.setVoltaje(cursor.getDouble(2));
                row.setCorriente(cursor.getDouble(3));
                row.setPotencia(cursor.getDouble(4));
                row.setEnergia(cursor.getDouble(5));

                int registro = cursor.getInt(6);
                row.setRegistroID(registro);
                row.setFecha(getFechaRegistro(registro));

                listaTablaDatos.add(row);
            }
        }
        catch (Exception e){
            Log.i(TAG, "getRegisterByID: Operacion fallida");
        }

        db.close();
        return listaTablaDatos;
    }

    public boolean createDocument(Uri uri, Integer registro_id){
        db = conn.getWritableDatabase();
        StringBuilder contenido = new StringBuilder("Tiempo,Voltaje,Corriente,Potencia,Energia\n"); //se usa para crear una cadena a partes

        ArrayList<TablaDatos> listaTablaDatos = readRegisterFromTablaDatos(registro_id);

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

    public void borrarBD(){
        db = conn.getWritableDatabase();
        db.execSQL(Querys.DELETE_FROM_TABLA_REGISTRO);
        db.execSQL(Querys.DELETE_FROM_TABLA_DATOS);
    }

}



























