
package com.jennifer_y_madeline.proyecto;

/**
 *
 * @author jpaol
 */
public class VentanaLogin extends javax.swing.JFrame {
   public boolean entro = false;
    public String usuarioIngresado = "";
    public String claveIngresada = "";
    public void mostrarMensaje(String texto) {
        lblIntentos.setText(texto);
    }
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaLogin.class.getName());

    /**
     * Creates new form VentanaLogin
     */
        public VentanaLogin() {
        initComponents();
        configurarBotonIngresar();
        final java.awt.Component[] orden = { txtUsuario, txtClave, btnIngresar };

setFocusTraversalPolicy(new java.awt.FocusTraversalPolicy() {
    @Override
    public java.awt.Component getComponentAfter(java.awt.Container c, java.awt.Component comp) {
        for (int i = 0; i < orden.length; i++) {
            if (orden[i] == comp) return orden[(i + 1) % orden.length];
        }
        return orden[0];
    }

    @Override
    public java.awt.Component getComponentBefore(java.awt.Container c, java.awt.Component comp) {
        for (int i = 0; i < orden.length; i++) {
            if (orden[i] == comp) return orden[(i - 1 + orden.length) % orden.length];
        }
        return orden[0];
    }

    @Override
    public java.awt.Component getFirstComponent(java.awt.Container c) { return orden[0]; }

    @Override
    public java.awt.Component getLastComponent(java.awt.Container c) { return orden[orden.length - 1]; }

   @Override
public java.awt.Component getDefaultComponent(java.awt.Container c) {
    return getContentPane();   // antes: orden[0]
}
});
getContentPane().setFocusable(true);

addWindowListener(new java.awt.event.WindowAdapter() {
    @Override
    public void windowOpened(java.awt.event.WindowEvent e) {
        getContentPane().requestFocusInWindow();   // el foco empieza en el fondo, no en un campo
    }
});
  

        txtUsuario.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(190, 190, 190), 2),
        javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 8)));
        txtClave.setBorder(javax.swing.BorderFactory.createCompoundBorder(
        javax.swing.BorderFactory.createLineBorder(new java.awt.Color(190, 190, 190), 2),
        javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 8)));
        
        btnOjo.setOpaque(false);
        btnOjo.setContentAreaFilled(false);
        btnOjo.setBorderPainted(false);
        btnOjo.setFocusPainted(false);
        btnOjo.setBorder(null);
       
       
 
        
        btnOjo.addActionListener(e -> {
            if (btnOjo.isSelected()) {
                txtClave.setEchoChar((char)0);
                btnOjo.setText("👁");
            } else {
                txtClave.setEchoChar('•');
                btnOjo.setText("️⦸");
            }
        });
        getContentPane().revalidate();
    getContentPane().repaint();
    
        
            
        txtUsuario.setText("  👤    Nombre de Usuario");
        txtUsuario.setForeground(new java.awt.Color(150, 150, 150));
        
        txtUsuario.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtUsuario.getText().equals("  👤    Nombre de Usuario")) {
                    txtUsuario.setText("");
                    txtUsuario.setForeground(new java.awt.Color(50, 50, 50));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtUsuario.getText().isEmpty()) {
                    txtUsuario.setText("  👤    Nombre de Usuario");
                    txtUsuario.setForeground(new java.awt.Color(150, 150, 150));
                }
            }
        });

              
        txtClave.setText("  🔒    Contraseña");
        txtClave.setForeground(new java.awt.Color(150, 150, 150));
        txtClave.setEchoChar((char) 0); 
        
        txtClave.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                String actual = new String(txtClave.getPassword());
                if (actual.equals("  🔒    Contraseña")) {
                    txtClave.setText("");
                    txtClave.setForeground(new java.awt.Color(50, 50, 50));
                    txtClave.setEchoChar('•'); 
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                String actual = new String(txtClave.getPassword());
                if (actual.isEmpty()) {
                    txtClave.setText("  🔒    Contraseña");
                    txtClave.setForeground(new java.awt.Color(150, 150, 150));
                    txtClave.setEchoChar((char) 0); 
                }
            }
        });
                
        setFocusable(true);
        
    }
       public void setIntentosRestantes(int cantidad) {
        if (cantidad > 0) {
            lblIntentos.setText("Intentos restantes: " + cantidad);
        } else {
            lblIntentos.setText("");
        }
    }
     public void setMensaje(String texto) {
        lblMensaje.setText(texto);
    }
  
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        txtClave = new javax.swing.JPasswordField();
        btnIngresar = new javax.swing.JButton();
        btnOjo = new javax.swing.JToggleButton();
        lblIntentos = new javax.swing.JLabel();
        lblMensaje = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Inicio de Sesión");
        setLocationByPlatform(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));

        jLabel3.setFont(new java.awt.Font("Franklin Gothic Demi Cond", 1, 24)); // NOI18N
        jLabel3.setText("Registro de Usuario");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        txtUsuario.setBackground(new java.awt.Color(236, 236, 236));
        txtUsuario.setForeground(new java.awt.Color(102, 102, 102));
        txtUsuario.setText("👤    Nombre de Usuario");
        txtUsuario.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));
        txtUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtUsuario.setMargin(new java.awt.Insets(0, 20, 0, 0));
        txtUsuario.setMinimumSize(new java.awt.Dimension(129, 19));
        txtUsuario.addActionListener(this::txtUsuarioActionPerformed);

        txtClave.setBackground(new java.awt.Color(231, 231, 231));
        txtClave.setText("🔒Contraseña");
        txtClave.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));

        btnIngresar.setBackground(new java.awt.Color(225, 225, 225));
        btnIngresar.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        btnIngresar.setText("INGRESAR");
        btnIngresar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnIngresar.addActionListener(this::btnIngresarActionPerformed);

        btnOjo.setBackground(new java.awt.Color(0, 0, 0));
        btnOjo.setText("⦸");
        btnOjo.setBorder(null);
        btnOjo.setBorderPainted(false);
        btnOjo.setContentAreaFilled(false);
        btnOjo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOjo.setFocusPainted(false);
        btnOjo.setMargin(new java.awt.Insets(0, 35, 0, 5));
        btnOjo.addActionListener(this::btnOjoActionPerformed);

        lblIntentos.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lblIntentos.setText("Intentos restantes: 3");

        lblMensaje.setFont(new java.awt.Font("Segoe UI", 2, 16)); // NOI18N
        lblMensaje.setForeground(new java.awt.Color(153, 153, 153));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblIntentos, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblMensaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtClave, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnOjo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(btnIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel3)
                .addGap(44, 44, 44)
                .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClave, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                    .addComponent(btnOjo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblIntentos, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(btnIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIngresarActionPerformed
                                         
        String usu = txtUsuario.getText().trim();
        String cla = new String(txtClave.getPassword());
        
       
        boolean usuarioVacio = usu.isEmpty() ||
            usu.equals("  👤    Nombre de Usuario") ||
            usu.equals("👤    Nombre de Usuario");
        
        boolean claveVacia = cla.isEmpty() ||
            cla.equals("  🔒    Contraseña") ||
            cla.equals("🔒Contraseña");
        
        if (usuarioVacio || claveVacia) {
            setMensaje("Por favor llene todos los campos");
            entro = false;
            return;
        }
        
       
        usuarioIngresado = usu;
        claveIngresada = cla;
        entro = true;
        dispose();
    
    
    }//GEN-LAST:event_btnIngresarActionPerformed

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void btnOjoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOjoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnOjoActionPerformed

   

 
    
    
    /**
     * @param args the command line arguments
     */
    
      
  /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
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
        java.awt.EventQueue.invokeLater(() -> new VentanaLogin().setVisible(true));
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIngresar;
    private javax.swing.JToggleButton btnOjo;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblIntentos;
    private javax.swing.JLabel lblMensaje;
    private javax.swing.JPasswordField txtClave;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables

private void configurarBotonIngresar() {
    final java.awt.Color normal = new java.awt.Color(225, 225, 225);
    final java.awt.Color hover = new java.awt.Color(0, 0, 0);
    final java.awt.Color presionado = new java.awt.Color(40, 40, 40);

    // Estilo básico: respeta siempre el color de fondo
    btnIngresar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    btnIngresar.setOpaque(true);
    btnIngresar.setContentAreaFilled(true);
    btnIngresar.setFocusPainted(false);
    btnIngresar.setBorder(javax.swing.BorderFactory.createLineBorder(
            new java.awt.Color(190, 190, 190), 1));

    // Estado inicial: gris con letras negras
    btnIngresar.setBackground(normal);
    btnIngresar.setForeground(java.awt.Color.BLACK);

    btnIngresar.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            btnIngresar.setBackground(hover);
            btnIngresar.setForeground(java.awt.Color.WHITE);
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            btnIngresar.setBackground(normal);
            btnIngresar.setForeground(java.awt.Color.BLACK);
        }

        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            btnIngresar.setBackground(presionado);
            btnIngresar.setForeground(java.awt.Color.WHITE);
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            if (btnIngresar.getMousePosition() != null) {
                btnIngresar.setBackground(hover);
                btnIngresar.setForeground(java.awt.Color.WHITE);
            } else {
                btnIngresar.setBackground(normal);
                btnIngresar.setForeground(java.awt.Color.BLACK);
            }
        }
    });
}
}

