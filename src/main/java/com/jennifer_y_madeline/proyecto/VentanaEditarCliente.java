package com.jennifer_y_madeline.proyecto;

import com.jennifer_y_madeline.proyecto.databases.Cliente;
import com.jennifer_y_madeline.proyecto.databases.ClienteDAO;
import com.jennifer_y_madeline.proyecto.databases.MetodoPago;
import com.jennifer_y_madeline.proyecto.databases.TipoCliente;
import static com.mysql.cj.conf.PropertyKey.logger;
import javax.swing.*;
import static org.hibernate.internal.CoreLogging.logger;

public class VentanaEditarCliente extends javax.swing.JFrame {
    
    private Cliente clienteAEditar;
    private ClienteDAO dao = new ClienteDAO();
    
    public VentanaEditarCliente(Cliente cliente) {
        initComponents();
        setLocationRelativeTo(null);
        
        this.clienteAEditar = cliente;
        
        // ✅ Llenar el combo con métodos de pago
        cargarMetodosPago();
        
        // ✅ Llenar campos con los datos del cliente
        txtEditarNombre.setText(cliente.getNombre());
        txtEditarEdad.setText(String.valueOf(cliente.getEdad()));
        txtEditarTipo.setText(cliente.getTipoCliente().getNombre());
        cboEditarPago.setSelectedItem(cliente.getMetodoPago());
    }

    private VentanaEditarCliente() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    // ✅ Cargar métodos de pago en el combo
    private void cargarMetodosPago() {
        cboEditarPago.removeAllItems();
        for (MetodoPago m : dao.listarMetodos()) {
            cboEditarPago.addItem(m);
        }
    }
    // === BOTÓN CANCELAR ===
    // === BOTÓN GUARDAR CAMBIOS ===
    
    // Buscar tipo de cliente por nombre
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
        cboEditarPago = new javax.swing.JComboBox<>();
        lblPago = new javax.swing.JLabel();
        btnGuardarCambios = new javax.swing.JButton();
        btnCancelarEditar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Editar Cliente");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        txtEditarNombre.setForeground(new java.awt.Color(102, 102, 102));
        txtEditarNombre.setText("👤    Nombre");
        txtEditarNombre.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));

        txtEditarEdad.setForeground(new java.awt.Color(102, 102, 102));
        txtEditarEdad.setText("🎂     Edad");
        txtEditarEdad.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));

        txtEditarTipo.setForeground(new java.awt.Color(102, 102, 102));
        txtEditarTipo.setText("👥    Tipo de cliente");
        txtEditarTipo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));
        txtEditarTipo.addActionListener(this::txtEditarTipoActionPerformed);

        cboEditarPago.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));

        lblPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPago.setText("Método de pago:");

        btnGuardarCambios.setBackground(new java.awt.Color(225, 225, 225));
        btnGuardarCambios.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnGuardarCambios.setText("Guardar");
        btnGuardarCambios.addActionListener(this::btnGuardarCambiosActionPerformed);

        btnCancelarEditar.setBackground(new java.awt.Color(225, 225, 225));
        btnCancelarEditar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCancelarEditar.setText("Cancelar");
        btnCancelarEditar.addActionListener(this::btnCancelarEditarActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPago)
                    .addComponent(cboEditarPago, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEditarTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtEditarEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtEditarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(81, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardarCambios, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelarEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(txtEditarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtEditarEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtEditarTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblPago)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cboEditarPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarCambios, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelarEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(54, Short.MAX_VALUE))
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
        Object seleccion = cboEditarPago.getSelectedItem();
        MetodoPago nuevoMetodo = (seleccion instanceof MetodoPago) ? (MetodoPago) seleccion : null;
         
        if (!nuevoNombre.matches("[\\p{L} .'-]+")) {
            JOptionPane.showMessageDialog(this, "El nombre solo puede contener letras y espacios.");
            txtEditarNombre.requestFocus(); // El cursor vuelve al campo nombre
            return; // Detiene todo si hay error
        }
        // Validar campos vacíos
        if (nuevoNombre.isEmpty() || edadTexto.isEmpty() || nuevoTipoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Llena todos los campos");
            return;
        }

        // Validar edad
        int nuevaEdad;
        try {
            nuevaEdad = Integer.parseInt(edadTexto);
            if (nuevaEdad < 1 || nuevaEdad > 120) {
                JOptionPane.showMessageDialog(this, "Edad debe ser entre 1 y 120");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Escribe un número válido en la edad");
            return;
        }

        // Validar tipo de cliente
        TipoCliente nuevoTipo = buscarTipo(nuevoTipoNombre);
        if (nuevoTipo == null) {
            JOptionPane.showMessageDialog(this,
                "Tipo inválido. Usa: Cliente Regular, Cliente Frecuente o Cliente Corporativo");
            return;
        }

        if (nuevoMetodo == null) {
            JOptionPane.showMessageDialog(this, "Selecciona método de pago");
            return;
        }

        try {
            // ✅ Actualizar en la base de datos
            dao.actualizarCompleto(clienteAEditar.getId(), nuevoNombre, nuevaEdad, nuevoTipo, nuevoMetodo);

            JOptionPane.showMessageDialog(this, "Cliente actualizado ✅");
            dispose(); // Cierra la ventana

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnGuardarCambiosActionPerformed

    private void txtEditarTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEditarTipoActionPerformed

    }//GEN-LAST:event_txtEditarTipoActionPerformed

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
            // No hacemos nada si falla el aspecto visual
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VentanaEditarCliente().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelarEditar;
    private javax.swing.JButton btnGuardarCambios;
    private javax.swing.JComboBox<MetodoPago> cboEditarPago;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblPago;
    private javax.swing.JTextField txtEditarEdad;
    private javax.swing.JTextField txtEditarNombre;
    private javax.swing.JTextField txtEditarTipo;
    // End of variables declaration//GEN-END:variables

    private void guardar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void cargarClientes() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void limpiar() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
