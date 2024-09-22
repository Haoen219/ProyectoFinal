package com.mycompany;

import com.mycompany.clases.SplashScreen;
import com.mycompany.SQL.SQL;
import com.mycompany.modelos.Articulo;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showConfirmDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.clases.Funciones;
import com.mycompany.clases.TablaCustom;
import com.mycompany.clases.VentanaArticulos;
import com.mycompany.clases.VentanaVentas;
import com.mycompany.modelos.Cliente;
import com.mycompany.modelos.Ticket;
import com.mycompany.modelos.Venta;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openide.util.Exceptions;

public class PantallaPrincipal extends javax.swing.JFrame {

    private GestorClientes gestorClientes;
    private Connection conn;
    private Ticket ticketAnterior;
    private String almacen = "";
    
    public Connection getConnection(){
        return this.conn;
    }

    public PantallaPrincipal() {
        initComponents();

        ImageIcon logo = new ImageIcon(getClass().getResource("/logo.png"));
        this.setIconImage(logo.getImage());

        gestorClientes = new GestorClientes(this, 1234);
        gestorClientes.start();
        conn = GestorBDD.conectar();
        try {
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }

        //Crear shortcuts
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent e) -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) {
                    crearNuevoTab();
                }
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X) {
                    cerrarCurrentTab();
                }
            }
            return false;
        });

        jTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = jTabbedPane.getSelectedIndex();

                if (index != jTabbedPane.getTabCount() - 1) {
                    JScrollPane selectedComponent = (JScrollPane) jTabbedPane.getComponentAt(index);
                    TablaCustom table = (TablaCustom) selectedComponent.getViewport().getView();

                    setTotal(table.calcularTotal());
                    setIdVenta(table.getID_VENTA());
                } else {
                    setTotal(BigDecimal.ZERO);
                    setIdVenta("");
                }
                jTextFieldDescuento.setText("0");
                jTextFieldRecibido.setText("0");
                jLabelDevolver_num.setText("0.0€");
                jButtonConfirmarVenta.setEnabled(false);
                jButtonFactura.setEnabled(false);
            }
        });

        crearNuevoTab();
    }

    public boolean customDialogoNuevo(Articulo articulo) {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField textFieldNombre = new JTextField(10);
        JLabel labelNombre = new JLabel("商品名称: ");
        panel.add(labelNombre);
        panel.add(textFieldNombre);

        JTextField textFieldPrecioAlmacen = new JTextField(10);
        JLabel labelPrecioAlmacen = new JLabel("商品进价: ");
        panel.add(labelPrecioAlmacen);
        panel.add(textFieldPrecioAlmacen);

        JTextField textFieldPrecio = new JTextField(10);
        JLabel labelPrecio = new JLabel("商品单价: ");
        panel.add(labelPrecio);
        panel.add(textFieldPrecio);

        JTextField textFieldAlmacen = new JTextField(10);
        textFieldAlmacen.setText(this.almacen);
        JLabel labelAlmacen = new JLabel("仓库名称: ");
        panel.add(labelAlmacen);
        panel.add(textFieldAlmacen);

        //leer los datos del articulo (si es que tiene)
        textFieldNombre.setText(articulo.getNombre());
        textFieldPrecioAlmacen.setText(articulo.getPrecioCompra().toString());
        textFieldPrecio.setText(articulo.getPrecio().toString());
        textFieldAlmacen.setText(this.almacen); //default nombre de almacen es el ultimo introducido por el usuario

        int option = showConfirmDialog(null, panel, "收入新商品 " + articulo.getID(), JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String nombre = textFieldNombre.getText();
            String precioAlm = textFieldPrecioAlmacen.getText();
            String precioStr = textFieldPrecio.getText();
            this.almacen = textFieldAlmacen.getText();

            if (nombre.isEmpty() || precioAlm.isEmpty() || precioStr.isEmpty() || almacen.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: 请填写空白字段.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogoNuevo(articulo);
            }
            if (!precioStr.matches("^[0-9]+(\\.[0-9]{1,2})?$") || !precioAlm.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                JOptionPane.showMessageDialog(null, "Error: 具有无效值的字段.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogoNuevo(articulo);
            }

            try {
                double precioCompra = Double.parseDouble(precioAlm);
                double precio = Double.parseDouble(precioStr);

                if (precio == 0) {
                    JOptionPane.showMessageDialog(null, "Error: 价格字段不可为0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return customDialogoNuevo(articulo);
                }

                articulo.setNombre(nombre);
                articulo.setPrecioCompra(new BigDecimal(precioCompra));
                articulo.setPrecio(new BigDecimal(precio));
                articulo.setAlmacen(almacen);
                return true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error: 具有无效的价格字段.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogoNuevo(articulo);
            }
        }
        return false;
    }

    private boolean validarCamposAgregar() {
        String numBarra = jTextFieldNumBarra.getText();
        String cantidad = jTextFieldCantidad.getText();

        if (numBarra.isEmpty() || cantidad.isEmpty()) {
            jLabelError.setText("Error: 请填写空白字段.");
            return false;
        }
        if (!numBarra.matches("[0-9]+") || !cantidad.matches("[0-9]+")) {
            jLabelError.setText("Error: 具有无效值的字段.");
            return false;
        }
        if (numBarra.length() > 18) {    //18 porque en la BD BIGINT puede tener max. 19 dígitos.
            jLabelError.setText("Error: 具有无效值的条码字段.");
            return false;
        }
        if (cantidad.length() > 10) {
            jLabelError.setText("Error: 具有无效值的数量字段.");
            return false;
        }

        if (Integer.parseInt(cantidad) == 0) {
            jLabelError.setText("Error: 数量字段不可为0.");
            return false;
        }

        jLabelError.setText("");
        return true;
    }

    private boolean validarCamposVenta() {
        String descuento = jTextFieldDescuento.getText();
        String recibido = jTextFieldRecibido.getText();

        if (descuento.isEmpty() || recibido.isEmpty()) {
            jLabelErrorVenta.setText("Error: 请填写空白字段.");
            return false;
        }
        if (!descuento.matches("^[0-9]+(\\.[0-9]{1,2})?$") || !recibido.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            jLabelErrorVenta.setText("Error: 具有无效值的字段.");
            return false;
        }

        jLabelErrorVenta.setText("");
        return true;
    }

    private void crearNuevoTab() {
        JTable tablaNuevoOrden = new TablaCustom(this);
        JScrollPane scrollPane = new JScrollPane(tablaNuevoOrden);

        jTabbedPane.insertTab("销售订单 " + jTabbedPane.getTabCount(), null, scrollPane, null, jTabbedPane.getTabCount() - 1);
        jTabbedPane.setSelectedIndex(jTabbedPane.getTabCount() - 2);
    }

    public void cerrarCurrentTab() {
        int index = jTabbedPane.getSelectedIndex();
        if (jTabbedPane.getTabCount() > 1 && index != jTabbedPane.getTabCount() - 1) {
            jTabbedPane.remove(index);

            if (jTabbedPane.getTabCount() == 1) {
                crearNuevoTab();
            }

            if (index == 0) {
                jTabbedPane.setSelectedIndex(index);
            } else if (jTabbedPane.getTabCount() > 1) {
                jTabbedPane.setSelectedIndex(index - 1);
            }
        }
    }

    public boolean actualizarArticuloTable(JTable table, Articulo articulo, int cantidadSuma) {
        for (int row = 0; row < table.getRowCount(); row++) {
            if (table.getValueAt(row, 0).equals(articulo.getID())) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int cantidad = (int) table.getValueAt(row, 3);
                cantidad += cantidadSuma;

                BigDecimal precioTotal = articulo.getPrecio().multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_EVEN);
                model.setValueAt(cantidad, row, 3);
                model.setValueAt(precioTotal, row, 4);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean addArticulo(Articulo articulo, int cantidad) {
        Articulo articulo_bdd = GestorBDD.recuperarArticuloID(conn, articulo.getID());
        if (articulo_bdd != null) {
            articulo = articulo_bdd;
        } else {
            //Nueva OptionPane para Introducir nombre y precio
            if (customDialogoNuevo(articulo)) {
                if (!GestorBDD.articuloEjecutarCRUD(conn, SQL.sql_insertar_articulo, articulo)) {
                    JOptionPane.showMessageDialog(null, "Error al intentar introducir el\narticulo en la BDD.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                return false;
            }
        }
        BigDecimal precio = articulo.getPrecio().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal precioTotal = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_EVEN);
        int index = jTabbedPane.getSelectedIndex();
        if (index >= 0 && index < jTabbedPane.getTabCount()) {
            JScrollPane scrollPane = (JScrollPane) jTabbedPane.getComponentAt(index);
            JTable table = (JTable) scrollPane.getViewport().getView();
            if (!actualizarArticuloTable(table, articulo, cantidad)) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{
                    articulo.getID(),
                    articulo.getNombre(),
                    precio,
                    cantidad,
                    precioTotal
                });
            }

            jTextFieldNumBarra.setText("");
            jTextFieldCantidad.setText("");
            return true; // Éxito al agregar el artículo
        }
        return false; // No se pudo agregar el artículo
    }

    public void setTotal(BigDecimal total) {
        jLabelTotal_num.setText(total.setScale(2, RoundingMode.HALF_EVEN) + "€");
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            jButtonRecibir.setEnabled(false);
        } else {
            jButtonRecibir.setEnabled(true);
        }
    }

    public void setIdVenta(String id) {
        jLabelIdVenta_num.setText(id);
    }

    public void generarTicket(Ticket ticket) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(ticket);

        try {
            PrintService[] printServices = PrinterJob.lookupPrintServices();

            if (printServices != null && printServices.length > 0) {
                // Obtener el servicio de impresión predeterminado
                PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

                if (defaultPrintService != null) {
                    job.setPrintService(defaultPrintService);

                    // Ajustar el formato de página para asegurar que sea correcto
                    PageFormat pageFormat = job.defaultPage();
                    Paper paper = pageFormat.getPaper();

                    // Configurar el área imprimible (dejando márgenes, por ejemplo 10 puntos)
                    paper.setImageableArea(10, 10, paper.getWidth(), paper.getHeight() + 100);

                    pageFormat.setPaper(paper);
                    job.setPrintable(ticket, pageFormat);

                    // Imprimir directamente sin mostrar el diálogo
                    job.print();
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "No hay impresora predeterminada disponible.",
                            "Atención",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "No hay impresoras disponibles.",
                        "Atención",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (PrinterAbortException e) {
            // No hacer nada si se cancela la impresión
        } catch (PrinterException e) {
            Funciones.mostrarExcepcion(e);
        }
        this.ticketAnterior = ticket;
        actualizarBotonTicketAnterior();

    }

    private void actualizarBotonTicketAnterior() {
        if (this.ticketAnterior == null) {
            jButtonTicketAnterior.setEnabled(false);
        } else {
            jButtonTicketAnterior.setEnabled(true);
        }
    }

    public void actualizarArticulo(Articulo articuloNuevo, boolean borrar) {
        // RECORRER TODAS LAS TABLAS ACTUALIZANDO ARTÍCULOS
        for (int panel = 0; panel < jTabbedPane.getTabCount() - 1; panel++) {
            JScrollPane selectedComponent = (JScrollPane) jTabbedPane.getComponentAt(panel);
            TablaCustom table = (TablaCustom) selectedComponent.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) table.getModel();

            for (int row = 0; row < model.getRowCount(); row++) {
                BigInteger id = new BigInteger(model.getValueAt(row, 0).toString());

                if (borrar) {
                    model.removeRow(row);
                } else if (id.equals(articuloNuevo.getID())) {
                    int cantidad = (int) model.getValueAt(row, 3);

                    String nombre = articuloNuevo.getNombre();
                    BigDecimal precio = articuloNuevo.getPrecio();
                    BigDecimal precioTotal = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_EVEN);

                    model.setValueAt(nombre, row, 1);
                    model.setValueAt(precio, row, 2);
                    model.setValueAt(precioTotal, row, 4);
                }
            }
        }
    }

    public void leerArchivoXLS() {
        // Crear un JFrame para utilizar con JFileChooser
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        // Crear un JFileChooser para que el usuario seleccione el archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("请选择 .xls 文件");

        // Mostrar el cuadro de diálogo de selección de archivo
        int result = fileChooser.showOpenDialog(frame);

        // Si el usuario selecciona un archivo, proceder a leerlo
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Verificar si el archivo tiene la extensión correcta
            if (!selectedFile.getName().toLowerCase().endsWith(".xls")) {
                System.out.println("Por favor, selecciona un archivo con la extensión '.xls'");
                return;
            }

            try {
                // Leer el archivo XLS
                HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(selectedFile));
                HSSFSheet sheet = wb.getSheetAt(0);

                System.out.println(sheet.getLastRowNum());

                for (int i = 1; i <= sheet.getLastRowNum(); ++i) {
                    HSSFRow row = sheet.getRow(i);

                    HSSFCell productCell = row.getCell(1);
                    HSSFCell idCell = row.getCell(3);
                    HSSFCell priceCell = row.getCell(9);

                    // Comprobar que no sea una línea de datos del comprador
                    if (productCell == null || idCell == null || priceCell == null) {
                        // Si alguna de las celdas es null, continúa con la siguiente fila
                        continue;
                    }

                    String product = productCell.getStringCellValue();
                    String barcode = idCell.getStringCellValue();
                    String price = priceCell.getStringCellValue();
                    Long barcode_ID = Long.valueOf(0);
                    BigDecimal precio_compra = BigDecimal.ZERO;

                    try {
                        // Comprobar que barcode es un número válido
                        barcode_ID = Long.valueOf(barcode);
                        precio_compra = BigDecimal.valueOf(Double.parseDouble(price));
                    } catch (NumberFormatException e) {
                        // Si barcode no es un número válido, saltar al siguiente ciclo
                        //System.out.println(i+ " Error en pasar los precios");
                        System.out.println(e);
                        continue;
                    }

                    System.out.println(product);
                    System.out.println(barcode_ID);
                    System.out.println(precio_compra);

                    //agregar nuevo articulo
                    Articulo articulo_nuevo = new Articulo(BigInteger.valueOf(barcode_ID), product, precio_compra, BigDecimal.ZERO, "");
                    Articulo articulo_bdd = GestorBDD.recuperarArticuloID(conn, articulo_nuevo.getID());
                    if (articulo_bdd != null) {

                        System.out.println(i + " editando");
                        editarArticulo(articulo_bdd);
                    } else {
                        System.out.println(i + " agregando");
                        //Nueva OptionPane para Introducir nombre y precio, no se llama la funcion de agregar para evitar agregarlo al pedido
                        if (customDialogoNuevo(articulo_nuevo)) {
                            if (!GestorBDD.articuloEjecutarCRUD(conn, SQL.sql_insertar_articulo, articulo_nuevo)) {
                                JOptionPane.showMessageDialog(null, "Error al intentar introducir el\narticulo en la BDD.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                Funciones.mostrarExcepcion(e);
            }
        } else {
            System.out.println("No se seleccionó ningún archivo.");
        }

        // Cerrar el JFrame
        frame.dispose();
    }

//Usado para editar articulos
    public boolean editarArticulo(Articulo articulo_original) {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField textFieldNombre = new JTextField(10);
        JLabel labelNombre = new JLabel("商品名称: ");
        panel.add(labelNombre);
        panel.add(textFieldNombre);

        JTextField textFieldPrecioAlmacen = new JTextField(10);
        JLabel labelPrecioAlmacen = new JLabel("商品进价: ");
        panel.add(labelPrecioAlmacen);
        panel.add(textFieldPrecioAlmacen);

        JTextField textFieldPrecio = new JTextField(10);
        JLabel labelPrecio = new JLabel("商品单价: ");
        panel.add(labelPrecio);
        panel.add(textFieldPrecio);

        JTextField textFieldAlmacen = new JTextField(10);
        JLabel labelAlmacen = new JLabel("仓库名称: ");
        panel.add(labelAlmacen);
        panel.add(textFieldAlmacen);

        //leer los datos del articulo (si es que tiene)
        textFieldNombre.setText(articulo_original.getNombre());
        textFieldPrecioAlmacen.setText(articulo_original.getPrecioCompra().toString());
        textFieldPrecio.setText(articulo_original.getPrecio().toString());
        if (articulo_original.getAlmacen() == null) {
            textFieldAlmacen.setText(this.almacen); //default nombre de almacen es el ultimo introducido por el usuario
        } else {
            textFieldAlmacen.setText(articulo_original.getAlmacen());
        }

        int option = showConfirmDialog(null, panel, "修改商品", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String nombre = textFieldNombre.getText();
            String precioAlm = textFieldPrecioAlmacen.getText();
            String precioStr = textFieldPrecio.getText();
            String almacenStr = textFieldAlmacen.getText();

            if (nombre.isEmpty() || precioAlm.isEmpty() || precioStr.isEmpty() || almacenStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: 请填写空白字段.", "Error", JOptionPane.ERROR_MESSAGE);
                return editarArticulo(articulo_original);
            }
            if (!precioAlm.matches("^[0-9]+(\\.[0-9]{1,2})?$") || !precioStr.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                JOptionPane.showMessageDialog(null, "Error: 具有无效值的字段.", "Error", JOptionPane.ERROR_MESSAGE);
                return editarArticulo(articulo_original);
            }

            try {
                this.almacen = almacenStr;
                double precio = Double.parseDouble(precioStr);
                double precioCompra = Double.parseDouble(precioAlm);

                if (precio == 0) {
                    JOptionPane.showMessageDialog(null, "Error: 价格字段不可为0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return editarArticulo(articulo_original);
                }

                Articulo articulo = new Articulo(articulo_original.getID(), nombre, BigDecimal.valueOf(precioCompra), BigDecimal.valueOf(precio), almacenStr);
                GestorBDD.articuloEjecutarCRUD(conn, SQL.sql_modificar_articulo, articulo);

                return true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error: 具有无效的价格字段.", "Error", JOptionPane.ERROR_MESSAGE);
                return editarArticulo(articulo_original);
            }
        }
        return false;
    }

    public void confirmarVenta(boolean factura) {
        Cliente cliente = null;
        int index = jTabbedPane.getSelectedIndex();
        if (index == jTabbedPane.getTabCount() - 1) {
            return;
        }

        if (factura) {
            cliente = customDialogoFactura();
        }

        JScrollPane selectedComponent = (JScrollPane) jTabbedPane.getComponentAt(index);
        TablaCustom table = (TablaCustom) selectedComponent.getViewport().getView();

        Map<Articulo, Integer> articulos = table.listarArticulos();
        Venta venta = new Venta(new BigInteger(table.getID_VENTA()), LocalDateTime.now());

        if (GestorBDD.ventaEjecutarCRUD(conn, SQL.sql_insertar_venta, venta)) {
            for (Map.Entry<Articulo, Integer> entry : articulos.entrySet()) {
                if (!GestorBDD.relacionEjecutarCRUD(conn, SQL.sql_insertar_relacion, venta.getID(), entry.getKey().getID(), entry.getValue())) {
                    //MENSAJE FALLADO, BORRAR TODAS LAS RELACIONES Y LA VENTA
                    JOptionPane.showMessageDialog(
                            null,
                            "Error: no se ha podido guardar la venta.\nBorrando las relaciones y cancelando acción.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    articulos.forEach((articulo, cantidad) -> {
                        GestorBDD.relacionEjecutarCRUD(conn, SQL.sql_borrar_relacion, venta.getID(), articulo.getID(), 0);
                    });
                    GestorBDD.ventaEjecutarCRUD(conn, SQL.sql_borrar_venta, venta);
                    return;
                }
            }
        }
        if (cliente == null) {
            generarTicket(new Ticket(articulos, venta, new BigDecimal(jTextFieldDescuento.getText()), new BigDecimal(jTextFieldRecibido.getText())));
        } else {
            generarTicket(new Ticket(articulos, venta, new BigDecimal(jTextFieldDescuento.getText()), new BigDecimal(jTextFieldRecibido.getText()), cliente));
        }

        cerrarCurrentTab();
    }

    private Cliente customDialogoFactura() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField textFieldDni = new JTextField(10);
        JLabel labeldni = new JLabel("DNI/NIE: ");
        panel.add(labeldni);
        panel.add(textFieldDni);

        JTextField textFieldNombre = new JTextField(10);
        JLabel labelNombre = new JLabel("名字: ");
        panel.add(labelNombre);
        panel.add(textFieldNombre);

        JTextField textDomicilio = new JTextField(10);
        JLabel labelDomicilio = new JLabel("地址: ");
        panel.add(labelDomicilio);
        panel.add(textDomicilio);

        JTextField textFieldTel = new JTextField(10);
        JLabel labelTel = new JLabel("电话: ");
        panel.add(labelTel);
        panel.add(textFieldTel);

        int option = showConfirmDialog(null, panel, "客人信息", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String dni = textFieldDni.getText();
            String nombre = textFieldNombre.getText();
            String domicilio = textDomicilio.getText();
            String telefono = textFieldTel.getText();

            if (dni.isEmpty() || nombre.isEmpty() || domicilio.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: 请填写空白字段.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogoFactura();
            }
            if (!telefono.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                JOptionPane.showMessageDialog(null, "Error: 具有无效的电话号码.", "Error", JOptionPane.ERROR_MESSAGE);
                return customDialogoFactura();
            }

            return new Cliente(dni, nombre, domicilio, telefono);
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jLabelIdVenta_num = new javax.swing.JLabel();
        jPanelVenta = new javax.swing.JPanel();
        jLabelTotal = new javax.swing.JLabel();
        jLabelRecibido = new javax.swing.JLabel();
        jLabelDevolver = new javax.swing.JLabel();
        jLabelDescuento = new javax.swing.JLabel();
        jTextFieldRecibido = new javax.swing.JTextField();
        jButtonConfirmarVenta = new javax.swing.JButton();
        jLabelErrorVenta = new javax.swing.JLabel();
        jButtonRecibir = new javax.swing.JButton();
        jTextFieldDescuento = new javax.swing.JTextField();
        jLabelTotal_num = new javax.swing.JLabel();
        jLabelDevolver_num = new javax.swing.JLabel();
        jButtonFactura = new javax.swing.JButton();
        jButtonTicketAnterior = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabbedPane = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuArticulos = new javax.swing.JMenu();
        jMenuVentas = new javax.swing.JMenu();
        jMenuConexion = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1161, 600));
        setSize(new java.awt.Dimension(0, 0));
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jPanelControles.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldNumBarra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí puedes especificar el componente al que deseas mover el foco
                jTextFieldCantidad.requestFocusInWindow();
            }
        });

        jLabelNumBarra.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        jLabelNumBarra.setText("条码:");

        jButtonAgregarArticulo.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        jButtonAgregarArticulo.setText("添加商品");
        jButtonAgregarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregarArticuloActionPerformed(evt);
            }
        });

        jLabelIdVenta.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        jLabelIdVenta.setText("出售ID: ");
        jLabelIdVenta.setToolTipText("");

        jTextFieldCantidad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí puedes especificar el componente al que deseas mover el foco
                jButtonAgregarArticulo.requestFocusInWindow();
            }
        });

        jLabelCantidad.setFont(new java.awt.Font("Microsoft YaHei", 0, 14)); // NOI18N
        jLabelCantidad.setText("数量:");

        jLabelError.setForeground(new java.awt.Color(255, 0, 0));

        jLabelIdVenta_num.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabelIdVenta_num.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout jPanelIntroducirLayout = new javax.swing.GroupLayout(jPanelIntroducir);
        jPanelIntroducir.setLayout(jPanelIntroducirLayout);
        jPanelIntroducirLayout.setHorizontalGroup(
            jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelIntroducirLayout.createSequentialGroup()
                .addGroup(jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelIdVenta_num, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(jPanelIntroducirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelIdVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelIdVenta_num, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabelTotal.setFont(new java.awt.Font("Microsoft YaHei", 0, 20)); // NOI18N
        jLabelTotal.setText("总价:");

        jLabelRecibido.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabelRecibido.setText("收取:");

        jLabelDevolver.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabelDevolver.setText("找钱:");

        jLabelDescuento.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jLabelDescuento.setText("降价:");

        jTextFieldRecibido.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextFieldRecibido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldRecibido.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí puedes especificar el componente al que deseas mover el foco
                jButtonRecibir.requestFocusInWindow();
            }
        });
        jTextFieldRecibido.setToolTipText("Cantidad recibida");

        jButtonConfirmarVenta.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jButtonConfirmarVenta.setText("确认销售");
        jButtonConfirmarVenta.setEnabled(false);
        jButtonConfirmarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarVentaActionPerformed(evt);
            }
        });

        jLabelErrorVenta.setForeground(new java.awt.Color(255, 0, 0));

        jButtonRecibir.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jButtonRecibir.setText("收取付款");
        jButtonRecibir.setEnabled(false);
        jButtonRecibir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecibirActionPerformed(evt);
            }
        });

        jTextFieldDescuento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextFieldDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldDescuento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí puedes especificar el componente al que deseas mover el foco
                jTextFieldRecibido.requestFocusInWindow();
            }
        });
        jTextFieldDescuento.setText("0");
        jTextFieldDescuento.setToolTipText("Cantidad restada a Total");

        jLabelTotal_num.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabelTotal_num.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTotal_num.setText("0.0€");
        jLabelTotal_num.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabelDevolver_num.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabelDevolver_num.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelDevolver_num.setText("0.0€");
        jLabelDevolver_num.setToolTipText("");

        jButtonFactura.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jButtonFactura.setText("账单");
        jButtonFactura.setEnabled(false);
        jButtonFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFacturaActionPerformed(evt);
            }
        });

        jButtonTicketAnterior.setFont(new java.awt.Font("Microsoft YaHei", 0, 18)); // NOI18N
        jButtonTicketAnterior.setText("打印上一张");
        jButtonTicketAnterior.setEnabled(false);
        jButtonTicketAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTicketAnteriorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelVentaLayout = new javax.swing.GroupLayout(jPanelVenta);
        jPanelVenta.setLayout(jPanelVentaLayout);
        jPanelVentaLayout.setHorizontalGroup(
            jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVentaLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelErrorVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelVentaLayout.createSequentialGroup()
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVentaLayout.createSequentialGroup()
                                .addComponent(jLabelRecibido)
                                .addGap(60, 60, 60))
                            .addGroup(jPanelVentaLayout.createSequentialGroup()
                                .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelDevolver)
                                    .addComponent(jLabelTotal)
                                    .addComponent(jLabelDescuento))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelTotal_num, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldDescuento, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                            .addComponent(jTextFieldRecibido, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelDevolver_num, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)))
                .addGap(18, 18, 18)
                .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanelVentaLayout.createSequentialGroup()
                            .addComponent(jButtonFactura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButtonConfirmarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButtonRecibir, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonTicketAnterior))
                .addGap(17, 17, 17))
        );
        jPanelVentaLayout.setVerticalGroup(
            jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVentaLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelVentaLayout.createSequentialGroup()
                        .addComponent(jButtonRecibir, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonConfirmarVenta, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                            .addComponent(jButtonFactura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanelVentaLayout.createSequentialGroup()
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelTotal)
                            .addComponent(jLabelTotal_num)
                            .addComponent(jButtonTicketAnterior))
                        .addGap(11, 11, 11)
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelRecibido)
                            .addGroup(jPanelVentaLayout.createSequentialGroup()
                                .addComponent(jTextFieldRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelDevolver)
                            .addComponent(jLabelDevolver_num))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelErrorVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelIntroducir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

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
                crearNuevoTab();
            }
        });

        jTabbedPane.addTab("", null);
        jTabbedPane.setTabComponentAt(0, botonNewTab);

        jScrollPane1.setViewportView(jTabbedPane);

        javax.swing.GroupLayout jPanelBaseLayout = new javax.swing.GroupLayout(jPanelBase);
        jPanelBase.setLayout(jPanelBaseLayout);
        jPanelBaseLayout.setHorizontalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanelControles, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1149, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelBaseLayout.setVerticalGroup(
            jPanelBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBaseLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelControles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenuArticulos.setText("商品");
        jMenuArticulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuArticulosMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuArticulos);

        jMenuVentas.setText("出售");
        jMenuVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuVentasMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuVentas);

        jMenuConexion.setText("IP地址");
        jMenuConexion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenuConexionMouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenuConexion);

        jMenu1.setText("import");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenuBar1.add(jMenu1);

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

        setSize(new java.awt.Dimension(1177, 608));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAgregarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAgregarArticuloActionPerformed
        if (validarCamposAgregar()) {
            addArticulo(new Articulo(new BigInteger(jTextFieldNumBarra.getText()), "", new BigDecimal(0)), Integer.parseInt(jTextFieldCantidad.getText()));
        }
        jTextFieldNumBarra.requestFocus();
    }//GEN-LAST:event_jButtonAgregarArticuloActionPerformed

    private void jTabbedPaneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPaneMousePressed
        // Borrar Tab abriendo un menú con click derecho
        if (SwingUtilities.isRightMouseButton(evt)) {
            // Obtener el índice de la pestaña que está debajo del puntero del ratón
            int index = jTabbedPane.indexAtLocation(evt.getX(), evt.getY());

            // Verificar que el índice es válido (es decir, que el ratón está sobre una pestaña)
            if (index != -1) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem cerrar = new JMenuItem("取消");  // "Cancelar" en chino
                cerrar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jTabbedPane.remove(index);
                        // Opcional: Seleccionar la pestaña anterior si existe
                        if (index > 0) {
                            jTabbedPane.setSelectedIndex(index - 1);
                        } else if (jTabbedPane.getTabCount() > 0) {
                            jTabbedPane.setSelectedIndex(0);
                        }
                    }
                });
                popupMenu.add(cerrar);
                popupMenu.show(jTabbedPane, evt.getX(), evt.getY());
            }
        }

    }//GEN-LAST:event_jTabbedPaneMousePressed

    private void jMenuConexionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuConexionMouseClicked
        String ip = "no obtenido";

        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            ip = datagramSocket.getLocalAddress().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            Exceptions.printStackTrace(e);
        }

        JOptionPane.showMessageDialog(null, "本地的IP地址:\n" + ip, "本地IP", JOptionPane.INFORMATION_MESSAGE);
