package com.jennifer_y_madeline.proyecto;

import com.jennifer_y_madeline.proyecto.databases.Producto;
import com.jennifer_y_madeline.proyecto.databases.ProductoDAO;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jpaol
 */
public class PanelListaProductos extends javax.swing.JPanel {

    private static final int STOCK_BAJO = 10;   // menos de 10 = "Poco stock"
    private static final String PLACEHOLDER = "🔍 Nombre o código...";

    private final ProductoDAO dao = new ProductoDAO();
    private List<Producto> todos = new ArrayList<>();
    private DefaultTableModel modelo;

    /**
     * Creates new form PanelListaProductos
     */
    public PanelListaProductos() {
        initComponents();
        Imagenes.ponerLogo2(jLabel1, 70);
        Imagenes.ponerPerroYGato(jLabel3, 100);
        inicializarLogica();
    }

    private void inicializarLogica() {
        configurarTarjetas();
        configurarMenu();
        configurarTabla();
        configurarBusqueda();
        btnNuevo.addActionListener(e -> nuevoProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnAgregarStock.addActionListener(e -> agregarStock());
        btnEliminar.addActionListener(e -> eliminarProducto());
        recargar();
    }

    // ============ TARJETAS (Productos y Stock bajo) ============

    private void configurarTarjetas() {
        lblIconoBajo.setText("  ⚠️");
        lblIconoBajo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        lblIconoBajo.setForeground(new Color(200, 150, 40));
        lblTxtBajo.setText("Stock bajo");
        lblTxtBajo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTxtBajo.setForeground(new Color(140, 135, 135));

        lblTotalProductos.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblStockBajo.setFont(new Font("Segoe UI", Font.BOLD, 17));

        // Son solo para mostrar datos: no se pueden editar ni seleccionar
        JTextField[] soloLectura = {lblTotalProductos, lblIconoBajo, lblTxtBajo, lblStockBajo};
        for (JTextField f : soloLectura) {
            f.setEditable(false);
            f.setFocusable(false);
        }
    }

    // ============ MENU LATERAL (hover y clic) ============

    private void configurarMenu() {
        estilizarBotonMenu(btnProductos, true);   // pagina actual: queda aclarado
        estilizarBotonMenu(btnVentas, false);

        btnProductos.addActionListener(e -> recargar());
        btnVentas.addActionListener(e -> {
            VentanaMenu menu = menuPrincipal();
            if (menu != null) menu.irAVentas();
        });
    }

    /**
     * Morado normal; al pasar el mouse se aclara; al presionar se oscurece.
     * activo = true deja el boton aclarado (pagina en la que estas).
     */
    private void estilizarBotonMenu(JButton b, boolean activo) {
        final Color normal = new Color(55, 38, 88);
        final Color claro = new Color(98, 77, 140);
        final Color oscuro = new Color(33, 22, 54);
        final Color reposo = activo ? claro : normal;

        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(reposo);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean dentro = false;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                dentro = true;
                b.setBackground(claro);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                dentro = false;
                b.setBackground(reposo);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                b.setBackground(oscuro);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                b.setBackground(dentro ? claro : reposo);
            }
        });
    }

    /** Devuelve la ventana principal (la que tiene el menú), o null si no está dentro de ella. */
    private VentanaMenu menuPrincipal() {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        return (w instanceof VentanaMenu) ? (VentanaMenu) w : null;
    }

    // ============ BUSQUEDA ============

    private void configurarBusqueda() {
        final Color gris = new Color(160, 160, 170);
        final Color oscuro = new Color(60, 40, 100);
        txtBuscar.setText(PLACEHOLDER);
        txtBuscar.setForeground(gris);

        txtBuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().equals(PLACEHOLDER)) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(oscuro);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setText(PLACEHOLDER);
                    txtBuscar.setForeground(gris);
                }
            }
        });

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });
    }

    private String textoBusqueda() {
        String t = txtBuscar.getText().trim();
        return t.equals(PLACEHOLDER) ? "" : t;
    }

    // ============ TABLA: [emoji] [producto + estado] [cantidad] ============

    private String estado(Producto p) {
        int c = p.getCantidad();
        if (c <= 0) return "Nada";
        if (c < STOCK_BAJO) return "Poco stock";
        return "Suficiente";
    }

    private String emojiEstado(Producto p) {
        int c = p.getCantidad();
        if (c <= 0) return "❌";
        if (c < STOCK_BAJO) return "⚠️";
        return "✅";
    }

    private Color colorEstado(Producto p) {
        int c = p.getCantidad();
        if (c <= 0) return new Color(200, 40, 60);
        if (c < STOCK_BAJO) return new Color(220, 110, 30);
        return new Color(20, 130, 60);
    }

    private String codigo(Producto p) {
        return "PR" + String.format("%03d", p.getId());
    }

    private String html(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void configurarTabla() {
        modelo = new DefaultTableModel(new Object[]{"", "Producto y estado", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProductos.setModel(modelo);
        tblProductos.setRowHeight(56);
        tblProductos.setShowVerticalLines(false);
        tblProductos.setGridColor(new Color(236, 232, 244));
        tblProductos.setSelectionBackground(new Color(236, 228, 248));
        tblProductos.setSelectionForeground(new Color(68, 44, 106));
        tblProductos.getTableHeader().setReorderingAllowed(false);
        tblProductos.getColumnModel().getColumn(0).setMaxWidth(80);
        tblProductos.getColumnModel().getColumn(2).setMaxWidth(110);

        // Columna 1: emoji del estado
        tblProductos.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, "", sel, foc, row, col);
                setText(emojiEstado((Producto) v));
                setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        // Columna 2: nombre del producto y su estado (Poco stock / Suficiente / Nada)
        tblProductos.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, "", sel, foc, row, col);
                Producto p = (Producto) v;
                Color c = colorEstado(p);
                String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
                setText("<html><b>" + html(p.getNombre()) + "</b><br>"
                        + "<font size='3' color='" + hex + "'>" + estado(p) + "</font>"
                        + "<font size='3' color='#9a92b0'>  ·  " + codigo(p) + "</font></html>");
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setHorizontalAlignment(LEFT);
                return this;
            }
        });

        // Columna 3: cuantos hay
        tblProductos.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, "", sel, foc, row, col);
                Producto p = (Producto) v;
                setText(String.valueOf(p.getCantidad()));
                setFont(new Font("Segoe UI", Font.BOLD, 18));
                setForeground(sel ? t.getSelectionForeground() : colorEstado(p));
                setHorizontalAlignment(CENTER);
                return this;
            }
        });
    }

    // ============ DATOS ============

    private void recargar() {
        final java.awt.Component padre = this;
        new SwingWorker<List<Producto>, Void>() {
            @Override
            protected List<Producto> doInBackground() {
                return dao.listar();
            }

            @Override
            protected void done() {
                try {
                    todos = get();
                    actualizarEstadisticas();
                    filtrar();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(padre, "Error al cargar productos: " + ex);
                }
            }
        }.execute();
    }

    private void filtrar() {
        String q = textoBusqueda().toLowerCase();
        modelo.setRowCount(0);
        for (Producto p : todos) {
            if (q.isEmpty()
                    || p.getNombre().toLowerCase().contains(q)
                    || codigo(p).toLowerCase().contains(q)) {
                modelo.addRow(new Object[]{p, p, p});
            }
        }
    }

    private void actualizarEstadisticas() {
        long bajos = todos.stream().filter(p -> p.getCantidad() < STOCK_BAJO).count();
        lblTotalProductos.setText(String.valueOf(todos.size()));
        lblStockBajo.setText(String.valueOf(bajos));
    }

    // ============ NUEVO / EDITAR / ELIMINAR ============

    private Producto seleccionado() {
        int fila = tblProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla.");
            return null;
        }
        return (Producto) modelo.getValueAt(tblProductos.convertRowIndexToModel(fila), 0);
    }

    /** Muestra el formulario (vacio o con los datos de base). Devuelve null si cancela. */
    private Producto pedirProducto(Producto base) {
        JTextField fNombre = new JTextField(base == null ? "" : base.getNombre(), 20);
        JTextField fCantidad = new JTextField(base == null ? "" : String.valueOf(base.getCantidad()), 20);
        JTextField fPrecio = new JTextField(base == null ? "" : base.getPrecioUnitario().toPlainString(), 20);
        Object[] campos = {"Nombre:", fNombre, "Cantidad:", fCantidad, "Precio unitario (Q):", fPrecio};
        String titulo = base == null ? "Nuevo producto" : "Editar producto";

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, campos, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (op != JOptionPane.OK_OPTION) return null;

            String nombre = fNombre.getText().trim();
            if (nombre.isEmpty() || nombre.length() > 100) {
                JOptionPane.showMessageDialog(this, "Ingrese un nombre válido (máximo 100 caracteres).");
                continue;
            }

            int cantidad;
            try {
                cantidad = Integer.parseInt(fCantidad.getText().trim());
                if (cantidad < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser un entero mayor o igual a 0.");
                continue;
            }

            BigDecimal precio;
            try {
                precio = new BigDecimal(fPrecio.getText().trim().replace("Q", "").replace(",", "").trim())
                        .setScale(2, RoundingMode.HALF_UP);
                if (precio.signum() <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número mayor a 0. Ejemplo: 55.00");
                continue;
            }

            Producto p = new Producto();
            p.setNombre(nombre);
            p.setCantidad(cantidad);
            p.setPrecioUnitario(precio);
            return p;
        }
    }

    private void nuevoProducto() {
        Producto p = pedirProducto(null);
        if (p == null) return;
        try {
            dao.guardar(p);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo registrar el producto: " + ex.getMessage());
            return;
        }
        recargar();
    }

    private void editarProducto() {
        Producto sel = seleccionado();
        if (sel == null) return;
        Producto datos = pedirProducto(sel);
        if (datos == null) return;
        try {
            dao.actualizar(sel.getId(), datos.getNombre(), datos.getCantidad(), datos.getPrecioUnitario());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el producto: " + ex.getMessage());
            return;
        }
        recargar();
    }

    private void agregarStock() {
        Producto sel = seleccionado();
        if (sel == null) return;
        String entrada = JOptionPane.showInputDialog(this,
                "Producto: " + sel.getNombre() + "\nStock actual: " + sel.getCantidad()
                + "\n\nCantidad a agregar:", "Agregar stock", JOptionPane.QUESTION_MESSAGE);
        if (entrada == null) return;
        int cantidad;
        try {
            cantidad = Integer.parseInt(entrada.trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número entero mayor a 0.");
            return;
        }
        try {
            int nuevo = dao.agregarStock(sel.getId(), cantidad);
            JOptionPane.showMessageDialog(this, "Stock actualizado. Ahora hay " + nuevo
                    + " unidades de \"" + sel.getNombre() + "\".");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo agregar el stock: " + ex.getMessage());
            return;
        }
        recargar();
    }

    private void eliminarProducto() {
        Producto sel = seleccionado();
        if (sel == null) return;
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar \"" + sel.getNombre() + "\"?", "Eliminar producto",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op != JOptionPane.YES_OPTION) return;
        try {
            dao.eliminar(sel.getId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() != null ? ex.getMessage() : "No se pudo eliminar el producto.");
            return;
        }
        recargar();
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        btnProductos = new javax.swing.JButton();
        btnVentas = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblTitulo6 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblTotalProductos = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        lblIconoBajo = new javax.swing.JTextField();
        lblStockBajo = new javax.swing.JTextField();
        lblTxtBajo = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        lblSubtotal = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnNuevo = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        btnEditar = new javax.swing.JButton();
        btnAgregarStock = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 251, 250));

        jPanel2.setBackground(new java.awt.Color(55, 38, 88));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("logo");

        jLabel17.setForeground(new java.awt.Color(183, 169, 209));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("──────────────────────────────────────────");

        btnProductos.setText("  📦   Productos");
        btnProductos.setBorder(null);
        btnProductos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProductos.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnVentas.setText("  💵  Registrar Ventas");
        btnVentas.setBorder(null);
        btnVentas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVentas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnVentas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                    .addComponent(btnProductos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(67, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(27, 27, 27)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addComponent(btnProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(116, 96, 153));
        jLabel2.setText("Lista de Productos");

        jLabel14.setForeground(new java.awt.Color(175, 175, 175));
        jLabel14.setText("Administra el inventario de tu tienda");

        jPanel3.setBackground(new java.awt.Color(253, 252, 253));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(228, 228, 228), 2, true));

        lblTitulo6.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo6.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 15));
        lblTitulo6.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo6.setText("  📦");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(140, 135, 135));
        jLabel19.setText("Productos");

        lblTotalProductos.setBackground(new java.awt.Color(253, 252, 253));
        lblTotalProductos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalProductos.setForeground(new java.awt.Color(116, 96, 153));
        lblTotalProductos.setBorder(null);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(lblTitulo6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo6, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(253, 252, 253));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(228, 228, 228), 2, true));

        lblIconoBajo.setBackground(new java.awt.Color(253, 252, 253));
        lblIconoBajo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblIconoBajo.setForeground(new java.awt.Color(116, 96, 153));
        lblIconoBajo.setBorder(null);

        lblStockBajo.setBackground(new java.awt.Color(253, 252, 253));
        lblStockBajo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblStockBajo.setForeground(new java.awt.Color(116, 96, 153));
        lblStockBajo.setBorder(null);

        lblTxtBajo.setBackground(new java.awt.Color(253, 252, 253));
        lblTxtBajo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTxtBajo.setForeground(new java.awt.Color(116, 96, 153));
        lblTxtBajo.setBorder(null);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblStockBajo, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTxtBajo, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIconoBajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblIconoBajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(lblTxtBajo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStockBajo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(253, 252, 253));
        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(228, 228, 228), 2, true));

        lblSubtotal.setForeground(new java.awt.Color(140, 135, 135));
        lblSubtotal.setText("BUSCAR PRODUCTO");

        txtBuscar.setForeground(new java.awt.Color(175, 175, 175));
        txtBuscar.setText("🔍 Nombre o código...");

        btnNuevo.setBackground(new java.awt.Color(55, 38, 88));
        btnNuevo.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevo.setText(" ➕   Nuevo producto");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSubtotal)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
                            .addComponent(txtBuscar))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSubtotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNuevo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(253, 252, 253));
        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(228, 228, 228), 2, true));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(55, 38, 88));
        jLabel18.setText("Inventario de productos");

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane3.setViewportView(tblProductos);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel18))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 674, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnEditar.setBackground(new java.awt.Color(239, 235, 245));
        btnEditar.setForeground(new java.awt.Color(55, 38, 88));
        btnEditar.setText("🖊️  ️  Editar");
        btnEditar.setBorder(null);

        btnAgregarStock.setBackground(new java.awt.Color(220, 242, 228));
        btnAgregarStock.setForeground(new java.awt.Color(20, 100, 50));
        btnAgregarStock.setText("➕  Agregar stock");
        btnAgregarStock.setBorder(null);

        btnEliminar.setBackground(new java.awt.Color(253, 203, 203));
        btnEliminar.setForeground(new java.awt.Color(143, 12, 12));
        btnEliminar.setText("🚮️   Eliminar");
        btnEliminar.setBorder(null);

        jLabel3.setText("imagen perroygato");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(28, 28, 28)
                                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)
                            .addComponent(btnAgregarStock, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAgregarStock, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 17, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(47, 47, 47))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarStock;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnVentas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField lblIconoBajo;
    private javax.swing.JTextField lblStockBajo;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTitulo6;
    private javax.swing.JTextField lblTotalProductos;
    private javax.swing.JTextField lblTxtBajo;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
