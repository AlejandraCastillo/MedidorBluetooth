package com.icat.javablue.database;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.security.PublicKey;

/**
 * Esta clase almacena los Strings necesarios para
 * realizar consultas a la base de datos
 * @author: María Alejandra Castillo Martínez
 */
public class Querys {

    private static final String TAG = "Querys";

    public static final String DB_DATOS = "db_datos";
    public static final String TABLA_DATOS = "datos";
    public static final String TABLA_GRUPO = "grupo";
    public static final String _DATOS_ID = "datos_id";
    public static final String _TIEMPO = "tiempo";
    public static final String _VOLTAJE = "voltaje";
    public static final String _CORRIENTE = "corriente";
    public static final String _POTENCIA = "potencia";
    public static final String _ENERGIA = "energia";
    public static final String _GRUPO_ID = "grupo_id";
    public static final String _FECHA= "fecha";

    /**
     * Sentencia empleada para crear la tabla Datos
     */
    public static final String CREAR_TABLA_DATOS =
            "CREATE TABLE " + TABLA_DATOS + " (" +
                    _DATOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    _TIEMPO + " REAL NOT NULL, " +
                    _VOLTAJE + " REAL NOT NULL, " +
                    _CORRIENTE + " REAL NOT NULL, " +
                    _POTENCIA + " REAL NOT NULL, " +
                    _ENERGIA + " REAL NOT NULL, " +
                    _GRUPO_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + _GRUPO_ID + ") " +
                    "REFERENCES " + TABLA_GRUPO + "(" + _GRUPO_ID + ")" +
                    ")";

    /**
     * Sentencia empleada para crear la tabla Grupo
     */
    public static final String CREATE_TABLA_GRUPO =
            "CREATE TABLE " + TABLA_GRUPO + " (" +
                    _GRUPO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    _FECHA + " TEXT NOT NULL" +
                    ")";

    /**
     * Query empleado para eliminar la tabla Datos en caso de existir
     */
    public static final String DROP_TABLA_DATOS_IF_EXISTS=
            "DROP TABLE IF EXISTS " + TABLA_DATOS;

    /**
     * Query empleado para eliminar la tabla Grupo en caso de existir
     */
    public static final String DROP_TABLA_GRUPO_IF_EXISTS=
            "DROP TABLE IF EXISTS " + TABLA_GRUPO;

    /**
     * Query empleado para obtener los datos de la tabla grupo
     */
    public static final String SELECT_FROM_GRUPO =
            "SELECT * FROM " + TABLA_GRUPO;

    /**
     * Query empelado para obtener la fecha de un Grupo en la
     * tabla Grupo empleando el id de dicho Grupo
     */
    public static final String SELECT_FROM_GRUPO_BY_ID =
            "SELECT " + _FECHA + " FROM " + TABLA_GRUPO + " WHERE " + _GRUPO_ID + "=?";

    /**
     * Query empelado para obtener un Grupo de datos de la tabla Datos
     * empleando el id de dicho Grupo
     */
    public static final String SELECT_FROM_DATOS_BY_GRUPO_ID =
            "SELECT * FROM " + TABLA_DATOS + " WHERE " + _GRUPO_ID + "=?";



}