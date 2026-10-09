
package com.jennifer_y_madeline.proyecto;
import com.jennifer_y_madeline.proyecto.databases.Cliente;
import com.jennifer_y_madeline.proyecto.databases.ClienteDAO;
import com.jennifer_y_madeline.proyecto.databases.MetodoPago;
import com.jennifer_y_madeline.proyecto.databases.TipoCliente;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Magy
 */
public class PanelRegistroClientes extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PanelRegistroClientes.class.getName());

    /**
     * Creates new form PanelRegistroClientes
     */
    public PanelRegistroClientes() {
        initComponents();
        cargarCombos();
         configurarValidacionEnVivo();
         configurarTabla();   
        cargarClientes();
        configurarBotonGuardar();    // ✅ Nuevo
    configurarBotonCancelar();   
         txtNombre1.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(197, 197, 197), 2),
            javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 8)
        ));
        
        txtEdad.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(197, 197, 197), 2),
            javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 8)
        ));
        
        txtTipo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(197, 197, 197), 2),
            javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 8)
        ));
    }
    

  private final String[] TIPOS_VALIDOS = {
    "cliente regular", "cliente frecuente", "cliente coorporativo"
};
private List<TipoCliente> tipos = new ArrayList<>();

private void cargarCombos() {
    btnGuardar.setEnabled(false);
    new SwingWorker<Object[], Void>() {
        @Override
        protected Object[] doInBackground() {
            return new Object[]{dao.listarTipos(), dao.listarMetodos()};
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void done() {
            try {
                Object[] r = get();
                tipos = (List<TipoCliente>) r[0];
                for (MetodoPago m : (List<MetodoPago>) r[1]) cboMetodo.addItem(m);
                cboMetodo.setSelectedIndex(-1);
                btnGuardar.setEnabled(true);
            } catch (Exception ex) {
                mostrarError("No se pudieron cargar los catálogos", ex);
            }
        }

        
    }.execute();
}

/** Mensajes "Edad válida" / "Cliente válido" mientras se escribe. */
private void configurarValidacionEnVivo() {
    javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) { revisarCampos(); }
        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) { revisarCampos(); }
        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) { revisarCampos(); }
    };
    txtEdad.getDocument().addDocumentListener(dl);
    txtTipo.getDocument().addDocumentListener(dl);
}

private void revisarCampos() {
    java.awt.Color verde = new java.awt.Color(80, 200, 120);
    java.awt.Color rojo = new java.awt.Color(240, 110, 110);
    
    String edad = txtEdad.getText().trim();
    String tipo = txtTipo.getText().trim();
    
    // === IGNORAR TEXTOS DE AYUDA ===
    boolean esTextoAyudaEdad = edad.equals("🎂     Edad") || edad.equals("Edad");
    boolean esTextoAyudaTipo = tipo.equals("👥    Tipo de cliente") || tipo.equals("Tipo de cliente");
    
    // Si es texto de ayuda → NO mostrar nada
    if (esTextoAyudaEdad || edad.isEmpty()) {
        lblEdadMsg.setText("");
    } else if (edadValida(edad)) {
        lblEdadMsg.setForeground(verde);
        lblEdadMsg.setText("Edad válida");
    } else {
        lblEdadMsg.setForeground(rojo);
        lblEdadMsg.setText("Edad no válida (1 a 120)");
    }
    
    if (esTextoAyudaTipo || tipo.isEmpty()) {
        lblTipoMsg.setText("");
    } else if (tipoValido(tipo)) {
        lblTipoMsg.setForeground(verde);
        lblTipoMsg.setText("Cliente válido");
    } else {
        lblTipoMsg.setForeground(rojo);
        lblTipoMsg.setText("Escriba: Cliente regular, frecuente o corporativo");
    }
}

private boolean edadValida(String texto) {
    try {
        int n = Integer.parseInt(texto);
        return n >= 1 && n <= 120;
    } catch (NumberFormatException ex) {
        return false;
    }
}

private boolean tipoValido(String texto) {
    for (String t : TIPOS_VALIDOS) {
        if (t.equalsIgnoreCase(texto)) return true;
    }
    return false;
}

private TipoCliente buscarTipo(String texto) {
    for (TipoCliente t : tipos) {
        if (t.getNombre().equalsIgnoreCase(texto)) return t;
    }
    return null;
}

