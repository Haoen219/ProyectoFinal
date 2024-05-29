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

    public static final String NOMBRE_ARTICULOS = "articulos";
    public static final String ARTICULOS_NOMBRE = "nombre";
    public static final String ARTICULOS_PRECIO = "precio";

    public static final String NOMBRE_VENTAS = "ventas";
    public static final String VENTAS_FECHA = "fecha";

    public static final String NOMBRE_VENTA_ARTICULO = "venta_articulo";
    public static final String RELACION_VENTA_ID = "venta_id";
    public static final String RELACION_ARTICULO_ID = "articulo_id";
    public static final String RELACION_CANTIDAD = "cantidad";

    //Tablas nuevas
    public static String sql_tabla_articulo
            = "CREATE TABLE IF NOT EXISTS " + NOMBRE_ARTICULOS + " (\n"
            + "    id BIGINT PRIMARY KEY,\n"
            + "   " + ARTICULOS_NOMBRE + " TEXT NOT NULL,\n"
            + "   " + ARTICULOS_PRECIO + " DECIMAL\n"
            + ");";

    public static String sql_tabla_venta
            = "CREATE TABLE IF NOT EXISTS " + NOMBRE_VENTAS + " (\n"
            + "    id BIGINT PRIMARY KEY,\n"
            + "   " + VENTAS_FECHA + " DATE\n"
            + ");";

    public static String sql_tabla_venta_articulo
            = "CREATE TABLE IF NOT EXISTS " + NOMBRE_VENTA_ARTICULO + " (\n"
            + "   " + RELACION_VENTA_ID + " BIGINT,\n"
            + "   " + RELACION_ARTICULO_ID + " BIGINT,\n"
            + "   " + RELACION_CANTIDAD + " INTEGER,\n"
            + "    FOREIGN KEY (" + RELACION_VENTA_ID + ") REFERENCES " + NOMBRE_VENTAS + "(id),\n"
            + "    FOREIGN KEY (" + RELACION_ARTICULO_ID + ") REFERENCES " + NOMBRE_ARTICULOS + "(id),\n"
            + "    PRIMARY KEY (" + RELACION_VENTA_ID + "," + RELACION_ARTICULO_ID + ")\n"
            + ");";

    //ARTICULOS
    public static String sql_leer_articulos = "SELECT * FROM " + NOMBRE_ARTICULOS;
    public static String sql_leer_articulo_id = "SELECT * FROM " + NOMBRE_ARTICULOS + " WHERE id = ?";
    public static String sql_insertar_articulo = "INSERT INTO " + NOMBRE_ARTICULOS + "(" + ARTICULOS_NOMBRE + "," + ARTICULOS_PRECIO + ", id) VALUES(?,?,?)";
    public static String sql_modificar_articulo = "UPDATE " + NOMBRE_ARTICULOS + " SET " + ARTICULOS_NOMBRE + " = ? , " + ARTICULOS_PRECIO + " = ? WHERE id = ?";
    public static String sql_borrar_articulo = "DELETE FROM " + NOMBRE_ARTICULOS + " WHERE id = ?";
    //VENTAS
    public static String sql_leer_ventas = "SELECT * FROM " + NOMBRE_VENTAS;
    public static String sql_insertar_venta = "INSERT INTO " + NOMBRE_VENTAS + "(" + VENTAS_FECHA + ", id) VALUES(?,?)";
    public static String sql_modificar_venta = "UPDATE " + NOMBRE_VENTAS + " SET " + VENTAS_FECHA + " = ?, WHERE id = ?";
    public static String sql_borrar_venta = "DELETE FROM " + NOMBRE_VENTAS + " WHERE id = ?";
    //RELACIONES
    public static String sql_leer_relaciones = "SELECT * FROM " + NOMBRE_VENTA_ARTICULO;
    public static String sql_leer_relacion = "SELECT * FROM " + NOMBRE_VENTA_ARTICULO + " WHERE " + RELACION_CANTIDAD + " = ? AND " + RELACION_VENTA_ID + " = ? AND " + RELACION_ARTICULO_ID + " = ?";
    public static String sql_insertar_relacion = "INSERT INTO " + NOMBRE_VENTA_ARTICULO + "(" + RELACION_CANTIDAD + "," + RELACION_VENTA_ID + "," + RELACION_ARTICULO_ID + ") VALUES(?,?,?)";
    public static String sql_modificar_relacion = "UPDATE " + NOMBRE_VENTA_ARTICULO + " SET " + RELACION_CANTIDAD + " = ? , WHERE " + RELACION_VENTA_ID + " = ? AND " + RELACION_ARTICULO_ID + " = ?";
    public static String sql_borrar_relacion = "DELETE FROM " + NOMBRE_VENTA_ARTICULO + " WHERE " + RELACION_VENTA_ID + " = ? AND " + RELACION_ARTICULO_ID + " = ?";
    public static String sql_borrar_relacion_venta = "DELETE FROM " + NOMBRE_VENTA_ARTICULO + " WHERE " + RELACION_VENTA_ID + " = ?";
    public static String sql_borrar_relacion_articulo = "DELETE FROM " + NOMBRE_VENTA_ARTICULO + " WHERE " + RELACION_VENTA_ID + " = ?";

    public static String sql_leer_ventas_tabla
            = "SELECT " + NOMBRE_VENTAS + ".id AS id,"
            + NOMBRE_VENTAS + "." + VENTAS_FECHA + " AS Fecha,"
            + NOMBRE_ARTICULOS + "." + ARTICULOS_NOMBRE + " AS Articulo,"
            + NOMBRE_VENTA_ARTICULO + "." + RELACION_CANTIDAD + " AS Cantidad,"
            + NOMBRE_ARTICULOS + "." + ARTICULOS_PRECIO + " AS 'Precio unitario',"
            + NOMBRE_VENTA_ARTICULO + "." + RELACION_CANTIDAD + " * " + NOMBRE_ARTICULOS + ".precio AS Total"
            + " FROM " + NOMBRE_VENTAS + " JOIN " + NOMBRE_VENTA_ARTICULO + " ON " + NOMBRE_VENTAS + ".id = " + NOMBRE_VENTA_ARTICULO + "." + RELACION_VENTA_ID
            + " JOIN " + NOMBRE_ARTICULOS + " ON " + NOMBRE_VENTA_ARTICULO + "." + RELACION_ARTICULO_ID + " = " + NOMBRE_ARTICULOS + ".id";
    public static String sql_leer_ventas_tabla_filtro
            = "SELECT " + NOMBRE_VENTAS + ".id AS id, "
            + NOMBRE_VENTAS + "." + VENTAS_FECHA + " AS Fecha, "
            + NOMBRE_ARTICULOS + "." + ARTICULOS_NOMBRE + " AS Articulo, "
            + NOMBRE_VENTA_ARTICULO + "." + RELACION_CANTIDAD + " AS Cantidad, "
            + NOMBRE_ARTICULOS + "." + ARTICULOS_PRECIO + " AS 'Precio unitario', "
            + NOMBRE_VENTA_ARTICULO + "." + RELACION_CANTIDAD + " * " + NOMBRE_ARTICULOS + ".precio AS Total"
            + " FROM " + NOMBRE_VENTAS + " JOIN " + NOMBRE_VENTA_ARTICULO + " ON " + NOMBRE_VENTAS + ".id = " + NOMBRE_VENTA_ARTICULO + "." + RELACION_VENTA_ID
            + " JOIN " + NOMBRE_ARTICULOS + " ON " + NOMBRE_VENTA_ARTICULO + "." + RELACION_ARTICULO_ID + " = " + NOMBRE_ARTICULOS + ".id"
            + " WHERE LOWER(" + NOMBRE_VENTAS + ".id) LIKE ? OR LOWER(" + NOMBRE_VENTAS + "." + VENTAS_FECHA + ") LIKE ? OR LOWER(" + NOMBRE_ARTICULOS + "." + ARTICULOS_NOMBRE + ") LIKE ?";

    public static String sql_leer_articulos_filtro
            = "SELECT * FROM " + NOMBRE_ARTICULOS
            + " WHERE LOWER(id) LIKE ? OR LOWER(" + ARTICULOS_NOMBRE + ") LIKE ?";
}
