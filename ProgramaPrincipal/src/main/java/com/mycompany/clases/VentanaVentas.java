/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.clases;

import com.mycompany.GestorBDD;
import com.mycompany.SQL.SQL;
import com.mycompany.modelos.Articulo;
import com.mycompany.modelos.Venta;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.openide.util.Exceptions;

/**
 *
 * @author haoen
 */
public class VentanaVentas extends javax.swing.JFrame {

    Connection conn;
    DefaultTableModel model;
    String[] columnNames = {"id", "Fecha", "Artículo", "Precio Unitario", "Subtotal"};

    /**
     * Creates new form VentanaVentas
     */
    public VentanaVentas(Connection conn) {
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Hacer que todas las celdas no sean editables
        jTableVentas.setDefaultEditor(Object.class, null);

        jTableVentas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Determinar en que row se ha realizado el click
                int r = jTableVentas.rowAtPoint(e.getPoint());
                if (r >= 0 && r < jTableVentas.getRowCount()) {
                    jTableVentas.setRowSelectionInterval(r, r);  //seleccionar el row clickeado
                } else {
                    jTableVentas.clearSelection();
                }

                int rowindex = jTableVentas.getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                if ((e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) && e.getComponent() instanceof JTable) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem borrar = new JMenuItem("Borrar de la Base");

                    borrar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //BORRAR DE LA BASE
                            borrarVenta(rowindex);
                        }
                    });

                    popupMenu.add(borrar);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    jTableVentas.setRowSelectionInterval(r, r);
                }
            }
        });
        jTableVentas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            Long lastId = null; // Último id procesado
            Color lastColor = null; // Color del último id procesado
            Color grisClaro = new Color(240, 240, 240);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                Long currentId = (Long) table.getValueAt(row, 0); // Obtener el id actual

                // Si el id actual es igual al id anterior, usar el mismo color
                if (lastId != null && lastId.equals(currentId)) {
                    c.setBackground(lastColor);
                } else {
                    // Si no, alternar entre gris claro y blanco
                    c.setBackground(row % 2 == 0 ? grisClaro : Color.WHITE);
                    lastColor = c.getBackground();
                }

                // Actualizar el último id procesado
                lastId = currentId;
                return c;
            }
        });

        this.conn = conn;
        this.model = new DefaultTableModel();
        jTableVentas.setModel(model);
        setVentas(obtenerVentas());
    }

    private void setVentas(ArrayList<Object[]> ventas) {
        this.model.setRowCount(0);
        // Definir los nombres de las columnas
        model.setColumnIdentifiers(this.columnNames);

        // Añadir los nuevos registros al modelo
        for (Object[] venta : ventas) {
            model.addRow(venta);
        }

        jTableVentas.setModel(model);
    }
    
    private ArrayList<Object[]> obtenerVentasFiltro() {
        String filtroValor = jTextFieldBuscar.getText();
        ResultSet rs = GestorBDD.recuperarVentasFiltrado(conn, filtroValor);

        ArrayList<Object[]> ventas = new ArrayList<>();
        try {
            while (rs.next()) {
                Object[] registro = new Object[6];
                registro[0] = rs.getLong("id");
                registro[1] = rs.getTimestamp("Fecha");
                registro[2] = rs.getString("Articulo");
                registro[3] = rs.getInt("Cantidad");
                registro[4] = rs.getDouble("Precio unitario");
                registro[5] = rs.getDouble("Total");
                ventas.add(registro);
            }
            return ventas;
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.out.println("null");
        return null;
    }

    private ArrayList<Object[]> obtenerVentas() {
        ResultSet rs = GestorBDD.recuperarElementos(conn, SQL.sql_leer_ventas_tabla);

        ArrayList<Object[]> ventas = new ArrayList<>();
        try {
            while (rs.next()) {
                Object[] registro = new Object[6];
                registro[0] = rs.getLong("id");
                registro[1] = rs.getTimestamp("Fecha");
                registro[2] = rs.getString("Articulo");
                registro[3] = rs.getInt("Cantidad");
                registro[4] = rs.getDouble("Precio unitario");
                registro[5] = rs.getDouble("Total");
                ventas.add(registro);
            }
            return ventas;
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.out.println("null");
        return null;
    }

    private void borrarVenta(int fila) {
        long id = (long) jTableVentas.getValueAt(fila, 0);
        Venta venta = new Venta(BigInteger.valueOf(id), LocalDateTime.now());
        if (GestorBDD.ventaEjecutarCRUD(conn, SQL.sql_borrar_venta, venta)){
            GestorBDD.relacionEjecutarCRUD(conn, SQL.sql_borrar_relacion_venta, BigInteger.valueOf(id), BigInteger.ZERO, 0);
            setVentas(obtenerVentas());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableVentas = new javax.swing.JTable();
        jTextFieldBuscar = new javax.swing.JTextField();
        jButtonBuscar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(950, 500));

        jTableVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableVentas);

        jTextFieldBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí puedes especificar el componente al que deseas mover el foco
                jButtonBuscar.requestFocusInWindow();
            }
        });

        jButtonBuscar.setText("Buscar");
        jButtonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextFieldBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonBuscar)
                        .addGap(0, 601, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(1114, 511));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBuscarActionPerformed
        setVentas(obtenerVentasFiltro());
    }//GEN-LAST:event_jButtonBuscarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBuscar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableVentas;
    private javax.swing.JTextField jTextFieldBuscar;
    // End of variables declaration//GEN-END:variables
}
