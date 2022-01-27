package com.icat.javablue.database;

import android.util.Log;

/**
 * Esta clase crea un objeto que representa una fila de la tabla Datos
 * @author: María Alejandra Castillo Martínez
 */
public class TablaDatos extends TablaGrupo {

    private static final String TAG = "TablaDatos";

    private Integer tiempo;
    private Double voltaje;
    private Double corriente;
    private Double potencia;
    private Double energia;

    /**
     * Constructor de la clase
     */
    public TablaDatos() {
    }

    public Integer getTiempo() {
        return tiempo;
    }

    public void setTiempo(Integer tiempo) {
        this.tiempo = tiempo;
    }

    public Double getVoltaje() {
        return voltaje;
    }

    public void setVoltaje(Double voltaje) {
        this.voltaje = voltaje;
    }

    public Double getCorriente() {
        return corriente;
    }

    public void setCorriente(Double corriente) {
        this.corriente = corriente;
    }

    public Double getPotencia() {
        return potencia;
    }

    public void setPotencia(Double potencia) {
        this.potencia = potencia;
    }

    public Double getEnergia() {
        return energia;
    }

    public void setEnergia(Double energia) {
        this.energia = energia;
    }

//    public void printRow(String tag){
//        String row = "T=" + tiempo + " V=" + voltaje + " I=" + corriente + " P=" + potencia + " E=" + energia;
//        Log.i(tag, row);
//    }

}
