/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.modelos;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 *
 * @author haoen
 */
public class Ticket implements Printable {

    private Map<Articulo, Integer> articulos;
    private Venta venta;
    private BigDecimal descuento;
    private BigDecimal recibido;
    private BigDecimal total;

    public Ticket(Map<Articulo, Integer> articulos, Venta venta, BigDecimal descuento, BigDecimal recibido) {
        this.articulos = articulos;
        this.venta = venta;
        this.descuento = descuento;
        this.recibido = recibido;

        this.total = BigDecimal.ZERO;
        for (Map.Entry<Articulo, Integer> entry : this.articulos.entrySet()) {
            total = total.add(entry.getKey().getPrecio().multiply(BigDecimal.valueOf(entry.getValue())));
        }
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

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));

        int y = 20;
        g.drawString("     *** Bazar Zhang ***", 10, y);
        y += 15;
        g.drawString("------------------------------", 10, y);
        y += 15;
        g.drawString(" Fecha: " + fechaFormateado, 10, y);
        y += 15;
        g.drawString(" Venta id: " + venta.getID(), 10, y);
        y += 15;
        g.drawString("------------------------------", 10, y);
        y += 15;

        // Detalles de cada artículo en el ticket
        for (Map.Entry<Articulo, Integer> entry : articulos.entrySet()) {
            Articulo articulo = entry.getKey();

            String itemLine = String.format(" %-22s %5d", articulo.getNombre(), entry.getValue());
            g.drawString(itemLine, 10, y);
            y += 15;

            String priceLine = String.format(" %27.2f€", articulo.getPrecio().multiply(BigDecimal.valueOf(entry.getValue())));
            g.drawString(priceLine, 10, y);
            y += 18;
        }

        g.drawString("------------------------------", 10, y);
        y += 15;

        String totalLine = String.format(" TOTAL: %20s€", total);
        g.drawString(totalLine, 10, y);
        y += 15;

        String descuentoLine = String.format(" Descuento: %16s€", descuento);
        g.drawString(descuentoLine, 10, y);
        y += 15;

        String recibidoLine = String.format(" Recibido: %17s€", recibido);
        g.drawString(recibidoLine, 10, y);
        y += 15;

        String devueltoLine = String.format(" Devuelto: %17s€", total.subtract(descuento).subtract(recibido));
        g.drawString(devueltoLine, 10, y);
        y += 15;

        g.drawString("------------------------------", 10, y);
        y += 15;
        g.drawString("     Gracias por la visita", 10, y);

        return PAGE_EXISTS;
    }
}
