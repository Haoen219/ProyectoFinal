/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.SQL to edit this template
 */
package com.mycompany.SQL;

/**
 *
 * @author haoen
 */
public class SQL {

    static final String NOMBRE_ARTICULOS = "articulos";
    static final String ARTICULOS_NOMBRE = "nombre";
    static final String ARTICULOS_PRECIO = "precio";
    static final String NOMBRE_VENTAS = "ventas";
    static final String VENTAS_FECHA = "fecha";
    static final String NOMBRE_VENTA_ARTICULO = "venta_articulo";
    static final String RELACION_VENTA_ID = "venta_id";
    static final String RELACION_ARTICULO_ID = "articulo_id";

    //Tablas nuevas
    public static String sql_tabla_articulo
            = "CREATE TABLE IF NOT EXISTS " + NOMBRE_ARTICULOS + " (\n"
            + "    id INTEGER PRIMARY KEY,\n"
            + "   " + ARTICULOS_NOMBRE + " TEXT NOT NULL,\n"
            + "   " + ARTICULOS_PRECIO + " DECIMAL\n"
            + ");";

    public static String sql_tabla_venta
            = "CREATE TABLE IF NOT EXISTS " + NOMBRE_VENTAS + " (\n"
            + "    id INTEGER PRIMARY KEY,\n"
            + "   " + VENTAS_FECHA + " DATE\n"
            + ");";

    public static String sql_tabla_venta_articulo
            = "CREATE TABLE IF NOT EXISTS " + NOMBRE_VENTA_ARTICULO + " (\n"
            + "   " + RELACION_VENTA_ID + " INTEGER,\n"
            + "   " + RELACION_ARTICULO_ID + " INTEGER,\n"
            + "    FOREIGN KEY (" + RELACION_VENTA_ID + ") REFERENCES " + NOMBRE_VENTAS + "(id),\n"
            + "    FOREIGN KEY (" + RELACION_ARTICULO_ID + ") REFERENCES " + NOMBRE_ARTICULOS + "(id),\n"
            + "    PRIMARY KEY (" + RELACION_VENTA_ID + "," + RELACION_ARTICULO_ID + ")\n"
            + ");";

    public static String sql_leer_articulo = "SELECT * FROM " + NOMBRE_ARTICULOS;
    public static String sql_insertar_articulo = "INSERT INTO " + NOMBRE_ARTICULOS + "(" + ARTICULOS_NOMBRE + "," + ARTICULOS_PRECIO + ", id) VALUES(?,?,?)";
    public static String sql_modificar_articulo = "UPDATE " + NOMBRE_ARTICULOS + " SET " + ARTICULOS_NOMBRE + " = ? , " + ARTICULOS_PRECIO + " = ? WHERE id = ?";
    public static String sql_borrar_articulo = "DELETE FROM " + NOMBRE_ARTICULOS + " WHERE " + ARTICULOS_NOMBRE + " = ? AND " + ARTICULOS_PRECIO + " = ? AND id = ?";
}
