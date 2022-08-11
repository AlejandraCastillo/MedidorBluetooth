package com.ake.medidorbluetooth.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class ConnectionSQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "ConnectionSQLiteHelper";

    public ConnectionSQLiteHelper(Context context, String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.i(TAG, "ConnectionSQLiteHelper: Iniciando...");
    }

    @Override
    public void onCreate(@NotNull SQLiteDatabase db) {
        db.execSQL(Querys.CREATE_TABLA_REGISTRO);
        Log.i(TAG, "Se ha creado la tabla Registro!!!");

        db.execSQL(Querys.CREAR_TABLA_DATOS);
        Log.i(TAG, "Se ha creado la tabla Datos!!!");
    }

    @Override
    public void onUpgrade(@NotNull SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: Iniciando...");

        db.execSQL(Querys.DROP_TABLA_DATOS_IF_EXISTS);
        db.execSQL(Querys.DROP_TABLA_REGISTRO_IF_EXISTS);

        Log.i(TAG, "Se ha borrado las tablas de la BD");
        onCreate(db);
    }

}
