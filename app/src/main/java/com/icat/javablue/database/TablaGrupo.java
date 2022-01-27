package com.icat.javablue.database;

/**
 * Esta clase crea un objeto que representa una fila de la tabla Grupo
 * @author: María Alejandra Castillo Martínez
 */
public class TablaGrupo {

    private static final String TAG = "TablaGrupo";

    private Integer grupo_id;
    private String fecha;

    /**
     * Constructor de la clase
     */
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
