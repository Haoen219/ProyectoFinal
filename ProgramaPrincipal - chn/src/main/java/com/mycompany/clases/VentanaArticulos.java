/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.clases;

import com.mycompany.GestorBDD;
import com.mycompany.PantallaPrincipal;
import com.mycompany.SQL.SQL;
import com.mycompany.modelos.Articulo;
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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
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
    String[] columnNames = {"商品ID", "商品名称", "商品进价", "商品单价", "仓库名称"};

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
                    JMenuItem editar = new JMenuItem("修改商品");
                    JMenuItem borrar = new JMenuItem("删除商品");

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
            model.addRow(new Object[]{articulo.getID(), articulo.getNombre(), articulo.getPrecioCompra(), articulo.getPrecio(), articulo.getAlmacen()});
        }
        this.model.fireTableDataChanged();
    }

    private ArrayList<Articulo> obtenerArticulos() {
        ResultSet rs = GestorBDD.recuperarElementos(conn, SQL.sql_leer_articulos);

        ArrayList<Articulo> articulos = new ArrayList<>();
        try {
            while (rs.next()) {
                articulos.add(new Articulo(BigInteger.valueOf(rs.getLong("id")), rs.getString(SQL.ARTICULOS_NOMBRE), rs.getBigDecimal(SQL.ARTICULOS_PRECIO_ALMACEN), rs.getBigDecimal(SQL.ARTICULOS_PRECIO), rs.getString(SQL.ARTICULOS_ALMACEN)));
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
        BigInteger id = (BigInteger) jTableArticulos.getValueAt(fila, 0);   //literalmente obtener el ID
        String nombre = (String) jTableArticulos.getValueAt(fila, 1);
        BigDecimal precioAlm = (BigDecimal) jTableArticulos.getValueAt(fila, 2);
        BigDecimal precio = (BigDecimal) jTableArticulos.getValueAt(fila, 3);
        String almacen = (String) jTableArticulos.getValueAt(fila, 4);
        
        Articulo articulo = new Articulo(id, nombre, precioAlm, precio, almacen);
        
        if (this.parent.editarArticulo(articulo)) {
            setArticulos(obtenerArticulos());
            this.parent.actualizarArticulo(articulo, false);
            return true;
        }
        return false;
    }

//    public boolean editarArticulo(int fila) {
//        JPanel panel = new JPanel(new GridLayout(2, 2));
//        JTextField textFieldNombre = new JTextField(10);
//        JLabel labelNombre = new JLabel("商品名称: ");
//        panel.add(labelNombre);
//        panel.add(textFieldNombre);
//
//        JTextField textFieldPrecioAlmacen = new JTextField(10);
//        JLabel labelPrecioAlmacen = new JLabel("商品进价: ");
//        panel.add(labelPrecioAlmacen);
//        panel.add(textFieldPrecioAlmacen);
//
//        JTextField textFieldPrecio = new JTextField(10);
//        JLabel labelPrecio = new JLabel("商品单价: ");
//        panel.add(labelPrecio);
//        panel.add(textFieldPrecio);
//        
//        JTextField textFieldAlmacen = new JTextField(10);
//        textFieldAlmacen.setText(this.parent.almacen); //pereza
//        JLabel labelAlmacen = new JLabel("仓库名称: ");
//        panel.add(labelAlmacen);
//        panel.add(textFieldAlmacen);
//
//        int option = showConfirmDialog(null, panel, "修改商品", JOptionPane.OK_CANCEL_OPTION);
//
//        if (option == JOptionPane.OK_OPTION) {
//            String nombre = textFieldNombre.getText();
//            String precioAlm = textFieldPrecio.getText();
//            String precioStr = textFieldPrecio.getText();
//            String almacenStr = textFieldPrecio.getText();
//
//            if (nombre.isEmpty() || precioAlm.isEmpty() || precioStr.isEmpty() || almacenStr.isEmpty()) {
//                JOptionPane.showMessageDialog(null, "Error: rellena los campos.", "Error", JOptionPane.ERROR_MESSAGE);
//                return editarArticulo(fila);
//            }
//            if (!precioStr.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
//                JOptionPane.showMessageDialog(null, "Error: valor no valido introducido.", "Error", JOptionPane.ERROR_MESSAGE);
//                return editarArticulo(fila);
//            }
//
//            try {
//                this.parent.almacen = almacenStr;
//                double precio = Double.parseDouble(precioStr);
//                double precioCompra = Double.parseDouble(precioAlm);
//
//                if (precio == 0) {
//                    JOptionPane.showMessageDialog(null, "Error: el precio no puede ser 0.", "Error", JOptionPane.ERROR_MESSAGE);
//                    return editarArticulo(fila);
//                }
//
//                BigInteger id = (BigInteger) jTableArticulos.getValueAt(fila, 0);   //literalmente obtener el ID
//                Articulo articulo = new Articulo(id, nombre, BigDecimal.valueOf(precio));
//                GestorBDD.articuloEjecutarCRUD(conn, SQL.sql_modificar_articulo, articulo);
//                
//                setArticulos(obtenerArticulos());
//                this.parent.actualizarArticulo(articulo, false);
//                return true;
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(null, "Error: el precio debe ser\nun número válido.", "Error", JOptionPane.ERROR_MESSAGE);
//                return editarArticulo(fila);
//            }
//        }
//        return false;
//    }

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

        jButtonBuscar.setFont(new java.awt.Font("Microsoft YaHei", 0, 12)); // NOI18N
        jButtonBuscar.setText(org.openide.util.NbBundle.getMessage(VentanaArticulos.class, "VentanaArticulos.jButtonBuscar.text")); // NOI18N
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
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
