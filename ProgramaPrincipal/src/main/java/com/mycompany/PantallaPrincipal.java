package com.mycompany;

import com.mycompany.clases.Articulo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import mdlaf.MaterialLookAndFeel;
import org.netbeans.validation.api.builtin.stringvalidation.StringValidators;
import org.netbeans.validation.api.ui.ValidationGroup;

class JpopupTabla extends javax.swing.JPopupMenu {

    JMenuItem editar = new JMenuItem("Editar Cantidad");
    JMenuItem borrar = new JMenuItem("Borrar de la lista");

    public JpopupTabla() {

    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author haoen
 */
public class PantallaPrincipal extends javax.swing.JFrame {

    /**
     * Creates new form PantallaPrincipal
     */
    public PantallaPrincipal() {
        initComponents();
        
        //Crear shortcuts
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent e) -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                    createNewTab();
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X) {
                    int index = jTabbedPane.getSelectedIndex();
                    if (jTabbedPane.getTabCount() > 1) {
                        jTabbedPane.remove(index);
                        jTabbedPane.setSelectedIndex(index - 1);
                    }
                }
            }
            return false;
        });
    }
    
    private boolean validarCampos() {
        String textoCampo1 = jTextFieldNumBarra.getText();
        String textoCampo2 = jTextFieldCantidad.getText();

        if (textoCampo1.isEmpty() || textoCampo2.isEmpty()) {
            jLabelError.setText("Error: rellena los campos.");
            return false;
        }
        if (!textoCampo1.matches("[0-9]+") || !textoCampo2.matches("[0-9]+")) {
            jLabelError.setText("Error: valor no valido introducido.");
            return false;
        }
        if (textoCampo1.length()>20) {
            jLabelError.setText("Error: Valor no valido para Núm. Barra.");
            return false;
        }
        if (textoCampo2.length()>10) {
            jLabelError.setText("Error: valor no valido para Cantidad.");
            return false;
        }

        jLabelError.setText("");
        return true;
    }

    private void createNewTab() {
        String[] columnNames = {"ID", "Nombre del artículo", "Precio", "Cantidad Unitario", "Precio"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        model.setColumnIdentifiers(columnNames);
        JTable tablaNuevoOrden = new JTable(model);
        tablaNuevoOrden.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = tablaNuevoOrden.rowAtPoint(e.getPoint());
                if (r >= 0 && r < tablaNuevoOrden.getRowCount()) {
                    tablaNuevoOrden.setRowSelectionInterval(r, r);
                } else {
                    tablaNuevoOrden.clearSelection();
                }

                int rowindex = tablaNuevoOrden.getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem editar = new JMenuItem("Editar Cantidad");
                    JMenuItem borrar = new JMenuItem("Borrar de la lista");

                    editar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JOptionPane.showMessageDialog(null, "Hola editar fila "+rowindex);
                        }
                    });
                    borrar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JOptionPane.showMessageDialog(null, "Hola borrar fila "+rowindex);
                        }
                    });

                    popupMenu.add(editar);
                    popupMenu.add(borrar);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    tablaNuevoOrden.setRowSelectionInterval(r, r);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaNuevoOrden);

