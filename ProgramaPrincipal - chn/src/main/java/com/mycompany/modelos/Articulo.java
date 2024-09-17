package com.mycompany.modelos;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Articulo {

    private BigInteger ID;
    private String nombre;
    private BigDecimal precio_compra;
    private BigDecimal precio;
    private String almacen;

    public Articulo(BigInteger id, String nombre, BigDecimal precio_compra, BigDecimal precio, String almacen) {
        this.ID = id;
        this.nombre = nombre;
        this.precio_compra = precio_compra;
        this.precio = precio;
        this.almacen = almacen;
    }
    
    public Articulo(BigInteger id, String nombre, BigDecimal precio) {
        this.ID = id;
        this.nombre = nombre;
        this.precio = precio;
        
        this.precio_compra = BigDecimal.ZERO;
        this.almacen = "";
    }

    //GETTER
    public BigInteger getID() {
        return this.ID;
    }

    public String getNombre() {
        return this.nombre;
    }

    public BigDecimal getPrecioCompra() {
        return this.precio_compra;
    }

    public BigDecimal getPrecio() {
        return this.precio;
    }

    public String getAlmacen() {
        return this.almacen;
    }

    //SETTER
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecioCompra(BigDecimal precio) {
        this.precio = precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public void setAlmacen(String nombre) {
        this.almacen = nombre;
    }
}
