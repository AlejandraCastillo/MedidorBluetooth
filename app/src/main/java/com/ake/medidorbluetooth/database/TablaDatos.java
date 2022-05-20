package com.ake.medidorbluetooth.database;

import android.util.Log;

public class TablaDatos extends TablaGrupo{

    private Integer tiempo;
    private Double voltaje;
    private Double corriente;
    private Double potencia;
    private Double energia;

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

    public void printRow(String tag){
        String row = "T=" + tiempo + " V=" + voltaje + " I=" + corriente + " P=" + potencia + " E=" + energia;
        Log.i(tag, row);
    }
}
