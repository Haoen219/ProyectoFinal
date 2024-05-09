/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clases;

import com.mycompany.PantallaPrincipal;
import static com.mycompany.clases.GestorBDD.mostrarExcepcion;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

            String entradaCliente;
            while ((entradaCliente = entrada.readLine()) != null) {
                JOptionPane.showConfirmDialog(null, "Entrada del cliente: " + entradaCliente);
                
//                parent.addArticulo(new Articulo(new BigInteger(entradaCliente), "", new BigDecimal(0)), 1);
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
