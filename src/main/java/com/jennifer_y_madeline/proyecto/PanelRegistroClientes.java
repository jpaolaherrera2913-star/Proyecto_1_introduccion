package com.jennifer_y_madeline.proyecto;
import com.jennifer_y_madeline.proyecto.databases.Cliente;
import com.jennifer_y_madeline.proyecto.databases.ClienteDAO;
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

    public PanelRegistroClientes() {
        initComponents();
        jLabel11.setVisible(false);
        cargarCombos();
         configurarValidacionEnVivo();
         configurarTabla();   
        cargarClientes();
        configurarBotonGuardar();    
    configurarBotonCancelar();   
         txtNombre1.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(116,96,153), 2),
            javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 8)
        ));
        
        txtEdad.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(116, 96, 153), 2),
            javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 8)
        ));
        
        txtTipo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(116, 96, 153), 2),
            javax.swing.BorderFactory.createEmptyBorder(0, 20, 0, 8)
        ));
    }
    

private List<TipoCliente> tipos = new ArrayList<>();

private void cargarCombos() {
    btnGuardar.setEnabled(false);
    new SwingWorker<Object[], Void>() {
        @Override
        protected Object[] doInBackground() {
            return new Object[]{dao.listarTipos()};
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void done() {
            try {
                Object[] r = get();
                tipos = (List<TipoCliente>) r[0];
                btnGuardar.setEnabled(true);
            } catch (Exception ex) {
                mostrarError("No se pudieron cargar los catálogos", ex);
            }
        }

        
    }.execute();
}

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
    
    boolean esTextoAyudaEdad = edad.equals("🎂     Ej. 25") || edad.equals("Edad");
    boolean esTextoAyudaTipo = tipo.equals("👥    Ej. Cliente Frecuente") || tipo.equals("Tipo de cliente");
    
    if (esTextoAyudaEdad || edad.isEmpty()) {
        lblEdadMsg.setText("");
    } else if (edadValida(edad)) {
        lblEdadMsg.setForeground(verde);
        lblEdadMsg.setText("Edad válida");
    } else {
        lblEdadMsg.setForeground(rojo);
        lblEdadMsg.setText("Edad no válida (0 a 255)");
    }
    
    if (esTextoAyudaTipo || tipo.isEmpty()) {
        lblTipoMsg.setText("");
    } else if (tipoValido(tipo)) {
        lblTipoMsg.setForeground(verde);
        lblTipoMsg.setText("Cliente válido");
    } else {
        lblTipoMsg.setForeground(rojo);
        lblTipoMsg.setText("Escriba un tipo de cliente del cat\u00e1logo");
    }
}

private boolean edadValida(String texto) {
    try {
        int n = Integer.parseInt(texto);
        return n >= 0 && n <= 255;
    } catch (NumberFormatException ex) {
        return false;
    }
}

private boolean tipoValido(String texto) {
    return buscarTipo(texto) != null;
}

private TipoCliente buscarTipo(String texto) {
    for (TipoCliente t : tipos) {
        if (t.getNombre().equalsIgnoreCase(texto)) return t;
    }
    return null;
}