private void guardar() {
    String nombre = txtNombre1.getText().trim().replaceAll("\\s+", " ");
    String edadTxt = txtEdad.getText().trim();
    String tipoTxt = txtTipo.getText().trim().replaceAll("\\s+", " ");
    MetodoPago metodo = (MetodoPago) cboMetodo.getSelectedItem();
 


// Igual que en login: comprobar con y sin espacios
boolean nombreVacio = nombre.isEmpty() ||
    nombre.equals("Nombre:") ||
    nombre.equals("  Nombre:");

boolean edadVacia = edadTxt.isEmpty() ||
    edadTxt.equals("Edad:") ||
    edadTxt.equals("  Edad:");

boolean tipoVacio = tipoTxt.isEmpty() ||
    tipoTxt.equals("Tipo de cliente:") ||
    tipoTxt.equals("  Tipo de cliente:");

if (nombreVacio) {
    avisar("Ingrese el nombre del cliente.");
    txtNombre1.requestFocus();
    return;
}
if (edadVacia) {
    avisar("Ingrese la edad del cliente.");
    txtEdad.requestFocus();
    return;
}
if (tipoVacio) {
    avisar("Escriba el tipo de cliente.");
    txtTipo.requestFocus();
    return;
}
    
    if (nombre.isEmpty()) {
        avisar("Ingrese el nombre del cliente.");
        txtNombre1.requestFocus();
        return;
    }
    if (nombre.length() < 2) {
        avisar("El nombre debe tener al menos 2 caracteres.");
        txtNombre1.requestFocus();
        return;
    }
    if (nombre.length() > 100) {
        avisar("El nombre no puede pasar de 100 caracteres.");
        txtNombre1.requestFocus();
        return;
    }
    if (!nombre.matches("[\\p{L} .'-]+")) {
        avisar("El nombre solo puede contener letras y espacios.");
        txtNombre1.requestFocus();
        return;
    }

    if (edadTxt.isEmpty()) {
        avisar("Ingrese la edad del cliente.");
        txtEdad.requestFocus();
        return;
    }
    
    int edad = Integer.parseInt(edadTxt);

    if (tipoTxt.isEmpty()) {
        avisar("Escriba el tipo de cliente.");
        txtTipo.requestFocus();
        return;
    }
    
    TipoCliente tipo = buscarTipo(tipoTxt);
    if (tipo == null) {
        avisar("Ese tipo de cliente no existe en la base de datos.");
        txtTipo.requestFocus();
        return;
    }

    if (metodo == null) {
        avisar("Seleccione el método de pago.");
        cboMetodo.requestFocus();
        return;
    }

    try {
        dao.guardar(new Cliente(nombre, edad, tipo, metodo));
        JOptionPane.showMessageDialog(this, "Cliente registrado correctamente.",
                "Registro", JOptionPane.INFORMATION_MESSAGE);
        limpiar();
    } catch (Exception ex) {
        mostrarError("No se pudo guardar el cliente", ex);
    }
}

