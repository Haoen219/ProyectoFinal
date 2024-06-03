/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import static com.mycompany.clases.Funciones.mostrarExcepcion;
import com.mycompany.SQL.SQL;
import com.mycompany.modelos.Articulo;
import com.mycompany.modelos.Venta;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 *
 * @author haoen
 */
public class GestorBDD {

    // Generador de conexiones
    public static Connection conectar() {
        Connection conn = null;
        String direccion = "";
        
        // Obtener el directorio de documentos del usuario y actualizar la dirección de la base de datos
        Path documentsDirectory = getDocumentsDirectory();
        if (documentsDirectory != null) {
            direccion = "jdbc:sqlite:" + documentsDirectory.resolve("bddArticulos.db").toString();
            System.out.println(direccion);
        } else {
            System.out.println("No se pudo encontrar el directorio de documentos del usuario.");
        }
        
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
            mostrarExcepcion(e);
        }
        return conn;
    }

    private static Path getDocumentsDirectory() {
        Path userHome = Paths.get(System.getProperty("user.home"));
        Path[] possibleDocumentPaths = {
            userHome.resolve("Documents"), // Inglés
            userHome.resolve("Documentos"), // Español
            userHome.resolve("Mis documentos") // Otra posibilidad en español
        // Puedes añadir más posibilidades aquí según sea necesario para otros idiomas
        };

        // Intentar detectar el nombre correcto del directorio de documentos
        for (Path path : possibleDocumentPaths) {
            if (Files.exists(path)) {
                return path;
            }
        }

        return null; // Si no se encuentra ningún directorio de documentos conocido
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
            mostrarExcepcion(e);
        }

        return respuestas;
    }

    public static boolean articuloEjecutarCRUD(Connection conn, String sql, Articulo articulo) {
        int filasAfectadas = 0;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (sql.startsWith("DELETE")) {
                pstmt.setString(1, articulo.getID().toString());
            } else {
                pstmt.setString(1, articulo.getNombre());
                pstmt.setBigDecimal(2, articulo.getPrecio());
                pstmt.setString(3, articulo.getID().toString());
            }
            filasAfectadas = pstmt.executeUpdate();
        } catch (SQLException e) {
            mostrarExcepcion(e);
        }
        return filasAfectadas > 0;
    }

    public static boolean ventaEjecutarCRUD(Connection conn, String sql, Venta venta) {
        int filasAfectadas = 0;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (sql.startsWith("DELETE")) {
                pstmt.setString(1, venta.getID().toString());
            } else {
                pstmt.setTimestamp(1, Timestamp.valueOf(venta.getFecha()));
                pstmt.setString(2, venta.getID().toString());
            }
            filasAfectadas = pstmt.executeUpdate();
        } catch (SQLException e) {
            mostrarExcepcion(e);
        }

        return filasAfectadas > 0;
    }

    // Se ha complicado el uso de esto :(
    public static boolean relacionEjecutarCRUD(Connection conn, String sql, BigInteger id_venta, BigInteger id_articulo, int cantidad) {
        int filasAfectadas = 0;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (sql.startsWith("DELETE")) {
                int parameterCount = pstmt.getParameterMetaData().getParameterCount();
                if (parameterCount == 1) {
                    if (sql.contains(SQL.NOMBRE_VENTA_ARTICULO)) {              //Si se usa el SQL.sql_borrar_relacion_venta o SQL.sql_borrar_relacion_articulo
                        pstmt.setString(1, id_venta.toString());
                    } else {
                        pstmt.setString(1, id_articulo.toString());
                    }
                } else if (parameterCount == 2) {
                    pstmt.setString(1, id_venta.toString());
                    pstmt.setString(2, id_articulo.toString());
                }
            } else {
                pstmt.setInt(1, cantidad);
                pstmt.setString(2, id_venta.toString());
                pstmt.setString(3, id_articulo.toString());
            }
            filasAfectadas = pstmt.executeUpdate();
        } catch (SQLException e) {
            mostrarExcepcion(e);
        }

        return filasAfectadas > 0;
    }

    public static ResultSet recuperarElementos(Connection conn, String sql) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            mostrarExcepcion(e);
        }
        return null;
    }

    public static ResultSet recuperarArticulosFiltrado(Connection conn, String valor) {
        valor = "%" + valor.toLowerCase() + "%";

        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL.sql_leer_articulos_filtro);
            pstmt.setString(1, valor);
            pstmt.setString(2, valor);
            ResultSet rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            mostrarExcepcion(e);
        }
        return null;
    }

    public static ResultSet recuperarVentasFiltrado(Connection conn, String valor) {
        valor = "%" + valor.toLowerCase() + "%";

        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL.sql_leer_ventas_tabla_filtro);
            pstmt.setString(1, valor);
            pstmt.setString(2, valor);
            pstmt.setString(3, valor);
            ResultSet rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            mostrarExcepcion(e);
        }
        return null;
    }

    public static Articulo recuperarArticuloID(Connection conn, BigInteger id) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL.sql_leer_articulo_id);
            pstmt.setString(1, id.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Articulo articulo = new Articulo(
                        BigInteger.valueOf(rs.getLong("id")),
                        rs.getString(SQL.ARTICULOS_NOMBRE),
                        rs.getBigDecimal(SQL.ARTICULOS_PRECIO)
                );
                rs.close();
                pstmt.close();
                return articulo;
            } else {
                rs.close();
                pstmt.close();
                return null;
            }

        } catch (SQLException e) {
            mostrarExcepcion(e);
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
        } catch (SQLException e) {
            mostrarExcepcion(e);
        }
        return respuesta;
    }
}
