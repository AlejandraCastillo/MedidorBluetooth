package com.ake.medidorbluetooth.database;

public class Querys {

    private static final String TAG = "Querys";

    public static final String DB_DATOS = "db_datos";
    public static final String TABLA_DATOS = "datos";
    public static final String TABLA_REGISTRO = "registro";
    public static final String _DATOS_ID = "datos_id";
    public static final String _TIEMPO = "tiempo";
    public static final String _VOLTAJE = "voltaje";
    public static final String _CORRIENTE = "corriente";
    public static final String _POTENCIA = "potencia";
    public static final String _ENERGIA = "energia";
    public static final String _REGISTRO_ID = "registro_id";
    public static final String _FECHA= "fecha";

    public static final String CREAR_TABLA_DATOS =
            "CREATE TABLE " + TABLA_DATOS + " (" +
                    _DATOS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    _TIEMPO + " REAL NOT NULL, " +
                    _VOLTAJE + " REAL NOT NULL, " +
                    _CORRIENTE + " REAL NOT NULL, " +
                    _POTENCIA + " REAL NOT NULL, " +
                    _ENERGIA + " REAL NOT NULL, " +
                    _REGISTRO_ID + " INTEGER NOT NULL, " +
                    "FOREIGN KEY (" + _REGISTRO_ID + ") " +
                    "REFERENCES " + TABLA_REGISTRO + "(" + _REGISTRO_ID + ")" +
                    ")";

    public static final String CREATE_TABLA_REGISTRO =
            "CREATE TABLE " + TABLA_REGISTRO + " (" +
                    _REGISTRO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    _FECHA + " TEXT NOT NULL" +
                    ")";

    public static final String DROP_TABLA_DATOS_IF_EXISTS=
            "DROP TABLE IF EXISTS " + TABLA_DATOS;

    public static final String DROP_TABLA_REGISTRO_IF_EXISTS =
            "DROP TABLE IF EXISTS " + TABLA_REGISTRO;

    public static final String SELECT_FROM_REGISTRO =
            "SELECT * FROM " + TABLA_REGISTRO;

    public static final String SELECT_FROM_REGISTRO_BY_ID =
            "SELECT " + _FECHA + " FROM " + TABLA_REGISTRO + " WHERE " + _REGISTRO_ID + "=?";

    public static final String SELECT_FROM_DATOS_BY_REGISTRO_ID =
            "SELECT * FROM " + TABLA_DATOS + " WHERE " + _REGISTRO_ID + "=?";

}
