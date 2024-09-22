/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany;

import com.mycompany.SQL.SQL;
import static com.mycompany.clases.Funciones.mostrarExcepcion;
import com.mycompany.modelos.Articulo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author haoen
 */
public class GestorClientes extends Thread {

    private PantallaPrincipal parent;
    private ServerSocket serverSocket;

    public GestorClientes(PantallaPrincipal parent, int puerto) {
        this.parent = parent;
        try {
            this.serverSocket = new ServerSocket(puerto, 0, java.net.InetAddress.getByName("0.0.0.0"));
            System.out.println("Servidor escuchando en todas las interfaces en el puerto " + puerto);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se ha podido establecer la conexión del Gestor de Clientes.\nCompruebe que el programa no se esté ejecutando de forma repetida.",
                    "Error al establecer Servidor",
                    JOptionPane.ERROR_MESSAGE
            );
            mostrarExcepcion(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clienteSocket.getInetAddress());

                // Crear un nuevo hilo para manejar la conexión
                ClienteHandler cliente = new ClienteHandler(parent, clienteSocket);
                cliente.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ClienteHandler extends Thread {

    private PantallaPrincipal parent;
    private Socket clienteSocket;

    public ClienteHandler(PantallaPrincipal parent, Socket clienteSocket) {
        this.parent = parent;
        this.clienteSocket = clienteSocket;
    }

    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true);

//            String entradaCliente;
//            while ((entradaCliente = entrada.readLine()) != null) {
////                JOptionPane.showConfirmDialog(null, "Entrada del cliente: " + entradaCliente);
//                
//                parent.addArticulo(new Articulo(new BigInteger(entradaCliente), "", new BigDecimal(0)), 1);
//            }
//            System.out.println("Se ha desconectado cliente con IP: "+clienteSocket.getInetAddress());
            String entradaCliente;
            while ((entradaCliente = entrada.readLine()) != null) {

                if (entradaCliente.startsWith("<<") && entradaCliente.contains(">>")) {
                    // Extraer el comando, formato "<<" + COMMANDO + ">>"
                    int finComando = entradaCliente.indexOf(">>") + 2;
                    String comando = entradaCliente.substring(0, finComando);
                    String datos = entradaCliente.substring(finComando).trim();

                    switch (comando) {
                        case "<<READ>>":      //buscar articulo
                            Articulo articulo_bdd = GestorBDD.recuperarArticuloID(parent.getConnection(), (new BigInteger(datos)));
                            if (articulo_bdd != null) {
                                salida.println("<<FOUND>>" + articulo_bdd.toString());
                            } else {
                                salida.println("<<NOT_FOUND>>");
                            }
                            break;

                        case "<<INSERT>>":      //registrar nuevo articulo
                            Articulo articulo_nuevo = Articulo.fromString(datos);
                            if (GestorBDD.articuloEjecutarCRUD(parent.getConnection(), SQL.sql_insertar_articulo, articulo_nuevo)) {
                                salida.println("<<TRUE>>");
                            } else {
                                salida.println("<<FALSE>>");
                            }
                            break;

                        case "<<UPDATE>>":      //editar artículo
                            Articulo articulo_modificar = Articulo.fromString(datos);
                            if (GestorBDD.articuloEjecutarCRUD(parent.getConnection(), SQL.sql_modificar_articulo, articulo_modificar)) {
                                salida.println("<<TRUE>>");
                            } else {
                                salida.println("<<FALSE>>");
                            }
                            break;

                        case "<<ADDCART>>":     //añadir al carrito
                            parent.addArticulo(new Articulo(new BigInteger(entradaCliente), "", new BigDecimal(0)), 1);
                            break;

                        default:
                            System.out.println("Comando no reconocido: " + comando);
                            break;
                    }
                } else {
                    System.out.println("Formato de mensaje no válido: " + entradaCliente);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
