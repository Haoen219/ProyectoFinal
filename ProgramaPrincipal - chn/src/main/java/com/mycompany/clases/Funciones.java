package com.mycompany.clases;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author haoen
 */
public class Funciones {
    public static void mostrarExcepcion(Exception ex) {
//        // Crear un JTextArea para mostrar el mensaje de la excepción
//        JTextArea textArea = new JTextArea(10, 30);
//        textArea.setEditable(false);
//        textArea.append("Se ha producido una excepción:\n\n");
//        textArea.append(ex.toString());
//        textArea.append("\n\nDetalles:\n\n");
//        for (StackTraceElement element : ex.getStackTrace()) {
//            textArea.append(element.toString() + "\n");
//        }
//
//        // Envolver el JTextArea en un JScrollPane para permitir el desplazamiento
//        JScrollPane scrollPane = new JScrollPane(textArea);
//
//        // Mostrar el mensaje de la excepción en un JOptionPane
//        JOptionPane.showMessageDialog(null, scrollPane, "Excepción", JOptionPane.ERROR_MESSAGE);
        DialogoError error = new DialogoError(ex.toString());
        error.setVisible(true);
        
    }
}