//        DialogoConexion dialogo = new DialogoConexion(this, true);
//        dialogo.setVisible(true);
    }//GEN-LAST:event_jMenuConexionMouseClicked

    private void jButtonRecibirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecibirActionPerformed
        if (!validarCamposVenta()) {
            jTextFieldDescuento.requestFocus();
            return;
        }
        BigDecimal total = new BigDecimal(jLabelTotal_num.getText().replace("€", ""));
        BigDecimal descuento = new BigDecimal(jTextFieldDescuento.getText());
        BigDecimal recibido = new BigDecimal(jTextFieldRecibido.getText());

        BigDecimal devolver = (total.subtract(descuento)).subtract(recibido);
        if (recibido.compareTo(total.subtract(descuento)) < 0) {
            jLabelErrorVenta.setText("注意: 收入低于总价.");
        } else {
            jLabelDevolver_num.setText(devolver.toString() + "€");
            jButtonConfirmarVenta.setEnabled(true);
            jButtonFactura.setEnabled(true);
            jButtonConfirmarVenta.requestFocus();
        }
    }//GEN-LAST:event_jButtonRecibirActionPerformed

    private void jButtonConfirmarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarVentaActionPerformed
        confirmarVenta(false);
    }//GEN-LAST:event_jButtonConfirmarVentaActionPerformed

    private void jMenuArticulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuArticulosMouseClicked
        VentanaArticulos ventanaArticulos = new VentanaArticulos(conn, this);
        ventanaArticulos.setVisible(true);
    }//GEN-LAST:event_jMenuArticulosMouseClicked

    private void jMenuVentasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuVentasMouseClicked
        VentanaVentas ventanaVentas = new VentanaVentas(conn);
        ventanaVentas.setVisible(true);
    }//GEN-LAST:event_jMenuVentasMouseClicked

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained

    }//GEN-LAST:event_formFocusGained

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        leerArchivoXLS();
    }//GEN-LAST:event_jMenu1MouseClicked

    private void jButtonFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFacturaActionPerformed
        confirmarVenta(true);
    }//GEN-LAST:event_jButtonFacturaActionPerformed

    private void jButtonTicketAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTicketAnteriorActionPerformed
        generarTicket(this.ticketAnterior);
    }//GEN-LAST:event_jButtonTicketAnteriorActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
