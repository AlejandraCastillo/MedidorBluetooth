package com.icat.javablue.database;

import java.sql.Time;

public class TablaDatos {

    private static final String TAG = "TablaDatos";

    private Integer tiempo;
    private Double volaje;
    private Double corriente;
    private Double potencia;
    private Double energia;
    private Integer grupo_id;
    private String fecha;

    public TablaDatos() {
    }

    public Integer getTiempo() {
        return tiempo;
    }

    public void setTiempo(Integer tiempo) {
        this.tiempo = tiempo;
    }

    public Double getVolaje() {
        return volaje;
    }

    public void setVolaje(Double volaje) {
        this.volaje = volaje;
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

    public Integer getGrupoID() {
        return grupo_id;
    }

    public void setGrupoID(Integer grupo_id) {
        this.grupo_id = grupo_id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
