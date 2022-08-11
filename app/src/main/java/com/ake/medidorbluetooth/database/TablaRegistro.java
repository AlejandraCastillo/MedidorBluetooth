package com.ake.medidorbluetooth.database;

public class TablaRegistro {
    private static final String TAG = "TablaRegistro";

    private Integer registro_id;
    private String fecha;

    public TablaRegistro() {
    }

    public Integer getRegistroID() {
        return registro_id;
    }

    public void setRegistroID(Integer registro_id) {
        this.registro_id = registro_id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