//            MaterialLookAndFeel lookAndFeel = new MaterialLookAndFeel();
//            javax.swing.UIManager.setLookAndFeel(lookAndFeel);
            javax.swing.UIManager.setLookAndFeel(new FlatLightLaf());

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PantallaPrincipal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        splash.dispose();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PantallaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAgregarArticulo;
    private javax.swing.JButton jButtonConfirmarVenta;
    private javax.swing.JButton jButtonFactura;
    private javax.swing.JButton jButtonRecibir;
    private javax.swing.JButton jButtonTicketAnterior;
    private javax.swing.JLabel jLabelCantidad;
    private javax.swing.JLabel jLabelDescuento;
    private javax.swing.JLabel jLabelDevolver;
    private javax.swing.JLabel jLabelDevolver_num;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelErrorVenta;
    private javax.swing.JLabel jLabelIdVenta;
    private javax.swing.JLabel jLabelIdVenta_num;
    private javax.swing.JLabel jLabelNumBarra;
    private javax.swing.JLabel jLabelRecibido;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JLabel jLabelTotal_num;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenuArticulos;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuConexion;
    private javax.swing.JMenu jMenuVentas;
    private javax.swing.JPanel jPanelBase;
    private javax.swing.JPanel jPanelControles;
    private javax.swing.JPanel jPanelIntroducir;
    private javax.swing.JPanel jPanelVenta;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextField jTextFieldCantidad;
    private javax.swing.JTextField jTextFieldDescuento;
    private javax.swing.JTextField jTextFieldNumBarra;
    private javax.swing.JTextField jTextFieldRecibido;
    // End of variables declaration//GEN-END:variables

}
