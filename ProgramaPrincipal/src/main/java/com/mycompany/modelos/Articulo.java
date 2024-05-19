package com.mycompany.modelos;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Articulo {
    private BigInteger ID;
    private String nombre;
    private BigDecimal precio;

    public Articulo (BigInteger id, String nombre, BigDecimal precio){
        this.ID = id;
        this.nombre = nombre;
        this.precio = precio;
    }
    
    //GETTER
    public BigInteger getID() {
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