private void guardar() {
   
    String nombre = txtNombre1.getText().trim();
    String edadTxt = txtEdad.getText().trim();
    String tipoTxt = txtTipo.getText().trim();
    
    nombre = nombre.replaceAll("\\s+", " ");
    tipoTxt = tipoTxt.replaceAll("\\s+", " ");
   
   boolean esTextoAyudaNombre = nombre.isEmpty() || nombre.startsWith("👤");
boolean esTextoAyudaEdad   = edadTxt.isEmpty() || edadTxt.startsWith("🎂");
boolean esTextoAyudaTipo   = tipoTxt.isEmpty()  || tipoTxt.startsWith("👥");
    
    if (esTextoAyudaNombre) {
        avisar("Ingrese el nombre del cliente.");
        txtNombre1.requestFocus();
        return;
    }
    if (esTextoAyudaTipo) {
        avisar("Escriba el tipo de cliente.");
        txtTipo.requestFocus();
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
    
    Integer edad;
    try {
        edad = esTextoAyudaEdad ? null : Integer.valueOf(edadTxt);
        if (edad != null && (edad < 0 || edad > 255)) {
            avisar("La edad debe ser entre 0 y 255.");
            txtEdad.requestFocus();
            return;
        }
    } catch (NumberFormatException e) {
        avisar("Escribe un número válido en la edad.");
        txtEdad.requestFocus();
        return;
    }
    
    TipoCliente tipo = buscarTipo(tipoTxt);
    if (tipo == null) {
        avisar("Ese tipo de cliente no existe en el cat\u00e1logo.");
        txtTipo.requestFocus();
        return;
    }
    
    try {
        dao.guardar(new Cliente(nombre, edad, tipo));
        JOptionPane.showMessageDialog(this, "Cliente registrado correctamente.",
                "Registro", JOptionPane.INFORMATION_MESSAGE);
        limpiar(); 
    } catch (Exception ex) {
        mostrarError("No se pudo guardar el cliente", ex);
    }
}

private void limpiar() {
    
    txtNombre1.setText("  👤    Ej. María Lopez");
    txtNombre1.setForeground(new java.awt.Color(150, 150, 150));
    
    txtEdad.setText("  🎂     Ej. 25");
    txtEdad.setForeground(new java.awt.Color(150, 150, 150));
    
    
    txtTipo.setText("  👥    Ej. Cliente Frecuente");
    txtTipo.setForeground(new java.awt.Color(150, 150, 150));
    lblEdadMsg.setText("");
    lblTipoMsg.setText("");
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
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes1 = new javax.swing.JTable();
        actualizarNombre = new javax.swing.JButton();
        eliminarPorNombre1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtNombre1 = new javax.swing.JTextField();
        txtEdad = new javax.swing.JTextField();
        lblEdadMsg = new javax.swing.JLabel();
        txtTipo = new javax.swing.JTextField();
        lblTipoMsg = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        lblTitulo3 = new javax.swing.JLabel();
        lblTitulo2 = new javax.swing.JLabel();
        lblTitulo4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblTitulo1 = new javax.swing.JLabel();

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

        jPanel3.setBackground(new java.awt.Color(255, 251, 250));
        jPanel3.setToolTipText("Estrella Peluda - Registro de clientes");

        jScrollPane1.setBackground(new java.awt.Color(116, 96, 153));
        jScrollPane1.setForeground(new java.awt.Color(255, 255, 255));

        tblClientes1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblClientes1);

        actualizarNombre.setBackground(new java.awt.Color(253, 252, 253));
        actualizarNombre.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        actualizarNombre.setText("Editar");
        actualizarNombre.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        actualizarNombre.addActionListener(this::actualizarNombreActionPerformed);

        eliminarPorNombre1.setBackground(new java.awt.Color(79, 57, 122));
        eliminarPorNombre1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        eliminarPorNombre1.setForeground(new java.awt.Color(255, 255, 255));
        eliminarPorNombre1.setText("Eliminar");
        eliminarPorNombre1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        eliminarPorNombre1.addActionListener(this::eliminarPorNombre1ActionPerformed);

        jPanel4.setBackground(new java.awt.Color(248, 246, 252));
        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));

        txtNombre1.setBackground(new java.awt.Color(239, 235, 245));
        txtNombre1.setForeground(new java.awt.Color(102, 102, 102));
        txtNombre1.setText("👤    Ej. María Lopez");
        txtNombre1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        txtNombre1.addActionListener(this::txtNombre1ActionPerformed);

        txtEdad.setBackground(new java.awt.Color(239, 235, 245));
        txtEdad.setForeground(new java.awt.Color(102, 102, 102));
        txtEdad.setText("🎂     Ej. 25");
        txtEdad.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));

        lblEdadMsg.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblEdadMsg.setForeground(new java.awt.Color(204, 204, 204));

        txtTipo.setBackground(new java.awt.Color(239, 235, 245));
        txtTipo.setForeground(new java.awt.Color(102, 102, 102));
        txtTipo.setText("👥    Ej. Cliente Frecuente");
        txtTipo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        txtTipo.addActionListener(this::txtTipoActionPerformed);

        lblTipoMsg.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblTipoMsg.setForeground(new java.awt.Color(204, 204, 204));



        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Tipo de cliente");

        jLabel9.setForeground(new java.awt.Color(252, 0, 0));
        jLabel9.setText("*");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Edad");

        jLabel11.setForeground(new java.awt.Color(252, 0, 0));
        jLabel11.setText("*");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Nombre");

        jLabel13.setForeground(new java.awt.Color(252, 0, 0));
        jLabel13.setText("*");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Nuevo Cliente");

        btnGuardar.setBackground(new java.awt.Color(79, 57, 122));
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar");
        btnGuardar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setOpaque(true);
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnCancelar.setBackground(new java.awt.Color(253, 252, 253));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(55, 38, 88));
        btnCancelar.setText("Cancelar");
        btnCancelar.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(116, 96, 153), 2, true));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);


        lblTitulo3.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo3.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo3.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo3.setText("  👤");

        lblTitulo2.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo2.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10)

        );
        lblTitulo2.setForeground(new java.awt.Color(79, 57, 122));
        lblTitulo2.setText("🎂");

        lblTitulo4.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo4.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 10
        )

    );
    lblTitulo4.setForeground(new java.awt.Color(79, 57, 122));
    lblTitulo4.setText("👥");

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblEdadMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTipo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblTipoMsg, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(27, 27, 27)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addComponent(lblTitulo4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(39, 39, 39)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(lblTitulo3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel12)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(lblTitulo2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addContainerGap(31, Short.MAX_VALUE))
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addGap(14, 14, 14)
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addComponent(lblTitulo3, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel10)
                .addComponent(jLabel11)
                .addComponent(lblTitulo2))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblEdadMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel8)
                .addComponent(lblTitulo4)
                .addComponent(jLabel9))
            .addGap(7, 7, 7)
            .addComponent(txtTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(lblTipoMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap())
    );

    jLabel14.setForeground(new java.awt.Color(175, 175, 175));
    jLabel14.setText("Completa la información del cliente para guardar en el sistema.");

    jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
    jLabel2.setForeground(new java.awt.Color(116, 96, 153));
    jLabel2.setText("Registro de clientes ");

    lblTitulo1.setBackground(new java.awt.Color(0, 0, 0));
    lblTitulo1.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 40)
    );
    lblTitulo1.setForeground(new java.awt.Color(79, 57, 122));
    lblTitulo1.setText("  👤");

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGap(30, 30, 30)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(553, 553, 553)
                    .addComponent(actualizarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(eliminarPorNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(lblTitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addContainerGap(30, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel3Layout.createSequentialGroup()
            .addGap(16, 16, 16)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(lblTitulo1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel14)))
            .addGap(18, 18, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(actualizarNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(eliminarPorNombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(23, 23, 23))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTipoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTipoActionPerformed

    private void eliminarPorNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarPorNombre1ActionPerformed
    
           int fila = tblClientes1.getSelectedRow();
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
           dao.eliminar(clientes.get(tblClientes1.convertRowIndexToModel(fila)).getId());
            JOptionPane.showMessageDialog(this, "Cliente eliminado ✅");
            cargarClientes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    }//GEN-LAST:event_eliminarPorNombre1ActionPerformed

    private void actualizarNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizarNombreActionPerformed
                                              
    int fila = tblClientes1.getSelectedRow();
    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla");
        return;
    }
    
    // ✅ Obtener TODOS los datos de la fila, NO solo el nombre
    Cliente seleccionado = clientes.get(tblClientes1.convertRowIndexToModel(fila));

    VentanaEditarCliente ventana = new VentanaEditarCliente(seleccionado);
    ventana.setVisible(true);
    ventana.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            cargarClientes(); // ✅ Recarga todo al cerrar
        }
    });

    }//GEN-LAST:event_actualizarNombreActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
    limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
     guardar();
    cargarClientes();

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtNombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombre1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombre1ActionPerformed

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
    private javax.swing.JButton eliminarPorNombre1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEdadMsg;
    private javax.swing.JLabel lblTipoMsg;
    private javax.swing.JLabel lblTitulo1;
    private javax.swing.JLabel lblTitulo2;
    private javax.swing.JLabel lblTitulo3;
    private javax.swing.JLabel lblTitulo4;
    private javax.swing.JTable tblClientes1;
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
    private int totalClientes = 0;
    private List<Cliente> clientes = new ArrayList<>();

