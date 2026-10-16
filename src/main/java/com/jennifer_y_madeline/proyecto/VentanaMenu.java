
package com.jennifer_y_madeline.proyecto;
/**
 *
 * @author Magy
 */
import javax.swing.*;
public class VentanaMenu extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaMenu.class.getName());
    private javax.swing.JButton botonActivo;
    private PanelInicio panelInicio;
    private PanelRegistroClientes panelClientes;
    public int opcion = 0;

  
    private static final java.awt.Dimension AREA_IDEAL = new java.awt.Dimension(900, 670);
    private java.awt.Dimension areaPaneles = new java.awt.Dimension(AREA_IDEAL);
    private static final java.awt.Color FONDO_PANEL = new java.awt.Color(255, 251, 250);
    private int anchoMenu;

public VentanaMenu() {
        initComponents();
        Imagenes.ponerLogo2(jLabel1, 110);
        anchoMenu = jPanel2.getPreferredSize().width + 10;
        ajustarAreaALaPantalla();
        contenedor.setPreferredSize(areaPaneles);
        contenedor.setBackground(FONDO_PANEL);
        setResizable(false);              
        pack();
        setLocationRelativeTo(null);
        contenedor.setLayout(new java.awt.BorderLayout());
        
     
        for (javax.swing.JButton b : new javax.swing.JButton[]{
                btnCambiarUsuario, btnClientes, btnVentas, btnConsultas,
                btnEstadisticas, btnCierre, btnSalir}) {
            configurarBotonMenu(b);
        }
         marcarComoActivo(btnCambiarUsuario);
  
        panelInicio = new PanelInicio();
        mostrarPanel(panelInicio);
    }

   
    private void ajustarAreaALaPantalla() {
        java.awt.Rectangle util = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int ancho = util.width - anchoMenu - 30;    
        int alto = util.height - 60;                  
        areaPaneles = new java.awt.Dimension(
                Math.max(500, Math.min(AREA_IDEAL.width, ancho)),
                Math.max(400, Math.min(AREA_IDEAL.height, alto)));
    }

    private void mostrarPanel(javax.swing.JPanel panel) {
        boolean ocultarMenu = (panel instanceof PanelListaProductos) || (panel instanceof PanelEliminarVenta);
        boolean cambioMenu = (jPanel2.isVisible() == ocultarMenu);
        jPanel2.setVisible(!ocultarMenu);

        contenedor.setPreferredSize(ocultarMenu
                ? new java.awt.Dimension(areaPaneles.width + anchoMenu, areaPaneles.height)
                : areaPaneles);
        if (panel instanceof PanelRegistroVentas || panel instanceof PanelEliminarVenta
                || panel.getClass() == javax.swing.JPanel.class) {
            panel.setBackground(FONDO_PANEL);
        }

        contenedor.removeAll();
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(panel,
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO_PANEL);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);
        contenedor.add(scroll, java.awt.BorderLayout.CENTER);
        contenedor.revalidate();
        contenedor.repaint();

        if (cambioMenu) {     
            pack();
            setLocationRelativeTo(null);
        }
    }

    public void irAInicio() {
        marcarComoActivo(btnCambiarUsuario);
        panelInicio = new PanelInicio(); 
        mostrarPanel(panelInicio);
    }

    public void irAVentas() {
        marcarComoActivo(btnVentas);
        mostrarPanel(new PanelRegistroVentas());
    }

    public void irAProductos() {
        marcarComoActivo(btnVentas);
        mostrarPanel(new PanelListaProductos());
    }

    public void irAEliminarVenta() {
        marcarComoActivo(btnVentas);
        mostrarPanel(new PanelEliminarVenta());
    }
        private static final java.awt.Color MENU_FONDO = new java.awt.Color(55, 38, 88);
    private static final java.awt.Color MENU_HOVER = new java.awt.Color(116, 96, 153);
    private static final java.awt.Color MENU_PRESIONADO = new java.awt.Color(90, 72, 128);