//        jTabbedPane.addTab("Pedido " + jTabbedPane.getTabCount(), scrollPane);
        jTabbedPane.insertTab("Pedido " + jTabbedPane.getTabCount(), null, scrollPane, null, jTabbedPane.getTabCount() - 1);
        jTabbedPane.setSelectedIndex(jTabbedPane.getTabCount() - 2);
    }

    private static void splashScreen() {
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        // Simula alguna tarea que el splash screen esté esperando
        try {
            Thread.sleep(500); // Espera 3 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        splash.dispose();
    }

    public boolean addArticulo(Articulo articulo, int cantidad) {
        int index = jTabbedPane.getSelectedIndex();
        if (index >= 0 && index < jTabbedPane.getTabCount()) {
            JScrollPane scrollPane = (JScrollPane) jTabbedPane.getComponentAt(index);
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.addRow(new Object[]{articulo.getID(), articulo.getNombre(), articulo.getPrecio(), cantidad});
            return true; // Éxito al agregar el artículo
        }
        return false; // No se pudo agregar el artículo
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanelBase = new javax.swing.JPanel();
        jPanelControles = new javax.swing.JPanel();
        jPanelIntroducir = new javax.swing.JPanel();
        jTextFieldNumBarra = new javax.swing.JTextField();
        jLabelNumBarra = new javax.swing.JLabel();
        jButtonAgregarArticulo = new javax.swing.JButton();
        jLabelIdVenta = new javax.swing.JLabel();
        jTextFieldCantidad = new javax.swing.JTextField();
        jLabelCantidad = new javax.swing.JLabel();
        jLabelError = new javax.swing.JLabel();
        jPanelVenta = new javax.swing.JPanel();
        jLabelTotal = new javax.swing.JLabel();
        jLabelRecibido = new javax.swing.JLabel();
        jLabelDevolver = new javax.swing.JLabel();
        jLabelDescuento = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButtonConfirmarVEnta = new javax.swing.JButton();
        jLabelSistema = new javax.swing.JLabel();
        jButtonImprimirRecibo = new javax.swing.JButton();
        jButtonMostrarRecibo = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabbedPane = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 600));
        setSize(new java.awt.Dimension(0, 0));

        jPanelControles.setBackground(new java.awt.Color(204, 204, 204));

        jLabelNumBarra.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabelNumBarra.setText("Num . de barra");

        jButtonAgregarArticulo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButtonAgregarArticulo.setText("Agregar artículo");
        jButtonAgregarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregarArticuloActionPerformed(evt);
            }
        });

        jLabelIdVenta.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabelIdVenta.setText("ID venta: ");

        jLabelCantidad.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabelCantidad.setText("Cantidad");

        jLabelError.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanelIntroducirLayout = new javax.swing.GroupLayout(jPanelIntroducir);
        jPanelIntroducir.setLayout(jPanelIntroducirLayout);
        jPanelIntroducirLayout.setHorizontalGroup(
            jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelIntroducirLayout.createSequentialGroup()
                .addGroup(jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelError, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelIntroducirLayout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jTextFieldCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonAgregarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelIntroducirLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabelIdVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabelNumBarra, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabelCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanelIntroducirLayout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addComponent(jTextFieldNumBarra, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanelIntroducirLayout.setVerticalGroup(
            jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelIntroducirLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelIdVenta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelNumBarra)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldNumBarra, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelIntroducirLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jButtonAgregarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelIntroducirLayout.createSequentialGroup()
                        .addComponent(jLabelCantidad)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelError, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        jLabelTotal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelTotal.setText("Total artículos:");

        jLabelRecibido.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelRecibido.setText("Recibido:");

        jLabelDevolver.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelDevolver.setText("A devolver:");

        jLabelDescuento.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelDescuento.setText("Descuento:");

        jButtonConfirmarVEnta.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButtonConfirmarVEnta.setText("Confirmar venta");
        jButtonConfirmarVEnta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarVEntaActionPerformed(evt);
            }
        });

        jLabelSistema.setText("Sistema:");

        jButtonImprimirRecibo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButtonImprimirRecibo.setText("Imprimir recibo");
        jButtonImprimirRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImprimirReciboActionPerformed(evt);
            }
        });

        jButtonMostrarRecibo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButtonMostrarRecibo.setText("Mostrar recibo");
        jButtonMostrarRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMostrarReciboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelVentaLayout = new javax.swing.GroupLayout(jPanelVenta);
        jPanelVenta.setLayout(jPanelVentaLayout);
        jPanelVentaLayout.setHorizontalGroup(
            jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVentaLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelVentaLayout.createSequentialGroup()
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelVentaLayout.createSequentialGroup()
                                .addComponent(jLabelSistema, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanelVentaLayout.createSequentialGroup()
                                .addComponent(jLabelDevolver)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonConfirmarVEnta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonImprimirRecibo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonMostrarRecibo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17))
                    .addGroup(jPanelVentaLayout.createSequentialGroup()
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelTotal)
                            .addGroup(jPanelVentaLayout.createSequentialGroup()
                                .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelDescuento)
                                    .addComponent(jLabelRecibido))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField1)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))))
                        .addContainerGap(243, Short.MAX_VALUE))))
        );
        jPanelVentaLayout.setVerticalGroup(
            jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVentaLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabelTotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelVentaLayout.createSequentialGroup()
                        .addComponent(jButtonMostrarRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonImprimirRecibo, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonConfirmarVEnta, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelVentaLayout.createSequentialGroup()
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDescuento))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelRecibido)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelDevolver)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelSistema)))
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout jPanelControlesLayout = new javax.swing.GroupLayout(jPanelControles);
        jPanelControles.setLayout(jPanelControlesLayout);
        jPanelControlesLayout.setHorizontalGroup(
            jPanelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelControlesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelIntroducir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelControlesLayout.setVerticalGroup(
            jPanelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelControlesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelIntroducir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane.setPreferredSize(new java.awt.Dimension(1100, 355));
        jTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTabbedPaneMousePressed(evt);
            }
        });

        //Botón para agregar nuevo orden sin tener que cancelar el previo.
        JButton botonNewTab = new JButton("+");
        botonNewTab.setBorder(null);
        botonNewTab.setFocusPainted(false);
        botonNewTab.setContentAreaFilled(false);
        botonNewTab.setToolTipText("Ctrl + N");
        botonNewTab.setPreferredSize(new Dimension(30, 30));
        botonNewTab.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewTab();
            }
        });

        jTabbedPane.addTab("", null);
        jTabbedPane.setTabComponentAt(0, botonNewTab);
        createNewTab();

        jScrollPane1.setViewportView(jTabbedPane);

        javax.swing.GroupLayout jPanelBaseLayout = new javax.swing.GroupLayout(jPanelBase);
        jPanelBase.setLayout(jPanelBaseLayout);
        jPanelBaseLayout.setHorizontalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanelControles, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1088, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelBaseLayout.setVerticalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelControles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenu1.setText("Artículos");
        jMenuBar1.add(jMenu1);

        jMenu4.setText("Ventas");
        jMenuBar1.add(jMenu4);

        jMenu2.setText("Configuración");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Ayuda");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(1116, 608));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonConfirmarVEntaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarVEntaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConfirmarVEntaActionPerformed

    private void jButtonImprimirReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImprimirReciboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonImprimirReciboActionPerformed

    private void jButtonAgregarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAgregarArticuloActionPerformed
        if (validarCampos()) {
            addArticulo(new Articulo(new BigInteger(jTextFieldNumBarra.getText()), "a", new BigDecimal(20.0)), Integer.parseInt(jTextFieldCantidad.getText()));
        }
    }//GEN-LAST:event_jButtonAgregarArticuloActionPerformed

    private void jButtonMostrarReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMostrarReciboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonMostrarReciboActionPerformed

    private void jTabbedPaneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPaneMousePressed
        //Borrar Tab abriendo un menú con click derecho
        if (SwingUtilities.isRightMouseButton(evt)) {
            int index = jTabbedPane.getSelectedIndex();

            if (index != jTabbedPane.getTabCount()) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem cerrar = new JMenuItem("Cerrar");
                cerrar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jTabbedPane.remove(index);
                        jTabbedPane.setSelectedIndex(index - 1);
                    }
                });
                popupMenu.add(cerrar);
                popupMenu.show(jTabbedPane.getComponentAt(index), evt.getX(), evt.getY() - 20);
            }
        }
    }//GEN-LAST:event_jTabbedPaneMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            MaterialLookAndFeel lookAndFeel = new MaterialLookAndFeel();
            javax.swing.UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
////            UIManager.setLookAndFeel(new MaterialLookAndFeel());
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>
        splashScreen();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PantallaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonAgregarArticulo;
    private javax.swing.JButton jButtonConfirmarVEnta;
    private javax.swing.JButton jButtonImprimirRecibo;
    private javax.swing.JButton jButtonMostrarRecibo;
    private javax.swing.JLabel jLabelCantidad;
    private javax.swing.JLabel jLabelDescuento;
    private javax.swing.JLabel jLabelDevolver;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelIdVenta;
    private javax.swing.JLabel jLabelNumBarra;
    private javax.swing.JLabel jLabelRecibido;
    private javax.swing.JLabel jLabelSistema;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelControles;
    private javax.swing.JPanel jPanelIntroducir;
    private javax.swing.JPanel jPanelVenta;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextFieldCantidad;
    private javax.swing.JTextField jTextFieldNumBarra;
    // End of variables declaration//GEN-END:variables
}
