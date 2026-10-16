package com.jennifer_y_madeline.proyecto;
import com.jennifer_y_madeline.proyecto.databases.Cliente;
import com.jennifer_y_madeline.proyecto.databases.ClienteDAO;
import com.jennifer_y_madeline.proyecto.databases.TipoCliente;
import javax.swing.*;

public class VentanaEditarCliente extends javax.swing.JFrame {
    
    private Cliente clienteAEditar;
    private ClienteDAO dao = new ClienteDAO();
    
    public VentanaEditarCliente(Cliente cliente) {
   
        initComponents();
        jLabel11.setVisible(false);
        setLocationRelativeTo(null);
        this.clienteAEditar = cliente;
        txtEditarNombre.setText(cliente.getNombre());
        txtEditarEdad.setText(cliente.getEdad() == null ? "" : String.valueOf(cliente.getEdad()));
        txtEditarTipo.setText(cliente.getTipoCliente().getNombre());
    }

    private void limpiar() {
    dispose();
}
    
  
    private TipoCliente buscarTipo(String nombre) {
        for (TipoCliente t : dao.listarTipos()) {
            if (t.getNombre().equalsIgnoreCase(nombre)) {
                return t;
            }
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtEditarNombre = new javax.swing.JTextField();
        txtEditarEdad = new javax.swing.JTextField();
        txtEditarTipo = new javax.swing.JTextField();
        btnGuardarCambios = new javax.swing.JButton();
        btnCancelarEditar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Estrella Peluda - Editar Cliente");

        jPanel1.setBackground(new java.awt.Color(248, 246, 252));

        txtEditarNombre.setForeground(new java.awt.Color(102, 102, 102));
        txtEditarNombre.setText("👤    Nombre");
        txtEditarNombre.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));

        txtEditarEdad.setForeground(new java.awt.Color(102, 102, 102));
        txtEditarEdad.setText("🎂     Edad");
        txtEditarEdad.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        txtEditarEdad.addActionListener(this::txtEditarEdadActionPerformed);

        txtEditarTipo.setForeground(new java.awt.Color(102, 102, 102));
        txtEditarTipo.setText("👥    Tipo de cliente");
        txtEditarTipo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        txtEditarTipo.addActionListener(this::txtEditarTipoActionPerformed);

        btnGuardarCambios.setBackground(new java.awt.Color(79, 57, 122));
        btnGuardarCambios.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardarCambios.setText(" 💾  Guardar");
        btnGuardarCambios.addActionListener(this::btnGuardarCambiosActionPerformed);

        btnCancelarEditar.setBackground(new java.awt.Color(253, 252, 253));
        btnCancelarEditar.setText(" ✖  Cancelar");
        btnCancelarEditar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        btnCancelarEditar.addActionListener(this::btnCancelarEditarActionPerformed);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(116, 96, 153));
        jLabel1.setText("Datos del cliente");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Nombre");

        jLabel13.setForeground(new java.awt.Color(252, 0, 0));
        jLabel13.setText("*");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Edad");

        jLabel11.setForeground(new java.awt.Color(252, 0, 0));
        jLabel11.setText("*");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Tipo de cliente");

        jLabel9.setForeground(new java.awt.Color(252, 0, 0));
        jLabel9.setText("*");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardarCambios, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelarEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtEditarTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEditarEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(54, 54, 54)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(25, 25, 25)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel10)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel12)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtEditarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addGap(4, 4, 4)
                .addComponent(txtEditarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEditarEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEditarTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarCambios, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarEditarActionPerformed
        limpiar();
    }//GEN-LAST:event_btnCancelarEditarActionPerformed

    private void btnGuardarCambiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarCambiosActionPerformed
        String nuevoNombre = txtEditarNombre.getText().trim();
        String edadTexto = txtEditarEdad.getText().trim();
        String nuevoTipoNombre = txtEditarTipo.getText().trim();
         
            if (nuevoNombre.isEmpty() || nuevoTipoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Llena todos los campos");
            return;
         }

        if (nuevoNombre.length() > 100) {
            JOptionPane.showMessageDialog(this, "El nombre no puede pasar de 100 caracteres.");
            return;
        }

        if (!nuevoNombre.matches("[\\p{L} .'-]+")) {
            JOptionPane.showMessageDialog(this, "El nombre solo puede contener letras y espacios.");
            txtEditarNombre.requestFocus(); 
            return;
        }

        
        Integer nuevaEdad;
        try {
            nuevaEdad = edadTexto.isEmpty() ? null : Integer.valueOf(edadTexto);
            if (nuevaEdad != null && (nuevaEdad < 0 || nuevaEdad > 255)) {
                JOptionPane.showMessageDialog(this, "Edad debe ser entre 0 y 255");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Escribe un número válido en la edad");
            return;
        }

        
        TipoCliente nuevoTipo = buscarTipo(nuevoTipoNombre);
        if (nuevoTipo == null) {
            JOptionPane.showMessageDialog(this,
                "Escriba un tipo de cliente del cat\u00e1logo.");
            return;
        }

        try {
           
            dao.actualizarCompleto(clienteAEditar.getId(), nuevoNombre, nuevaEdad, nuevoTipo);

            JOptionPane.showMessageDialog(this, "Cliente actualizado ✅");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnGuardarCambiosActionPerformed

    private void txtEditarTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEditarTipoActionPerformed

    }//GEN-LAST:event_txtEditarTipoActionPerformed

    private void txtEditarEdadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEditarEdadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEditarEdadActionPerformed

   
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
            // No hacemos nada si falla el aspecto visual
        }
        //</editor-fold>

        /* Create and display the form */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarEditar;
    private javax.swing.JButton btnGuardarCambios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtEditarEdad;
    private javax.swing.JTextField txtEditarNombre;
    private javax.swing.JTextField txtEditarTipo;
    // End of variables declaration//GEN-END:variables
}