private void limpiar() {
    txtNombre1.setText("  Nombre:");
    txtNombre1.setForeground(new java.awt.Color(150, 150, 150));
    
    txtEdad.setText("  Edad:");
    txtEdad.setForeground(new java.awt.Color(150, 150, 150));
    
    txtTipo.setText("  Tipo de cliente:");
    txtTipo.setForeground(new java.awt.Color(150, 150, 150));
    
    cboMetodo.setSelectedIndex(-1);
    txtNombre1.requestFocus();
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jDialog1 = new javax.swing.JDialog();
        jDialog2 = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        lblPago = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        txtEdad = new javax.swing.JTextField();
        txtNombre1 = new javax.swing.JTextField();
        cboMetodo = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtTipo = new javax.swing.JTextField();
        lblEdadMsg = new javax.swing.JLabel();
        lblTipoMsg = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        actualizarNombre = new javax.swing.JButton();
        eliminarPorNombre = new javax.swing.JButton();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenuItem3.setText("jMenuItem3");

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Registro de clientes");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setToolTipText("Registro de clientes");

        lblPago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPago.setText("Método de pago:");

        lblTitulo.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTitulo.setText("Registro de Clientes");

        txtEdad.setForeground(new java.awt.Color(102, 102, 102));
        txtEdad.setText("🎂     Edad");
        txtEdad.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));

        txtNombre1.setForeground(new java.awt.Color(102, 102, 102));
        txtNombre1.setText("👤    Nombre");
        txtNombre1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));

        cboMetodo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));

        btnGuardar.setBackground(new java.awt.Color(225, 225, 225));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnCancelar.setBackground(new java.awt.Color(225, 225, 225));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        txtTipo.setForeground(new java.awt.Color(102, 102, 102));
        txtTipo.setText("👥    Tipo de cliente");
        txtTipo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(197, 197, 197), 2, true));
        txtTipo.addActionListener(this::txtTipoActionPerformed);

        lblEdadMsg.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblEdadMsg.setForeground(new java.awt.Color(204, 204, 204));

        lblTipoMsg.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblTipoMsg.setForeground(new java.awt.Color(204, 204, 204));

        jScrollPane1.setBackground(new java.awt.Color(0, 0, 0));

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblClientes);

        actualizarNombre.setBackground(new java.awt.Color(225, 225, 225));
        actualizarNombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        actualizarNombre.setText("Editar");
        actualizarNombre.addActionListener(this::actualizarNombreActionPerformed);

        eliminarPorNombre.setBackground(new java.awt.Color(225, 225, 225));
        eliminarPorNombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        eliminarPorNombre.setText("Eliminar");
        eliminarPorNombre.addActionListener(this::eliminarPorNombreActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(actualizarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(eliminarPorNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblTipoMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtTipo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblEdadMsg, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(lblPago)
                                    .addComponent(cboMetodo, 0, 294, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblTitulo)
                        .addGap(19, 19, 19)
                        .addComponent(txtNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(lblEdadMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTipoMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPago)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(actualizarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eliminarPorNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpiar();
          
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardar();
        cargarClientes();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTipoActionPerformed
        
    }//GEN-LAST:event_txtTipoActionPerformed

    private void actualizarNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizarNombreActionPerformed

    int fila = tblClientes.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla");
        return;
    }
    
    String nombre = modeloTabla.getValueAt(fila, 0).toString();
    
    // Buscar el cliente completo
    Cliente seleccionado = null;
    for (Cliente c : dao.listar()) {
        if (c.getNombre().equals(nombre)) {
            seleccionado = c;
            break;
        }
    }
    
    if (seleccionado == null) {
        JOptionPane.showMessageDialog(this, "Cliente no encontrado");
        return;
    }
    
    // Abrir ventana de edición
    VentanaEditarCliente ventana = new VentanaEditarCliente(seleccionado);
    ventana.setVisible(true);
    
    // Refrescar tabla al cerrar
    ventana.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            cargarClientes();
        }
    });

    }//GEN-LAST:event_actualizarNombreActionPerformed

    private void eliminarPorNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarPorNombreActionPerformed
           int fila = tblClientes.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla");
        return;
    }
    String nombre = modeloTabla.getValueAt(fila, 0).toString();
    
    int seguro = JOptionPane.showConfirmDialog(this,
        "¿Seguro que quieres eliminar a \"" + nombre + "\"?",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION);
    
    if (seguro == JOptionPane.YES_OPTION) {
        try {
           dao.eliminarPorNombre(nombre);
            JOptionPane.showMessageDialog(this, "Cliente eliminado ✅");
            cargarClientes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    }//GEN-LAST:event_eliminarPorNombreActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new PanelRegistroClientes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton actualizarNombre;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<MetodoPago> cboMetodo;
    private javax.swing.JButton eliminarPorNombre;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEdadMsg;
    private javax.swing.JLabel lblPago;
    private javax.swing.JLabel lblTipoMsg;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtEdad;
    private javax.swing.JTextField txtNombre1;
    private javax.swing.JTextField txtTipo;
    // End of variables declaration//GEN-END:variables
private final ClienteDAO dao = new ClienteDAO();

    private void avisar(String msg) {
    JOptionPane.showMessageDialog(this, msg, "Validación", JOptionPane.WARNING_MESSAGE);
}

private void mostrarError(String titulo, Exception ex) {
    Throwable causa = ex;
    while (causa.getCause() != null) causa = causa.getCause();
    JOptionPane.showMessageDialog(this, titulo + ":\n" + causa.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
}
    private DefaultTableModel modeloTabla;

private void configurarTabla() {
    modeloTabla = new DefaultTableModel(
            new Object[]{"Nombre", "Edad", "Tipo de cliente", "Método de pago"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;   // la tabla es solo para ver
        }
    };
    tblClientes.setModel(modeloTabla);
    tblClientes.getTableHeader().setReorderingAllowed(false);
}

private void cargarClientes() {
    new SwingWorker<List<Cliente>, Void>() {
        @Override
        protected List<Cliente> doInBackground() {
            return dao.listar();
        }

        @Override
        protected void done() {
            try {
                modeloTabla.setRowCount(0);   // vacía la tabla antes de llenarla
                for (Cliente c : get()) {
                    modeloTabla.addRow(new Object[]{
                        c.getNombre(),
                        c.getEdad(),
                        c.getTipoCliente().getNombre(),    // el nombre, no el número
                        c.getMetodoPago().getNombre()
                    });
                }
            } catch (Exception ex) {
                mostrarError("No se pudieron cargar los clientes", ex);
            }
        }
    }.execute();
}
private void configurarBotonGuardar() {
    final java.awt.Color normal = new java.awt.Color(225,225,225);        // Negro normal
    final java.awt.Color hover = new java.awt.Color(0,0,0);       // Gris oscuro al pasar
    final java.awt.Color presionado = new java.awt.Color(40, 40, 40);  // Más oscuro al hacer clic

    btnGuardar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    btnGuardar.setOpaque(true);
    btnGuardar.setContentAreaFilled(true);
    btnGuardar.setFocusPainted(false);
    btnGuardar.setBorder(javax.swing.BorderFactory.createLineBorder(
            new java.awt.Color(190, 190, 190), 1));
    
    btnGuardar.setBackground(normal);
    btnGuardar.setForeground(java.awt.Color.BLACK); // Letras blancas por defecto

    btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            btnGuardar.setBackground(hover);
            btnGuardar.setForeground(java.awt.Color.WHITE);
        }
        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            btnGuardar.setBackground(normal);
            btnGuardar.setForeground(java.awt.Color.BLACK);
        }
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            btnGuardar.setBackground(presionado);
            btnGuardar.setForeground(java.awt.Color.WHITE);
        }
        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            if (btnGuardar.getMousePosition() != null) {
                btnGuardar.setBackground(hover);
                btnGuardar.setForeground(java.awt.Color.WHITE);
            } else {
                btnGuardar.setBackground(normal);
                btnGuardar.setForeground(java.awt.Color.BLACK);
            }
        }
    });
}

