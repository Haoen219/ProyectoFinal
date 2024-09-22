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

    //usado para enviar datos al cliente móvil
    public String toString() {
        return this.ID + "|" + this.nombre + "|" + this.precio_compra + "|" + this.precio + "|" + this.almacen;
    }

    //usado para recuperar los datos desde un string
    public void updateFromString(String str) {
        String[] campos = str.split("\\|");

        if (campos.length != 5) {
            throw new IllegalArgumentException("Formato de cadena no válido para Articulo.");
        }

        setNombre(campos[1]);
        setPrecioCompra(new BigDecimal(campos[2]));
        setPrecio(new BigDecimal(campos[3]));
        setAlmacen(campos[4]);
    }
    
    //usado para crear nuevo articulo desde el String
    public static Articulo fromString(String str) {
        String[] campos = str.split("\\|");

        if (campos.length != 5) {
            throw new IllegalArgumentException("Formato de cadena no válido para Articulo.");
        }

        BigInteger id = new BigInteger(campos[0]);
        String nombre = campos[1];
        BigDecimal precioCompra = new BigDecimal(campos[2]);
        BigDecimal precio = new BigDecimal(campos[3]);
        String almacen = campos[4];

        return new Articulo(id, nombre, precioCompra, precio, almacen);
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
