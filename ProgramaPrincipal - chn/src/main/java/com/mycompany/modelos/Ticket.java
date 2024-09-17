/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.modelos;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author haoen
 */
public class Ticket implements Printable {

    static Image image;

    private Map<Articulo, Integer> articulos;
    private Venta venta;
    private BigDecimal descuento;
    private BigDecimal recibido;
    private BigDecimal total;
    private Cliente cliente;

    public Ticket(Map<Articulo, Integer> articulos, Venta venta, BigDecimal descuento, BigDecimal recibido) {
        instanciarTicket(articulos, venta, descuento, recibido);
    }

    public Ticket(Map<Articulo, Integer> articulos, Venta venta, BigDecimal descuento, BigDecimal recibido, Cliente cliente) {
        instanciarTicket(articulos, venta, descuento, recibido);
        this.cliente = cliente;
    }

    private void instanciarTicket(Map<Articulo, Integer> articulos, Venta venta, BigDecimal descuento, BigDecimal recibido) {
        this.articulos = articulos;
        this.venta = venta;
        this.descuento = descuento;
        this.recibido = recibido;

        this.total = BigDecimal.ZERO;
        for (Map.Entry<Articulo, Integer> entry : this.articulos.entrySet()) {
            total = total.add(entry.getKey().getPrecio().multiply(BigDecimal.valueOf(entry.getValue())));
        }

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("logo.png")) {
            if (inputStream == null) {
                System.err.println("No se encontró el archivo de imagen en el classpath.");
                return;
            }

            BufferedImage bufferedImage = ImageIO.read(inputStream);

            if (bufferedImage == null) {
                System.err.println("No se pudo leer la imagen. Verifica que el archivo sea un PNG válido.");
                return;
            }

            // Redimensionar la imagen
            int nuevoAncho = 50;
            int nuevoAlto = 50;
            Image imagenRedimensionada = bufferedImage.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);

            // Convertir el Image redimensionado de nuevo a BufferedImage
            BufferedImage imagenFinal = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = imagenFinal.createGraphics();
            g2d.drawImage(imagenRedimensionada, 0, 0, null);
            g2d.dispose();

            image = imagenFinal;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
    }

    // Función para imprimir texto dividiendo en líneas de hasta 25 caracteres
    private int imprimirTexto(Graphics g, String texto, int x, int y, int maxLineaLongitud, int yOffset) {
        // Dividir el texto en líneas de hasta 'maxLineaLongitud' caracteres
        ArrayList<String> lineas = new ArrayList<>();
        for (int i = 0; i < texto.length(); i += maxLineaLongitud) {
            int end = Math.min(texto.length(), i + maxLineaLongitud);
            lineas.add(texto.substring(i, end));
        }

        // Imprimir cada línea del texto
        for (String linea : lineas) {
            g.drawString(linea, x, y);
            y += yOffset;
        }

        return y; // Retorna la posición y actualizada después de imprimir el texto
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        String fechaFormateado = venta.getFecha().format(formatter);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        g.setFont(new Font("Monospaced", Font.PLAIN, 8));

        //Datos del local y icono
        g2d.drawImage(image, 45, 0, null);
        int y = 120;
        g.drawString("    *** Bazar Zhang ***", 5, y);
        y += 25;
        g.drawString(" Plaza Ejercito Español n7", 5, y);
        y += 10;

        if (cliente != null) {
            //datos del cliente si hay factura (Cliente)
            g.drawString("------------------------------", 5, y);
            y += 10;
            g.drawString(" Datos del cliente:", 5, y);
            y += 9;
            g.drawString(" DNI/NIE: " + this.cliente.getDNI(), 5, y);
            y += 9;
            g.drawString(" Nombre: ", 5, y);
            y += 9;

            y = imprimirTexto(g, this.cliente.getNombre(), 10, y, 25, 9);

            g.drawString(" Dirección: ", 5, y);
            y += 9;

            y = imprimirTexto(g, this.cliente.getDireccion(), 10, y, 25, 9);

            g.drawString(" Teléfono: " + this.cliente.getTelefono(), 5, y);
            y += 10;
        }

        g.drawString("------------------------------", 5, y);
        y += 10;
        g.drawString(" Fecha: " + fechaFormateado, 5, y);
        y += 9;
        g.drawString(" Id: " + venta.getID(), 5, y);
        y += 10;
        g.drawString("------------------------------", 5, y);
        y += 10;

        // Detalles de cada artículo en el ticket
        for (Map.Entry<Articulo, Integer> entry : articulos.entrySet()) {
            Articulo articulo = entry.getKey();
            String nombre = articulo.getNombre();
//            int maxLineaLongitud = 25; // Máximo de caracteres por línea

//            // Dividir el nombre en líneas de hasta 24 caracteres
//            ArrayList<String> lineas = new ArrayList<>();
//            for (int i = 0; i < nombre.length(); i += maxLineaLongitud) {
//                int end = Math.min(nombre.length(), i + maxLineaLongitud);
//                lineas.add(nombre.substring(i, end));
//            }
//
//            for (String linea : lineas) {
//                g.drawString(linea, 10, y);
//                y += 7;
//            }
            y = imprimirTexto(g, nombre, 10, y, 25, 7);

            String priceLine = String.format(" x%-3d %18.2f€", (int) entry.getValue(), articulo.getPrecio().multiply(BigDecimal.valueOf(entry.getValue())));
            g.drawString(priceLine, 10, y);
            y += 10;
        }

        g.drawString("------------------------------", 5, y);
        y += 10;

        String totalLine = String.format("TOTAL: %17s€", total);
        g.drawString(totalLine, 10, y);
        y += 9;

        if (descuento.compareTo(BigDecimal.ZERO) == 1) {
            String descuentoLine = String.format("Desc.: %17s€", descuento);
            g.drawString(descuentoLine, 10, y);
            y += 9;
        }

        String recibidoLine = String.format("Recibo: %16s€", recibido);
        g.drawString(recibidoLine, 10, y);
        y += 9;

        BigDecimal cambio = recibido.subtract(total.subtract(descuento));
        String cambioLine = String.format("Cambio: %16s€", cambio);
        g.drawString(cambioLine, 10, y);
        y += 9;

        // Asegurar que haya una separación de 20 unidades antes de los guiones
        y += 20 - (y % 20); // Ajustar la posición y para asegurar la separación de 20 unidades

        // Imprimir línea de guiones y mensaje final
        g.drawString("------------------------------", 5, y);
        y += 20 - (y % 20); // Ajustar la posición y para asegurar la separación de 20 unidades
        g.drawString(" Gracias por la visita :)", 10, y);

        
        
        //Ñapa hecho porque la impresora barata que compré funciona mal
        y += 20;
        g.drawString("", 5, y);
        y += 30;
        g.drawString("", 5, y);
        y += 30;
        g.drawString("------------------------------", 5, y);
        y += 30;
        g.drawString("------------------------------", 5, y);

        return PAGE_EXISTS;
    }
}