private void configurarTabla() {
    modeloTabla = new DefaultTableModel(
            new Object[]{"Nombre", "Edad", "Tipo de cliente"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;   
        }
    };
    tblClientes1.setModel(modeloTabla);
    tblClientes1.getTableHeader().setReorderingAllowed(false);
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
                List<Cliente> lista = get();
                clientes = lista;
                modeloTabla.setRowCount(0);
                for (Cliente c : lista) {
                    modeloTabla.addRow(new Object[]{
                        c.getNombre(),
                        c.getEdad(),
                        c.getTipoCliente().getNombre()
                    });
                }
                totalClientes = lista.size();
               
            } catch (Exception ex) {
                mostrarError("No se pudieron cargar los clientes", ex);
            }
        }
    }.execute();
}
private void configurarBotonGuardar() {
    // Solo efectos al pasar el cursor
    btnGuardar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    btnGuardar.addMouseListener(new java.awt.event.MouseAdapter() {
        
    });
}

private void configurarBotonCancelar() {
    // Solo efectos al pasar el cursor
    btnCancelar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    btnCancelar.addMouseListener(new java.awt.event.MouseAdapter() {

    });

        txtNombre1.setText("  👤    Ej. María Lopez");  
        txtNombre1.setForeground(new java.awt.Color(150, 150, 150));
        
        txtNombre1.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtNombre1.getText().equals("  👤    Ej. María Lopez")) {
                    txtNombre1.setText("");
                    txtNombre1.setForeground(new java.awt.Color(50, 50, 50));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtNombre1.getText().isEmpty()) {
                    txtNombre1.setText("  👤    Ej. María Lopez");
                    txtNombre1.setForeground(new java.awt.Color(150, 150, 150));
                }
            }
        });

        txtEdad.setText("  🎂     Ej. 25");  
        txtEdad.setForeground(new java.awt.Color(150, 150, 150));
        
        txtEdad.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtEdad.getText().equals("  🎂     Ej. 25")) {
                    txtEdad.setText("");
                    txtEdad.setForeground(new java.awt.Color(50, 50, 50));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtEdad.getText().isEmpty()) {
                    txtEdad.setText("  🎂     Ej. 25");
                    txtEdad.setForeground(new java.awt.Color(150, 150, 150));
                }
            }
        });

        txtTipo.setText("  👥    Ej. Cliente Frecuente");  
        txtTipo.setForeground(new java.awt.Color(150, 150, 150));
        
        txtTipo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtTipo.getText().equals("  👥    Ej. Cliente Frecuente")) {
                    txtTipo.setText("");
                    txtTipo.setForeground(new java.awt.Color(50, 50, 50));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtTipo.getText().isEmpty()) {
                    txtTipo.setText("  👥    Ej. Cliente Frecuente");
                    txtTipo.setForeground(new java.awt.Color(150, 150, 150));
                }
            }
        });
}

        public int getTotalClientes() {
        return totalClientes;
    }

}
