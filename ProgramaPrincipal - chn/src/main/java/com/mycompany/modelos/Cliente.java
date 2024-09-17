/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.modelos;

/**
 *
 * @author haoen
 */
public class Cliente {
    private String DNI;
    private String nombre;
    private String direccion;
    private String telefono;

    public Cliente(String dni, String nombre, String direccion, String telefono) {
        this.DNI = dni;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }
    
    /**
     * @return the DNI
     */
    public String getDNI() {
        return DNI;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }
}
