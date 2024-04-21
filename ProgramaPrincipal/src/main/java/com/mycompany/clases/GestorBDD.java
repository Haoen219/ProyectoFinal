/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clases;

import com.mycompany.SQL.SQL;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author haoen
 */
public class GestorBDD {

    //dirección predefinida
    private String direccion = "jdbc:sqlite:bddArticulos.db";

    public GestorBDD() {
    }

    public GestorBDD(String direccion) {
        //dirección indicada por el usuario
        this.direccion = direccion;
    }

    // Generador de conexiones
    public Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(direccion);
            System.out.println("Conexión con la BDD establecida.");

            //crear las tablas
            GestorBDD.ejecutar(
                    conn,
                    SQL.sql_tabla_articulo,
                    SQL.sql_tabla_venta,
                    SQL.sql_tabla_venta_articulo
            );

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    //Permite ejecutar multiples comandos
    /**
     * Función que ejecuta los comandos que recibe usando la Connection
     * recibida. Usado para crear tabla.
     *
     * @param conn - objeto Connection.
     * @param statements - Los comandos que se desean ejecutar.
     * @return boolean[] que indica cuales se han ejecutado de forma exitoso.
     */
    public static boolean[] ejecutar(Connection conn, String... statements) {
        boolean[] respuestas = new boolean[statements.length];

        try {
            Statement stmt = conn.createStatement();
            for (int i = 0; i < statements.length; i++) {
                try {
                    stmt.execute(statements[i]);        //asumimos que si no hay excepción, la ejecución es correcto
                    respuestas[i] = true;
                } catch (SQLException e) {
                    respuestas[i] = false;
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return respuestas;
    }
    
    public static boolean ejecutarCRUD(Connection conn, String sql, Articulo articulo) {
        int filasAfectadas = 0;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, articulo.getNombre());
            pstmt.setBigDecimal(2, articulo.getPrecio());
            pstmt.setInt(3, articulo.getID());
            filasAfectadas = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return filasAfectadas>0;
    }
    
    public static boolean ejecutarCRUD(Connection conn, String sql, Venta venta) {
        int filasAfectadas = 0;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, venta.getFecha());
            pstmt.setInt(2, venta.getID());
            filasAfectadas = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return filasAfectadas>0;
    }

    public static boolean ejecutarCRUD(Connection conn, String sql, int id_venta, int id_articulo) {
        int filasAfectadas = 0;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id_venta);
            pstmt.setInt(2, id_articulo);
            filasAfectadas = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return filasAfectadas>0;
    }
    
    public static ResultSet recuperarElementos(Connection conn, String sql) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
            
//            ArrayList<Articulo> articulos = new ArrayList<>();
//            while (rs.next()) {
//                articulos.add(new Articulo(rs.getInt("id"), rs.getString("nombre"), rs.getBigDecimal("precio")));
//            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static boolean desconectar(Connection conn) {
        boolean respuesta = false;
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Se ha cerrado la conexión con la BDD.");
            }
            respuesta = true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return respuesta;
    }
    
//    public static void main(String[] args) {
//        GestorBDD gestor = new GestorBDD();
//        Connection conn = gestor.conectar();
//        
//        Articulo articulo_test = new Articulo(70, "HaoenQPro", BigDecimal.valueOf(69.69));
//        
//        //ORIGINAL
//        Articulo[] lista_articulos = recuperarArticulos(conn);
//        System.out.println("primer Print");
//        for (int i = 0; i< lista_articulos.length; i++) {
//            System.out.println(lista_articulos[i].getID() +"  "+ lista_articulos[i].getNombre() +"  "+ lista_articulos[i].getPrecio());
//        }
//        System.out.println("---");
//        //CREAR
//        ejecutarCRUD(conn, SQL.sql_insertar_articulo, articulo_test);
//        
//        lista_articulos = recuperarArticulos(conn);
//        System.out.println("segundo Print");
//        for (int i = 0; i< lista_articulos.length; i++) {
//            System.out.println(lista_articulos[i].getID() +"  "+ lista_articulos[i].getNombre() +"  "+ lista_articulos[i].getPrecio());
//        }
//        System.out.println("---");
//        //MODIFICAR
//        articulo_test.setNombre("Hamogus");
//        articulo_test.setPrecio(BigDecimal.valueOf(12.00));
//        ejecutarCRUD(conn, SQL.sql_modificar_articulo, articulo_test);
//        
//        lista_articulos = recuperarArticulos(conn);
//        System.out.println("tercer Print");
//        for (int i = 0; i< lista_articulos.length; i++) {
//            System.out.println(lista_articulos[i].getID() +"  "+ lista_articulos[i].getNombre() +"  "+ lista_articulos[i].getPrecio());
//        }
//        System.out.println("---");
//        //BORRAR
//        ejecutarCRUD(conn, SQL.sql_borrar_articulo, articulo_test);
//        
//        lista_articulos = recuperarArticulos(conn);
//        System.out.println("tercer Print");
//        for (int i = 0; i< lista_articulos.length; i++) {
//            System.out.println(lista_articulos[i].getID() +"  "+ lista_articulos[i].getNombre() +"  "+ lista_articulos[i].getPrecio());
//        }
//        System.out.println("---");
//        
//        GestorBDD.desconectar(conn);
//    }
}
