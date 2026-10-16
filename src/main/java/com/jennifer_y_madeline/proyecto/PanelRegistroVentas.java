package com.jennifer_y_madeline.proyecto;

import com.jennifer_y_madeline.proyecto.databases.Cliente;
import com.jennifer_y_madeline.proyecto.databases.ClienteDAO;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.jennifer_y_madeline.proyecto.databases.*;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.math.RoundingMode;

public class PanelRegistroVentas extends javax.swing.JPanel {
   
    private BigDecimal subtotalActual = BigDecimal.ZERO;
    private BigDecimal descuentoActual = BigDecimal.ZERO;
    private BigDecimal recargoActual = BigDecimal.ZERO;
    private BigDecimal totalFinalActual = BigDecimal.ZERO;
    private int porcentajeActual = 0;
    private final ClienteDAO dao = new ClienteDAO();
    private List<Cliente> listaClientes;
    private DefaultTableModel modeloTablaResumen;
    
    private final VentaDAO ventaDAO = new VentaDAO();
    private List<Producto> productos = new ArrayList<>();
    private final List<Venta> detalle = new ArrayList<>();

    private int ventasRegistradasSesion = 0;
    private BigDecimal acumuladoSesion = BigDecimal.ZERO;

    public PanelRegistroVentas() {
        initComponents();
        Imagenes.ponerPerroYGato(jLabel4, 90);
        inicializarLogicaVentas();
    }