private void configurarBotonCancelar() {
    final java.awt.Color normal = new java.awt.Color(225,225,225);       // Negro normal
    final java.awt.Color hover = new java.awt.Color(0,0,0);       // Gris oscuro al pasar
    final java.awt.Color presionado = new java.awt.Color(40, 40, 40);  // Más oscuro al hacer clic

    btnCancelar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    btnCancelar.setOpaque(true);
    btnCancelar.setContentAreaFilled(true);
    btnCancelar.setFocusPainted(false);
    btnCancelar.setBorder(javax.swing.BorderFactory.createLineBorder(
            new java.awt.Color(190, 190, 190), 1));
    
    btnCancelar.setBackground(normal);
    btnCancelar.setForeground(java.awt.Color.BLACK); // Letras blancas por defecto

    btnCancelar.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            btnCancelar.setBackground(hover);
            btnCancelar.setForeground(java.awt.Color.WHITE);
        }
        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            btnCancelar.setBackground(normal);
            btnCancelar.setForeground(java.awt.Color.BLACK);
        }
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            btnCancelar.setBackground(presionado);
            btnCancelar.setForeground(java.awt.Color.WHITE);
        }
        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            if (btnCancelar.getMousePosition() != null) {
                btnCancelar.setBackground(hover);
                btnCancelar.setForeground(java.awt.Color.WHITE);
            } else {
                btnCancelar.setBackground(normal);
                btnCancelar.setForeground(java.awt.Color.BLACK);
            }
        }
    });
                    // === NOMBRE — IGUAL A VENTANALOGIN ===
        txtNombre1.setText("  👤    Nombre");  // ← con espacio al principio
        txtNombre1.setForeground(new java.awt.Color(150, 150, 150));
        
        txtNombre1.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtNombre1.getText().equals("  👤    Nombre")) {
                    txtNombre1.setText("");
                    txtNombre1.setForeground(new java.awt.Color(50, 50, 50));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtNombre1.getText().isEmpty()) {
                    txtNombre1.setText("  👤    Nombre");
                    txtNombre1.setForeground(new java.awt.Color(150, 150, 150));
                }
            }
        });

        // === EDAD — IGUAL A VENTANALOGIN ===
        txtEdad.setText("  🎂     Edad");  // ← con espacio al principio
        txtEdad.setForeground(new java.awt.Color(150, 150, 150));
        
        txtEdad.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtEdad.getText().equals("  🎂     Edad")) {
                    txtEdad.setText("");
                    txtEdad.setForeground(new java.awt.Color(50, 50, 50));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtEdad.getText().isEmpty()) {
                    txtEdad.setText("  🎂     Edad");
                    txtEdad.setForeground(new java.awt.Color(150, 150, 150));
                }
            }
        });

        // === TIPO DE CLIENTE — IGUAL A VENTANALOGIN ===
        txtTipo.setText("  👥    Tipo de cliente");  // ← con espacio al principio
        txtTipo.setForeground(new java.awt.Color(150, 150, 150));
        
        txtTipo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtTipo.getText().equals("  👥    Tipo de cliente")) {
                    txtTipo.setText("");
                    txtTipo.setForeground(new java.awt.Color(50, 50, 50));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtTipo.getText().isEmpty()) {
                    txtTipo.setText("  👥    Tipo de cliente");
                    txtTipo.setForeground(new java.awt.Color(150, 150, 150));
                }
            }
        });
}

}
