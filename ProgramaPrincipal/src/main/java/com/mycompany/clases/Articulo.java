package com.mycompany.clases;

import java.math.BigDecimal;

public class Articulo {
    private final int ID;
    private String nombre;
    private BigDecimal precio;

    public Articulo (int id, String nombre, BigDecimal precio){
        this.ID = id;
        this.nombre = nombre;
        this.precio = precio;
    }
    
    //GETTER
    public int getID() {
        return this.ID;
    }
    public String getNombre() {
        return this.nombre;
    }
    public BigDecimal getPrecio() {
        return this.precio;
    }
    //SETTER
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

}
