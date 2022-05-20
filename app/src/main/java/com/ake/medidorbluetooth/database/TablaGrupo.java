package com.ake.medidorbluetooth.database;

public class TablaGrupo {
    private static final String TAG = "TablaGrupo";

    private Integer grupo_id;
    private String fecha;

    public TablaGrupo() {
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