private void configurarBotonMenu(final javax.swing.JButton btn) {
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        pintarBotonMenu(btn, MENU_FONDO);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                pintarBotonMenu(btn, MENU_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn == botonActivo) {
                    pintarBotonMenu(btn, MENU_HOVER);
                } else {
                    pintarBotonMenu(btn, MENU_FONDO);
                }
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                pintarBotonMenu(btn, MENU_PRESIONADO);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (btn == botonActivo || btn.getMousePosition() != null) {
                    pintarBotonMenu(btn, MENU_HOVER);
                } else {
                    pintarBotonMenu(btn, MENU_FONDO);
                }
            }
        });
    }

   private void pintarBotonMenu(javax.swing.JButton btn, java.awt.Color color) {
        btn.setBackground(color);
        btn.setForeground(java.awt.Color.WHITE);
        btn.setBorder(new javax.swing.border.LineBorder(color, 4, true));
    }
 
    private void marcarComoActivo(javax.swing.JButton btn) {
        if (botonActivo == btn) return;
        if (botonActivo != null) {
            pintarBotonMenu(botonActivo, MENU_FONDO);
        }
        botonActivo = btn;
        pintarBotonMenu(botonActivo, MENU_HOVER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contenedor = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnCambiarUsuario = new javax.swing.JButton();
        btnClientes = new javax.swing.JButton();
        btnVentas = new javax.swing.JButton();
        btnConsultas = new javax.swing.JButton();
        btnEstadisticas = new javax.swing.JButton();
        btnCierre = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Estrella Peluda");

        contenedor.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout contenedorLayout = new javax.swing.GroupLayout(contenedor);
        contenedor.setLayout(contenedorLayout);
        contenedorLayout.setHorizontalGroup(
            contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 799, Short.MAX_VALUE)
        );
        contenedorLayout.setVerticalGroup(
            contenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(55, 38, 88));

        btnCambiarUsuario.setBackground(new java.awt.Color(116, 96, 153));
        btnCambiarUsuario.setForeground(new java.awt.Color(255, 255, 255));
        btnCambiarUsuario.setText("  🏠  Inicio");
        btnCambiarUsuario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 4, true));
        btnCambiarUsuario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCambiarUsuario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCambiarUsuario.setIconTextGap(10);
        btnCambiarUsuario.addActionListener(this::btnCambiarUsuarioActionPerformed);

        btnClientes.setBackground(new java.awt.Color(116, 96, 153));
        btnClientes.setForeground(new java.awt.Color(255, 255, 255));
        btnClientes.setText("  👤  Registrar Clientes");
        btnClientes.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 4, true));
        btnClientes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnClientes.setIconTextGap(10);
        btnClientes.addActionListener(this::btnClientesActionPerformed);

        btnVentas.setBackground(new java.awt.Color(116, 96, 153));
        btnVentas.setForeground(new java.awt.Color(255, 255, 255));
        btnVentas.setText("  💵  Registrar Ventas");
        btnVentas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 4, true));
        btnVentas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVentas.setIconTextGap(10);
        btnVentas.addActionListener(this::btnVentasActionPerformed);

        btnConsultas.setBackground(new java.awt.Color(116, 96, 153));
        btnConsultas.setForeground(new java.awt.Color(255, 255, 255));
        btnConsultas.setText("  🔍  Consultar Ventas");
        btnConsultas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 4, true));
        btnConsultas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnConsultas.setIconTextGap(10);
        btnConsultas.addActionListener(this::btnConsultasActionPerformed);

        btnEstadisticas.setBackground(new java.awt.Color(116, 96, 153));
        btnEstadisticas.setForeground(new java.awt.Color(255, 255, 255));
        btnEstadisticas.setText("  📊  Consultar Estadísticas");
        btnEstadisticas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 4, true));
        btnEstadisticas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEstadisticas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEstadisticas.setIconTextGap(10);
        btnEstadisticas.addActionListener(this::btnEstadisticasActionPerformed);

        btnCierre.setBackground(new java.awt.Color(116, 96, 153));
        btnCierre.setForeground(new java.awt.Color(255, 255, 255));
        btnCierre.setText("  🔒  Cerrar Caja");
        btnCierre.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 4, true));
        btnCierre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCierre.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCierre.addActionListener(this::btnCierreActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("logo");

        btnSalir.setBackground(new java.awt.Color(116, 96, 153));
        btnSalir.setForeground(new java.awt.Color(255, 255, 255));
        btnSalir.setText("  ↩️  Salir ");
        btnSalir.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 4, true));
        btnSalir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSalir.setMaximumSize(new java.awt.Dimension(72, 24));
        btnSalir.setMinimumSize(new java.awt.Dimension(72, 24));
        btnSalir.setPreferredSize(new java.awt.Dimension(72, 24));
        btnSalir.addActionListener(this::btnSalirActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCierre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConsultas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVentas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClientes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCambiarUsuario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEstadisticas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(20, 20, 20)
                .addComponent(btnCambiarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConsultas, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEstadisticas, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCierre, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contenedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(contenedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEstadisticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstadisticasActionPerformed
         marcarComoActivo(btnEstadisticas);
        mostrarPanel(new PanelEnDesarrollo("Estadisticas"));
    }//GEN-LAST:event_btnEstadisticasActionPerformed

    private void btnCierreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCierreActionPerformed
        marcarComoActivo(btnCierre);
        mostrarPanel(new PanelEnDesarrollo("Cierre de caja"));
    }//GEN-LAST:event_btnCierreActionPerformed

    private void btnConsultasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultasActionPerformed
      marcarComoActivo(btnConsultas);
        mostrarPanel(new PanelEnDesarrollo("Consultar Ventas"));
    }//GEN-LAST:event_btnConsultasActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        opcion=7;
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnCambiarUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCambiarUsuarioActionPerformed
         marcarComoActivo(btnCambiarUsuario);
        
        panelInicio = new PanelInicio();   
        mostrarPanel(panelInicio);
        if (panelClientes != null) {
            panelInicio.actualizarClientes(panelClientes.getTotalClientes());
        }
    }//GEN-LAST:event_btnCambiarUsuarioActionPerformed

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClientesActionPerformed
            marcarComoActivo(btnClientes);
        panelClientes = new PanelRegistroClientes();
        mostrarPanel((javax.swing.JPanel) panelClientes.getContentPane());


    }//GEN-LAST:event_btnClientesActionPerformed

    private void btnVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasActionPerformed
          marcarComoActivo(btnVentas);
        mostrarPanel(new PanelRegistroVentas());
    }//GEN-LAST:event_btnVentasActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VentanaMenu().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCambiarUsuario;
    private javax.swing.JButton btnCierre;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnConsultas;
    private javax.swing.JButton btnEstadisticas;
    private javax.swing.JButton btnSalir;
    private javax.swing.JButton btnVentas;
    private javax.swing.JPanel contenedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    }


