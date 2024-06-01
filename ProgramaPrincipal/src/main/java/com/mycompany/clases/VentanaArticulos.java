/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.clases;

import com.mycompany.GestorBDD;
import com.mycompany.PantallaPrincipal;
import com.mycompany.SQL.SQL;
import com.mycompany.modelos.Articulo;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showConfirmDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.openide.util.Exceptions;

/**
 *
 * @author haoen
 */
public class VentanaArticulos extends javax.swing.JFrame {
    PantallaPrincipal parent;
    Connection conn;
    DefaultTableModel model;
    String[] columnNames = {"id", "Nombre", "Precio Unitario"};

    /**
     * Creates new form VentanaVentas
     */
    public VentanaArticulos(Connection conn, PantallaPrincipal parent) {
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Hacer que todas las celdas no sean editables
        jTableArticulos.setDefaultEditor(Object.class, null);

        jTableArticulos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Determinar en que row se ha realizado el click
                int r = jTableArticulos.rowAtPoint(e.getPoint());
                if (r >= 0 && r < jTableArticulos.getRowCount()) {
                    jTableArticulos.setRowSelectionInterval(r, r);  //seleccionar el row clickeado
                } else {
                    jTableArticulos.clearSelection();
                }

                int rowindex = jTableArticulos.getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                if ((e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) && e.getComponent() instanceof JTable) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem editar = new JMenuItem("Editar Artículo");
                    JMenuItem borrar = new JMenuItem("Borrar de la Base");

                    editar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //DIALOGO PARA EDITAR ARTICULO
                            editarArticulo(rowindex);
                        }
                    });
                    borrar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //BORRAR DE LA BASE
                            borrarArticulo(rowindex);
                        }
                    });

                    popupMenu.add(editar);
                    popupMenu.add(borrar);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    jTableArticulos.setRowSelectionInterval(r, r);
                }
            }
        });

        this.parent = parent;
        this.conn = conn;
        this.model = new DefaultTableModel();
        this.jTableArticulos.setModel(model);
        setArticulos(obtenerArticulos());
    }

    private void setArticulos(ArrayList<Articulo> articulos) {
        this.model.setRowCount(0);
        // Definir los nombres de las columnas
        this.model.setColumnIdentifiers(this.columnNames);

        // Añadir los nuevos artículos al modelo
        for (Articulo articulo : articulos) {
            model.addRow(new Object[]{articulo.getID(), articulo.getNombre(), articulo.getPrecio()});
        }
        this.model.fireTableDataChanged();
    }

    private ArrayList<Articulo> obtenerArticulos() {
        ResultSet rs = GestorBDD.recuperarElementos(conn, SQL.sql_leer_articulos);

        ArrayList<Articulo> articulos = new ArrayList<>();
        try {
            while (rs.next()) {
                articulos.add(new Articulo(BigInteger.valueOf(rs.getLong("id")), rs.getString(SQL.ARTICULOS_NOMBRE), rs.getBigDecimal(SQL.ARTICULOS_PRECIO)));
            }
            return articulos;
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private ArrayList<Articulo> obtenerArticulosFiltro() {
        String filtroValor = jTextFieldBuscar.getText();
        ResultSet rs = GestorBDD.recuperarArticulosFiltrado(conn, filtroValor);

        ArrayList<Articulo> articulos = new ArrayList<>();
        try {
            while (rs.next()) {
                articulos.add(new Articulo(BigInteger.valueOf(rs.getLong("id")), rs.getString(SQL.ARTICULOS_NOMBRE), rs.getBigDecimal(SQL.ARTICULOS_PRECIO)));
            }
            return articulos;
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public boolean editarArticulo(int fila) {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField textFieldNombre = new JTextField(10);
        JLabel labelNombre = new JLabel("Ingrese nombre: ");
        panel.add(labelNombre);
        panel.add(textFieldNombre);

        JTextField textFieldPrecio = new JTextField(10);
        JLabel labelPrecio = new JLabel("Ingrese precio: ");
        panel.add(labelPrecio);
        panel.add(textFieldPrecio);

        int option = showConfirmDialog(null, panel, "Registrar nuevo Artículo", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String nombre = textFieldNombre.getText();
            String precioStr = textFieldPrecio.getText();

            if (nombre.isEmpty() || precioStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: rellena los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return editarArticulo(fila);
            }
            if (!precioStr.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                JOptionPane.showMessageDialog(null, "Error: valor no valido introducido.", "Error", JOptionPane.ERROR_MESSAGE);
                return editarArticulo(fila);
            }

            try {
                double precio = Double.parseDouble(precioStr);

                if (precio == 0) {
                    JOptionPane.showMessageDialog(null, "Error: el precio no puede ser 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return editarArticulo(fila);
                }

                BigInteger id = (BigInteger) jTableArticulos.getValueAt(fila, 0);
                Articulo articulo = new Articulo(id, nombre, BigDecimal.valueOf(precio));
                GestorBDD.articuloEjecutarCRUD(conn, SQL.sql_modificar_articulo, articulo);
                setArticulos(obtenerArticulos());
                this.parent.actualizarArticulo(articulo, false);
                return true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error: el precio debe ser\nun número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return editarArticulo(fila);
            }
        }
        return false;
    }

    private void borrarArticulo(int fila) {
        BigInteger id = (BigInteger) jTableArticulos.getValueAt(fila, 0);
        Articulo articulo = new Articulo(id, "", BigDecimal.ZERO);
        if (GestorBDD.articuloEjecutarCRUD(conn, SQL.sql_borrar_articulo, articulo)) {
            GestorBDD.relacionEjecutarCRUD(conn, SQL.sql_borrar_relacion_articulo, BigInteger.ZERO, id, 0);
            setArticulos(obtenerArticulos());
            this.parent.actualizarArticulo(articulo, true);
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
        jTableArticulos = new javax.swing.JTable();
        jTextFieldBuscar = new javax.swing.JTextField();
        jButtonBuscar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(950, 500));

        jTableArticulos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTableArticulos);

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
                        .addGap(0, 617, Short.MAX_VALUE)))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
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

        setSize(new java.awt.Dimension(1130, 508));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBuscarActionPerformed
        setArticulos(obtenerArticulosFiltro());
    }//GEN-LAST:event_jButtonBuscarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBuscar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableArticulos;
    private javax.swing.JTextField jTextFieldBuscar;
    // End of variables declaration//GEN-END:variables
}
