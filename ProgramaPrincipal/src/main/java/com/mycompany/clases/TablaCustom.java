/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.clases;

import com.mycompany.PantallaPrincipal;
import com.mycompany.modelos.Articulo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showConfirmDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author haoen
 */
public class TablaCustom extends javax.swing.JTable {

    private final String ID_VENTA;
    private PantallaPrincipal parent;
    private String[] columnNames = {"ID", "Nombre del artículo", "Precio Unitario", "Cantidad", "Subtotal"};
    private DefaultTableModel model;

    public String getID_VENTA() {
        return this.ID_VENTA;
    }

    public TablaCustom(PantallaPrincipal parent) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        ID_VENTA = now.format(formatter);

        this.parent = parent;

        // Crear y configurar modelo
        model = new DefaultTableModel(columnNames, 0);
        model.setColumnIdentifiers(columnNames);
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                parent.setTotal(calcularTotal());
            }
        });
        this.setModel(model);

        // Crear un DefaultTableCellRenderer para centrar el texto
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Añadir un MouseListener para manejar clics con el botón derecho del ratón
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Determinar en que row se ha realizado el click
                int r = rowAtPoint(e.getPoint());
                if (r >= 0 && r < getRowCount()) {
                    setRowSelectionInterval(r, r);  //seleccionar el row clickeado
                } else {
                    clearSelection();
                }

                int rowindex = getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                if ((e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) && e.getComponent() instanceof JTable) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem editar = new JMenuItem("Editar Cantidad");
                    JMenuItem borrar = new JMenuItem("Borrar de la lista");

                    editar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (customDialogEditar(rowindex)) {
                                parent.setTotal(calcularTotal());
                                parent.setIdVenta(getID_VENTA());
                            }
                        }
                    });
                    borrar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            model.removeRow(rowindex);
                        }
                    });

                    popupMenu.add(editar);
                    popupMenu.add(borrar);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    setRowSelectionInterval(r, r);
                }
            }
        });
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = new BigDecimal(0);

        for (int i = 0; i < model.getRowCount(); i++) {
            total = total.add((BigDecimal) model.getValueAt(i, 4));
        }

        return total;
    }

    public Map<Articulo, Integer> listarID() {
        Map<Articulo, Integer> articulos = new HashMap<>() {
        };

        for (int i = 0; i < model.getRowCount(); i++) {
            BigInteger id = (BigInteger) model.getValueAt(i, 0);
            String nombre = (String) model.getValueAt(i, 1);
            BigDecimal precio = (BigDecimal) model.getValueAt(i, 2);
            int cantidad = (int) model.getValueAt(i, 3);

            articulos.put(new Articulo(id, nombre, precio), cantidad);
        }

        return articulos;
    }

    public boolean customDialogEditar(int rowindex) {
        JPanel panel = new JPanel();
        JTextField textField = new JTextField(10);
        JLabel label = new JLabel("Ingrese la cantidad: ");
        panel.add(label);
        panel.add(textField);

        int option = showConfirmDialog(null, panel, "Modificar cantidad", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String textoCampo = textField.getText();

            if (textoCampo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: rellena los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogEditar(rowindex);
            }
            if (!textoCampo.matches("[0-9]+")) {
                JOptionPane.showMessageDialog(null, "Error: valor no valido introducido.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogEditar(rowindex);
            }
            if (textoCampo.length() > 10) {
                JOptionPane.showMessageDialog(null, "Error: Valor no valido\npara Núm. Barra.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogEditar(rowindex);
            }

            if (Integer.parseInt(textoCampo) == 0) {
                JOptionPane.showMessageDialog(null, "Error: No puede introducir\n0 como cantidad.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogEditar(rowindex);
            }

            model.setValueAt(textField.getText(), rowindex, 3);
            BigDecimal precio = new BigDecimal(((Number) model.getValueAt(rowindex, 2)).doubleValue());
            BigDecimal precioTotal = precio.multiply(BigDecimal.valueOf(Integer.parseInt(textoCampo))).setScale(2, RoundingMode.HALF_EVEN);
            model.setValueAt(precioTotal, rowindex, 4);
            return true;
        }
        return false;
    }
}
