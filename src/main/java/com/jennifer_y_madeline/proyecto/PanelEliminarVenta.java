/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.jennifer_y_madeline.proyecto;

import com.jennifer_y_madeline.proyecto.databases.VentaDAO;
import com.jennifer_y_madeline.proyecto.databases.VentaResumen;
import java.awt.Color;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jpaol
 */
public class PanelEliminarVenta extends javax.swing.JPanel {

    private static final String PLACEHOLDER = "🔍 Busca por cliente, producto o número de venta...";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final VentaDAO dao = new VentaDAO();
    private List<VentaResumen> todas = new ArrayList<>();
    private DefaultTableModel modelo;

    /**
     * Creates new form PanelEliminarVenta
     */
    public PanelEliminarVenta() {
        initComponents();
        inicializarLogica();
    }

    private void inicializarLogica() {
        Imagenes.ponerLogo2(jLabel1, 70);
        Imagenes.ponerPerroYGato(jLabel3, 140);
        configurarMenu();
        configurarTabla();
        configurarBusqueda();
        jButton1.addActionListener(e -> eliminarVenta());
        recargar();
    }

    // ============ MINI MENU (hover y clic, igual que en la lista de productos) ============

    private void configurarMenu() {
        estilizarBotonMenu(btnProductos, true);    // "Ventas": pagina actual, queda aclarado
        estilizarBotonMenu(btnVentas, false);      // "Registrar Ventas"

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
        final Color gris = new Color(175, 175, 175);
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

    // ============ TABLA DE VENTAS (viene de MySQL) ============

    private String html(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String numero(VentaResumen v) {
        return String.format("%05d", v.getNumero());
    }

    /** Celda con texto (puede llevar HTML) y margen interno; cada columna dice qué texto muestra. */
    private abstract class CeldaVenta extends DefaultTableCellRenderer {
        abstract String texto(VentaResumen v);

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable t, Object valor,
                boolean sel, boolean foco, int fila, int col) {
            super.getTableCellRendererComponent(t, "", sel, false, fila, col);
            setText(texto((VentaResumen) valor));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(sel ? t.getSelectionForeground() : new Color(55, 38, 88));
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 6));
            setHorizontalAlignment(LEFT);
            return this;
        }
    }

    private void configurarTabla() {
        modelo = new DefaultTableModel(new Object[]{"No. Venta", "Fecha", "Cliente",
            "Productos", "Total", "Método de pago"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(modelo);
        jTable1.setRowHeight(54);
        jTable1.setShowVerticalLines(false);
        jTable1.setShowHorizontalLines(true);
        jTable1.setGridColor(new Color(236, 232, 244));
        jTable1.setSelectionBackground(new Color(236, 228, 248));
        jTable1.setSelectionForeground(new Color(55, 38, 88));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 38));
        jScrollPane1.getViewport().setBackground(Color.WHITE);

        // Encabezado lila como en el diseño
        DefaultTableCellRenderer encabezado = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foco, int fila, int col) {
                super.getTableCellRendererComponent(t, v, false, false, fila, col);
                setOpaque(true);
                setBackground(new Color(237, 233, 247));
                setForeground(new Color(55, 38, 88));
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 6));
                setHorizontalAlignment(LEFT);
                return this;
            }
        };
        int[] anchos = {80, 95, 130, 190, 85, 130};
        for (int i = 0; i < anchos.length; i++) {
            jTable1.getColumnModel().getColumn(i).setHeaderRenderer(encabezado);
            jTable1.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        jTable1.getColumnModel().getColumn(0).setCellRenderer(new CeldaVenta() {
            @Override String texto(VentaResumen v) {
                return numero(v);
            }
        });
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new CeldaVenta() {
            @Override String texto(VentaResumen v) {
                return "<html>" + v.getFecha().format(FORMATO_FECHA)
                        + "<br><font size='2' color='#8a82a0'>" + v.getFecha().format(FORMATO_HORA) + "</font></html>";
            }
        });
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new CeldaVenta() {
            @Override String texto(VentaResumen v) {
                return html(v.getCliente().getNombre());
            }
        });
        jTable1.getColumnModel().getColumn(3).setCellRenderer(new CeldaVenta() {
            @Override String texto(VentaResumen v) {
                List<String> n = v.getNombresProductos();
                StringBuilder sb = new StringBuilder("<html>").append(html(n.get(0)));
                if (n.size() >= 2) {
                    sb.append("<br>").append(html(n.get(1)));
                }
                if (n.size() > 2) {
                    sb.append(" <font size='2' color='#5b6bd6'>+").append(n.size() - 2).append(" más</font>");
                }
                return sb.append("</html>").toString();
            }
        });
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new CeldaVenta() {
            @Override String texto(VentaResumen v) {
                return "<html><b>Q " + v.getTotal().toPlainString() + "</b></html>";
            }
        });
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new CeldaVenta() {
            @Override String texto(VentaResumen v) {
                String nombre = v.getMetodoPago().getNombre();
                return html(nombre);
            }
        });
    }

    // ============ DATOS ============

    private void recargar() {
        final java.awt.Component padre = this;
        new SwingWorker<List<VentaResumen>, Void>() {
            @Override
            protected List<VentaResumen> doInBackground() {
                return dao.listarResumenes();
            }

            @Override
            protected void done() {
                try {
                    todas = get();
                    filtrar();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(padre, "Error al cargar las ventas: " + ex);
                }
            }
        }.execute();
    }

    private void filtrar() {
        String q = textoBusqueda().toLowerCase();
        modelo.setRowCount(0);
        for (VentaResumen v : todas) {
            if (q.isEmpty() || coincide(v, q)) {
                modelo.addRow(new Object[]{v, v, v, v, v, v});
            }
        }
    }

    private boolean coincide(VentaResumen v, String q) {
        if (numero(v).contains(q) || String.valueOf(v.getNumero()).equals(q)) return true;
        if (v.getCliente().getNombre().toLowerCase().contains(q)) return true;
        for (String producto : v.getNombresProductos()) {
            if (producto.toLowerCase().contains(q)) return true;
        }
        return false;
    }

    // ============ ELIMINAR (usa el panel de confirmación) ============

    private void eliminarVenta() {
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta de la tabla.");
            return;
        }
        VentaResumen v = (VentaResumen) modelo.getValueAt(jTable1.convertRowIndexToModel(fila), 0);

        // "Collar para perro +1 más  ·  Ana Torres  ·  Q 75.00"
        List<String> nombres = v.getNombresProductos();
        String producto = nombres.get(0) + (nombres.size() > 1 ? " +" + (nombres.size() - 1) + " más" : "");
        String detalle = producto + "  ·  " + v.getCliente().getNombre() + "  ·  Q " + v.getTotal().toPlainString();

        if (!PanelMensajeEliminar.confirmar(this, "la venta", detalle)) {
            return;
        }
        try {
            // true = las unidades de la venta vuelven al inventario de los productos
            dao.eliminarVenta(v.getIdsLineas(), true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo eliminar la venta: " + ex.getMessage());
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
        txtBuscar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 251, 250));

        jPanel2.setBackground(new java.awt.Color(55, 38, 88));

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("logo");

        jLabel17.setForeground(new java.awt.Color(183, 169, 209));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("──────────────────────────────────────────");

        btnProductos.setText("  🛍   Ventas");
        btnProductos.setActionCommand("  🛍   Ventas");
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
        jLabel2.setText("Ventas");

        jLabel14.setForeground(new java.awt.Color(175, 175, 175));
        jLabel14.setText("Aqui puedes ver el historial de ventas realizadas.");

        jPanel3.setBackground(new java.awt.Color(253, 252, 253));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(228, 228, 228), 2, true));
        jPanel3.setForeground(new java.awt.Color(253, 252, 253));

        txtBuscar.setForeground(new java.awt.Color(175, 175, 175));
        txtBuscar.setText("🔍 Busca por cliente, producto o número de venta...");
        txtBuscar.addActionListener(this::txtBuscarActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 606, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(253, 203, 203));
        jButton1.setForeground(new java.awt.Color(143, 12, 12));
        jButton1.setText("🚮️   Eliminar");
        jButton1.setToolTipText("");
        jButton1.setBorder(null);

        jLabel3.setText("imagen perroygato");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel3)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 633, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1265, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel3)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnProductos;
    private javax.swing.JButton btnVentas;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
