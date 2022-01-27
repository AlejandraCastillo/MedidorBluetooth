package com.icat.javablue.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * Esta clase es la encargada de crear la base de datos de la aplicacion,
 * actualizarla e inicializar conecciones hacía la misma.
 * @author: María Alejandra Castillo Martínez
 */
public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "ConexionSQLiteHelper";

    /**
     * Inicializa una coneccion hacia la base de datos
     * @param context El contexto del Activity desde el que se manda a llamar
     * @param name Nombre de la base de datos a la que se desea acceder
     * @param factory
     * @param version Version de la base a la que se desea acceder
     */
    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i(TAG, "ConexionSQLiteHelper: Iniciando...");
    }

    /**
     * Crea la base de datos en caso de no existir
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: Iniciando...");

        db.execSQL(Querys.CREATE_TABLA_GRUPO);
            Log.i(TAG, Querys.CREATE_TABLA_GRUPO);
        Log.i(TAG, "Se ha creado la tabla Grupo!!!");

        db.execSQL(Querys.CREAR_TABLA_DATOS);
            Log.i(TAG, Querys.CREAR_TABLA_DATOS);
        Log.i(TAG, "Se ha creado la tabla Datos!!!");
    }

    /**
     * Actualiza la base de daros de ser necesario
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: Iniciando...");

        db.execSQL(Querys.DROP_TABLA_DATOS_IF_EXISTS);
            Log.i(TAG, Querys.DROP_TABLA_DATOS_IF_EXISTS);

        db.execSQL(Querys.DROP_TABLA_GRUPO_IF_EXISTS);
            Log.i(TAG, Querys.DROP_TABLA_GRUPO_IF_EXISTS);

        Log.i(TAG, "Se ha borrado las tablas de la BD");
        onCreate(db);
    }
}