    public void inicializarLogicaVentas() {
        configurarTabla();
        cargarClientesCombo();
        
        txtFecha.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")));
        txtFecha.setEditable(false);
        txtPrecio.setEditable(false);
        txtCantidad.setModel(new javax.swing.SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        txtProducto.addActionListener(e -> actualizarPrecio());
        jLabel26.setText("Descuento (0%)");
        txtPrecio1.setEditable(false); 
        btnCancelar.setText(" ✖  Cancelar producto");
        btnCancelar.setToolTipText("Seleccione un producto del resumen para quitarlo de la venta");
        cboCliente.addActionListener(e -> {
            actualizarTipoCliente();
            calcularTotalesGlobales();
        });
        cboPago.addActionListener(e -> calcularTotalesGlobales());
        cargarProductos();
        jButton1.addActionListener(e -> {
            VentanaMenu menu = menuPrincipal();
            if (menu != null) menu.irAProductos();
        });
        jButton2.addActionListener(e -> {
            VentanaMenu menu = menuPrincipal();
            if (menu != null) menu.irAEliminarVenta();
        });
    }

    
    private VentanaMenu menuPrincipal() {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        return (w instanceof VentanaMenu) ? (VentanaMenu) w : null;
    }

    
    private void actualizarTipoCliente() {
        int i = cboCliente.getSelectedIndex();
        txtPrecio1.setText(listaClientes != null && i >= 0 && i < listaClientes.size()
                ? listaClientes.get(i).getTipoCliente().getNombre() : "");
    }

    private void actualizarPrecio() {
        int i = txtProducto.getSelectedIndex();
        txtPrecio.setText(i >= 0 && i < productos.size() ? productos.get(i).getPrecioUnitario().toPlainString() : "");
    }

    private void cargarProductos() {
        btnAgregarProducto.setEnabled(false);
        btnRegistrarVenta.setEnabled(false);
        new SwingWorker<Object[], Void>() {
            protected Object[] doInBackground() { return new Object[]{ventaDAO.listarProductos(), ventaDAO.listarMetodos()}; }
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] datos = get();
                    productos = (List<Producto>) datos[0];
                    cboPago.removeAllItems();
                    for (MetodoPago pago : (List<MetodoPago>) datos[1]) cboPago.addItem(pago);
                    txtProducto.removeAllItems();
                    for (Producto producto : productos) txtProducto.addItem(producto.getNombre() + " (#" + producto.getId() + ")");
                    actualizarPrecio();
                    btnAgregarProducto.setEnabled(true);
                    btnRegistrarVenta.setEnabled(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PanelRegistroVentas.this, "Error al cargar productos: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void configurarTabla() {
        modeloTablaResumen = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(modeloTablaResumen); 
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    private void cargarClientesCombo() {
        new SwingWorker<List<Cliente>, Void>() {
            @Override
            protected List<Cliente> doInBackground() {
                return dao.listar();
            }
            @Override
            protected void done() {
                try {
                    listaClientes = get();
                    cboCliente.removeAllItems();
                    for (Cliente c : listaClientes) {
                        cboCliente.addItem(c.getNombre());
                    }
                    if (cboCliente.getItemCount() > 0) {
                        cboCliente.setSelectedIndex(0);
                    }
                    actualizarTipoCliente();
                } catch (Exception ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(PanelRegistroVentas.this, "Error al cargar clientes: " + ex);
}
            }
        }.execute();
    }

    private void calcularTotalesGlobales() {
   
    BigDecimal subtotal = BigDecimal.ZERO;
    modeloTablaResumen.setRowCount(0);
    for (int i = 0; i < detalle.size(); i++) {
        Venta linea = detalle.get(i);
        BigDecimal subtotalProducto = linea.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(linea.getCantidad()))
                .setScale(2, RoundingMode.HALF_UP);
        modeloTablaResumen.addRow(new Object[]{linea.getProducto().getNombre(), linea.getCantidad(),
            "Q " + subtotalProducto.toPlainString()});
        subtotal = subtotal.add(subtotalProducto);
    }
    subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);


    int porcentaje = 0;
    int indiceCliente = cboCliente.getSelectedIndex();
    if (listaClientes != null && indiceCliente >= 0 && indiceCliente < listaClientes.size()) {
        String tipo = listaClientes.get(indiceCliente).getTipoCliente().getNombre();
        porcentaje = obtenerPorcentajeDescuento(tipo, subtotal);
    }
    porcentajeActual = porcentaje;
    BigDecimal descuento = subtotal.multiply(BigDecimal.valueOf(porcentaje))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    BigDecimal totalConDescuento = subtotal.subtract(descuento);

  
    BigDecimal recargo = BigDecimal.ZERO;
    if (esTarjeta((MetodoPago) cboPago.getSelectedItem())) {
        recargo = totalConDescuento.multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP);
    }
    BigDecimal totalFinal = totalConDescuento.add(recargo);

    subtotalActual = subtotal;
    descuentoActual = descuento;
    recargoActual = recargo;
    totalFinalActual = totalFinal;

    jLabel26.setText("Descuento (" + porcentaje + "%)");
    lblSubtotalMonto.setText("Q " + subtotal.toPlainString());
    lblDescuentoMonto.setText("- Q " + descuento.toPlainString());
    lblTotalMonto.setText("Q " + totalFinal.toPlainString()
            + (recargo.signum() > 0 ? " (incl. recargo 2%)" : ""));
    lblTotalMonto.setToolTipText(recargo.signum() > 0
            ? "Incluye recargo (2%): Q " + recargo.toPlainString() : null);
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
        jLabel1 = new javax.swing.JLabel();
        lblTitulo1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cardFormulario = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        cboCliente = new javax.swing.JComboBox<>();
        txtProducto = new javax.swing.JComboBox<>();
        cboPago = new javax.swing.JComboBox<>();
        txtCantidad = new javax.swing.JSpinner();
        txtPrecio = new javax.swing.JTextField();
        txtFecha = new javax.swing.JTextField();
        lblTitulo3 = new javax.swing.JLabel();
        lblTitulo4 = new javax.swing.JLabel();
        lblTitulo5 = new javax.swing.JLabel();
        lblTitulo6 = new javax.swing.JLabel();
        lblTitulo7 = new javax.swing.JLabel();
        lblTitulo8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        txtPrecio1 = new javax.swing.JTextField();
        lblTitulo9 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        cardResumen = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        tblResumen = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        lblSubtotalMonto = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        lblDescuentoMonto = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        lblTotalMonto = new javax.swing.JLabel();
        btnRegistrarVenta = new javax.swing.JButton();
        btnAgregarProducto = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 251, 250));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(116, 96, 153));
        jLabel1.setText("Registro de Ventas");

        lblTitulo1.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo1.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 40)
        );
        lblTitulo1.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo1.setText("🛍️");

        jLabel14.setForeground(new java.awt.Color(175, 175, 175));
        jLabel14.setText("Completo los datos para registrar una venta ");

        cardFormulario.setBackground(new java.awt.Color(248, 246, 252));
        cardFormulario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(79, 57, 122));
        jLabel12.setText("Cliente");

        jLabel13.setForeground(new java.awt.Color(252, 0, 0));
        jLabel13.setText("*");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(79, 57, 122));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Cantidad");

        jLabel16.setForeground(new java.awt.Color(252, 0, 0));
        jLabel16.setText("*");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel17.setForeground(new java.awt.Color(252, 0, 0));
        jLabel17.setText("*");
        jLabel17.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(79, 57, 122));
        jLabel18.setText("Fecha");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(79, 57, 122));
        jLabel19.setText("Producto");

        jLabel20.setForeground(new java.awt.Color(252, 0, 0));
        jLabel20.setText("*");
        jLabel20.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(79, 57, 122));
        jLabel21.setText("Precio Unitario");

        jLabel22.setForeground(new java.awt.Color(252, 0, 0));
        jLabel22.setText("*");
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(79, 57, 122));
        jLabel23.setText("Metodo de pago");

        jLabel24.setForeground(new java.awt.Color(252, 0, 0));
        jLabel24.setText("*");
        jLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        cboCliente.setBackground(new java.awt.Color(239, 235, 245));
        cboCliente.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));

        txtProducto.setBackground(new java.awt.Color(239, 235, 245));
        txtProducto.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));

        cboPago.setBackground(new java.awt.Color(239, 235, 245));
        cboPago.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));

        txtCantidad.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));

        txtPrecio.setBackground(new java.awt.Color(239, 235, 245));
        txtPrecio.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        txtPrecio.addActionListener(this::txtPrecioActionPerformed);

        txtFecha.setBackground(new java.awt.Color(239, 235, 245));
        txtFecha.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));

        lblTitulo3.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo3.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo3.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo3.setText("  👤");

        lblTitulo4.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo4.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo4.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo4.setText("  #️");

        lblTitulo5.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo5.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo5.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo5.setText("  📅");

        lblTitulo6.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo6.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo6.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo6.setText("  📦");

        lblTitulo7.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo7.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo7.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo7.setText("  💳");

        lblTitulo8.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo8.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo8.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo8.setText("  💲");

        jLabel2.setForeground(new java.awt.Color(183, 169, 209));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("──────────────────────────────────────────────");

        jButton1.setBackground(new java.awt.Color(225, 215, 240));
        jButton1.setForeground(new java.awt.Color(79, 57, 122));
        jButton1.setText("📖  Revisar catalogo de productos");
        jButton1.setBorder(null);

        txtPrecio1.setBackground(new java.awt.Color(239, 235, 245));
        txtPrecio1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        txtPrecio1.addActionListener(this::txtPrecio1ActionPerformed);

        lblTitulo9.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo9.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo9.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo9.setText("  👥");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(79, 57, 122));
        jLabel27.setText("Tipo de cliente");

        jLabel28.setForeground(new java.awt.Color(252, 0, 0));
        jLabel28.setText("*");
        jLabel28.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        btnCancelar.setBackground(new java.awt.Color(253, 203, 203));
        btnCancelar.setForeground(new java.awt.Color(143, 12, 12));
        btnCancelar.setText(" ✖  Cancelar");
        btnCancelar.setBorder(null);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        jButton2.setBackground(new java.awt.Color(253, 203, 203));
        jButton2.setForeground(new java.awt.Color(143, 12, 12));
        jButton2.setText(" 🚮  Eliminar Venta");
        jButton2.setBorder(null);

        javax.swing.GroupLayout cardFormularioLayout = new javax.swing.GroupLayout(cardFormulario);
        cardFormulario.setLayout(cardFormularioLayout);
        cardFormularioLayout.setHorizontalGroup(
            cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardFormularioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cardFormularioLayout.createSequentialGroup()
                        .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(cardFormularioLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(cardFormularioLayout.createSequentialGroup()
                                        .addComponent(lblTitulo5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel18)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(cardFormularioLayout.createSequentialGroup()
                                        .addComponent(lblTitulo4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel15)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(cardFormularioLayout.createSequentialGroup()
                                        .addComponent(lblTitulo3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(cardFormularioLayout.createSequentialGroup()
                                    .addComponent(lblTitulo9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(5, 5, 5)
                                    .addComponent(jLabel27)
                                    .addGap(4, 4, 4)
                                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtPrecio1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(cardFormularioLayout.createSequentialGroup()
                                .addComponent(lblTitulo7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(cardFormularioLayout.createSequentialGroup()
                                .addComponent(lblTitulo6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(cardFormularioLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lblTitulo8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtProducto, 0, 190, Short.MAX_VALUE)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .addComponent(cboPago, 0, 190, Short.MAX_VALUE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cardFormularioLayout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        cardFormularioLayout.setVerticalGroup(
            cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardFormularioLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(lblTitulo3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTitulo6, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(lblTitulo8, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTitulo9, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecio1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(cardFormularioLayout.createSequentialGroup()
                        .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jLabel24)
                            .addComponent(lblTitulo7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPago, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(cardFormularioLayout.createSequentialGroup()
                        .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(lblTitulo4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel17)
                    .addComponent(lblTitulo5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(cardFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        cardResumen.setBackground(new java.awt.Color(248, 246, 252));
        cardResumen.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        jTextField1.setBackground(new java.awt.Color(239, 235, 245));
        jTextField1.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 12)

        );
        jTextField1.setForeground(new java.awt.Color(79, 57, 122));
        jTextField1.setText("📝  Resumen de venta");
        jTextField1.setToolTipText("");
        jTextField1.setBorder(null);

        tblResumen.setBackground(new java.awt.Color(248, 246, 252));
        tblResumen.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(228, 228, 228), 2, true));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Producto", "Cantidad", "Subtotal"
            }
        ));
        tblResumen.setViewportView(jTable1);

        jPanel2.setBackground(new java.awt.Color(248, 246, 252));
        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(228, 228, 228), 2, true));
        jPanel2.setForeground(new java.awt.Color(79, 57, 122));

        jLabel25.setForeground(new java.awt.Color(79, 57, 122));
        jLabel25.setText("Subtotal");

        lblSubtotalMonto.setForeground(new java.awt.Color(79, 57, 122));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(79, 57, 122));
        jLabel26.setText("% Descuento");

        lblDescuentoMonto.setForeground(new java.awt.Color(79, 57, 122));

        jLabel3.setForeground(new java.awt.Color(183, 169, 209));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("──────────────────────────────────────");

        jTextField4.setBackground(new java.awt.Color(239, 235, 245));
        jTextField4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(79, 57, 122));
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField4.setText("Q  TOTAL");
        jTextField4.setBorder(null);

        lblTotalMonto.setBackground(new java.awt.Color(239, 235, 245));
        lblTotalMonto.setForeground(new java.awt.Color(79, 57, 122));
        lblTotalMonto.setToolTipText("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblSubtotalMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel25)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel26)
                            .addGap(18, 18, 18)
                            .addComponent(lblDescuentoMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblSubtotalMonto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDescuentoMonto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout cardResumenLayout = new javax.swing.GroupLayout(cardResumen);
        cardResumen.setLayout(cardResumenLayout);
        cardResumenLayout.setHorizontalGroup(
            cardResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(cardResumenLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(cardResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tblResumen, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        cardResumenLayout.setVerticalGroup(
            cardResumenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardResumenLayout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblResumen, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnRegistrarVenta.setBackground(new java.awt.Color(116, 96, 153));
        btnRegistrarVenta.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrarVenta.setText(" 💾  Regsitar Venta");
        btnRegistrarVenta.setBorder(null);
        btnRegistrarVenta.addActionListener(this::btnRegistrarVentaActionPerformed);

        btnAgregarProducto.setBackground(new java.awt.Color(220, 242, 228));
        btnAgregarProducto.setForeground(new java.awt.Color(20, 100, 50));
        btnAgregarProducto.setText(" ➕  Agregar Producto  ");
        btnAgregarProducto.setBorder(null);
        btnAgregarProducto.addActionListener(this::btnAgregarProductoActionPerformed);

        jLabel4.setText("imagen perroygato");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cardFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cardResumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRegistrarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(28, 28, 28))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cardFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(cardResumen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRegistrarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtPrecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioActionPerformed

    private void btnAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductoActionPerformed
        int indice = txtProducto.getSelectedIndex();
        if (indice < 0 || indice >= productos.size()) return;
        try {
            txtCantidad.commitEdit();
            int cantidad = ((Number) txtCantidad.getValue()).intValue();
            Producto producto = productos.get(indice);
            int reservada = detalle.stream().filter(v -> v.getProducto().getId().equals(producto.getId())).mapToInt(Venta::getCantidad).sum();
            if (cantidad <= 0 || cantidad > producto.getCantidad() - reservada) {
                JOptionPane.showMessageDialog(this, "Cantidad inv\u00e1lida o existencias insuficientes.");
                return;
            }
            
            int pos = 0;
            boolean encontrado = false;
            while (pos < detalle.size() && !encontrado) {
                if (detalle.get(pos).getProducto().getId().equals(producto.getId())) {
                    encontrado = true;
                } else {
                    pos++;
                }
            }
            if (encontrado) {
                Venta existente = detalle.get(pos);
                existente.setCantidad(existente.getCantidad() + cantidad);
            } else {
                Venta venta = new Venta();
                venta.setProducto(producto);
                venta.setCantidad(cantidad);
                venta.setPrecioUnitario(producto.getPrecioUnitario());
                detalle.add(venta);
            }
            txtCantidad.setValue(1);
            calcularTotalesGlobales();
        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad v\u00e1lida.");
        }
    }//GEN-LAST:event_btnAgregarProductoActionPerformed

    private void btnRegistrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarVentaActionPerformed
        int indice = cboCliente.getSelectedIndex();
        MetodoPago pago = (MetodoPago) cboPago.getSelectedItem();
        if (pago == null || listaClientes == null || indice < 0 || productos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione el cliente y la forma de pago.");
            return;
        }

        if (detalle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto a la venta.");
            return;
        }

       
        int contadorProductos = detalle.size();
        int totalUnidades = 0;
        for (int i = 0; i < detalle.size(); i++) {
            totalUnidades += detalle.get(i).getCantidad();
        }

     
        calcularTotalesGlobales();
        BigDecimal totalFinal = totalFinalActual;

        
        BigDecimal recibido = null;
        BigDecimal cambio = BigDecimal.ZERO;
        if (esEfectivo(pago)) {
            recibido = solicitarEfectivo(totalFinal);
            if (recibido == null) return;           
            cambio = recibido.subtract(totalFinal); 
        }

        Cliente cliente = listaClientes.get(indice);
        LocalDateTime fecha = LocalDateTime.now();
        for (Venta venta : detalle) {
            venta.setCliente(cliente);
            venta.setMetodoPago(pago);
            venta.setFecha(fecha);
        }
        try {
            ventaDAO.guardar(detalle);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la venta: " + ex.getMessage());
            return;
        }

       
        ventasRegistradasSesion++;
        acumuladoSesion = acumuladoSesion.add(totalFinal);

        
        VentanaFactura factura = new VentanaFactura(cliente, detalle,
                subtotalActual, descuentoActual, porcentajeActual,
                recargoActual, totalFinal,
                pago, recibido, cambio);
        javax.swing.JDialog dialogoFactura = new javax.swing.JDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                "Factura - venta #" + ventasRegistradasSesion + " de la sesión  |  "
                        + contadorProductos + " producto(s), " + totalUnidades + " unidad(es)  |  "
                        + "Acumulado: Q " + acumuladoSesion.toPlainString(),
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialogoFactura.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialogoFactura.setContentPane(factura);
        dialogoFactura.pack();
        dialogoFactura.setResizable(false);
        dialogoFactura.setLocationRelativeTo(this);
        dialogoFactura.setVisible(true);   

       
        detalle.clear();
        modeloTablaResumen.setRowCount(0);
        calcularTotalesGlobales();
        txtFecha.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")));
        cargarProductos();
        VentanaMenu menu = menuPrincipal();
        if (menu != null) menu.irAInicio();
    }//GEN-LAST:event_btnRegistrarVentaActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        
        int fila = jTable1.getSelectedRow();
        if (fila < 0 || fila >= detalle.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.");
            return;
        }
        Venta linea = detalle.get(fila);

        int cuantas = 1;
        if (linea.getCantidad() >= 2) {
            Integer respuesta = solicitarCuantasUnidades(linea);
            if (respuesta == null) return;     
            cuantas = respuesta;
        }

        BigDecimal montoQuitado = linea.getPrecioUnitario().multiply(BigDecimal.valueOf(cuantas))
                .setScale(2, RoundingMode.HALF_UP);
        String resumen = linea.getProducto().getNombre() + "  ·  " + cuantas
                + (cuantas == 1 ? " unidad" : " unidades") + "  ·  Q " + montoQuitado.toPlainString();
        if (!PanelMensajeEliminar.confirmar(jTable1, "el producto", resumen)) {
            return;
        }

        if (cuantas >= linea.getCantidad()) {
            detalle.remove(fila);                                   
        } else {
            linea.setCantidad(linea.getCantidad() - cuantas);       
        }
        calcularTotalesGlobales();    
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtPrecio1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecio1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecio1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarProducto;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnRegistrarVenta;
    private javax.swing.JPanel cardFormulario;
    private javax.swing.JPanel cardResumen;
    private javax.swing.JComboBox<String> cboCliente;
    private javax.swing.JComboBox<com.jennifer_y_madeline.proyecto.databases.MetodoPago> cboPago;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel lblDescuentoMonto;
    private javax.swing.JLabel lblSubtotalMonto;
    private javax.swing.JLabel lblTitulo1;
    private javax.swing.JLabel lblTitulo3;
    private javax.swing.JLabel lblTitulo4;
    private javax.swing.JLabel lblTitulo5;
    private javax.swing.JLabel lblTitulo6;
    private javax.swing.JLabel lblTitulo7;
    private javax.swing.JLabel lblTitulo8;
    private javax.swing.JLabel lblTitulo9;
    private javax.swing.JLabel lblTotalMonto;
    private javax.swing.JScrollPane tblResumen;
    private javax.swing.JSpinner txtCantidad;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtPrecio1;
    private javax.swing.JComboBox<String> txtProducto;
    // End of variables declaration//GEN-END:variables

private int obtenerPorcentajeDescuento(String tipoCliente, BigDecimal monto) {
    String tipo = tipoCliente == null ? "" : tipoCliente.trim().toLowerCase();
    if (tipo.contains("corporativo")) {
        if (monto.compareTo(new BigDecimal("1000")) >= 0) return 20;
        if (monto.compareTo(new BigDecimal("500")) >= 0) return 15;
        return 10;
    } else if (tipo.contains("frecuente")) {
        if (monto.compareTo(new BigDecimal("500")) >= 0) return 15;
        if (monto.compareTo(new BigDecimal("200")) >= 0) return 10;
        return 5;
    } else if (tipo.contains("regular")) {
        if (monto.compareTo(new BigDecimal("500")) >= 0) return 8;
        if (monto.compareTo(new BigDecimal("200")) >= 0) return 5;
        return 0;
    }
    return 0;
}

  
    private BigDecimal solicitarEfectivo(BigDecimal total) {
        BigDecimal recibido = null;
        boolean valido;
        do {
            valido = false;
            String entrada = JOptionPane.showInputDialog(this,
                    "Total a pagar: Q " + total.toPlainString() + "\nIngrese la cantidad recibida:",
                    "Pago en efectivo", JOptionPane.QUESTION_MESSAGE);
            if (entrada == null) return null;
            try {
                BigDecimal monto = new BigDecimal(entrada.trim().replace(",", ""));
                if (monto.compareTo(total) < 0) {
                    JOptionPane.showMessageDialog(this,
                            "La cantidad recibida es menor que el total. Ingrese un valor v\u00e1lido.");
                } else {
                    recibido = monto.setScale(2, RoundingMode.HALF_UP);
                    valido = true;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un n\u00famero v\u00e1lido.");
            }
        } while (!valido);
        return recibido;
    }


    private Integer solicitarCuantasUnidades(Venta linea) {
        int maximo = linea.getCantidad();
        int cantidad = 0;
        boolean valido;
        do {
            String entrada = JOptionPane.showInputDialog(this,
                    linea.getProducto().getNombre() + " lleva " + maximo + " unidades.\n¿Cuántas quieres quitar? (1 a " + maximo + ")",
                    "Cancelar producto", JOptionPane.QUESTION_MESSAGE);
            if (entrada == null) return null;       
            valido = false;
            try {
                cantidad = Integer.parseInt(entrada.trim());
                if (cantidad >= 1 && cantidad <= maximo) {
                    valido = true;
                } else {
                    JOptionPane.showMessageDialog(this, "Ingrese un número entre 1 y " + maximo + ".");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número entero válido.");
            }
        } while (!valido);
        return cantidad;
    }

    private boolean esEfectivo(MetodoPago pago) {
        return pago != null && pago.getNombre() != null
                && pago.getNombre().toLowerCase().contains("efectivo");
    }

    private boolean esTarjeta(MetodoPago pago) {
        return pago != null && pago.getNombre() != null
                && pago.getNombre().toLowerCase().contains("tarjeta");
    }
}
