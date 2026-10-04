package com.jennifer_y_madeline.proyecto;

import com.jennifer_y_madeline.proyecto.databases.Cliente;
import com.jennifer_y_madeline.proyecto.databases.ClienteDAO;
import com.jennifer_y_madeline.proyecto.databases.MetodoPago;
import com.jennifer_y_madeline.proyecto.databases.TipoCliente;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * Ventana modal para registrar un cliente.
 * Uso: new VentanaRegistroCliente(ventanaPadre).setVisible(true);
 * (ventanaPadre puede ser null)
 */
public class VentanaRegistroCliente extends JDialog {

    private final ClienteDAO dao = new ClienteDAO();

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtEdad = new JTextField(5);
    private final JComboBox<TipoCliente> cboTipo = new JComboBox<>();
    private final JComboBox<MetodoPago> cboMetodo = new JComboBox<>();
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnCancelar = new JButton("Cancelar");

    public VentanaRegistroCliente(Frame padre) {
        super(padre, "Registrar cliente", true);
        construirInterfaz();
        cargarCombos();
        pack();
        setLocationRelativeTo(padre);
    }

    private void construirInterfaz() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;

        agregarFila(form, g, 0, "Nombre:", txtNombre);
        agregarFila(form, g, 1, "Edad:", txtEdad);
        agregarFila(form, g, 2, "Tipo de cliente:", cboTipo);
        agregarFila(form, g, 3, "Método de pago:", cboMetodo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar);
        botones.add(btnGuardar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnGuardar);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void agregarFila(JPanel p, GridBagConstraints g, int fila, String etiqueta, java.awt.Component campo) {
        g.gridx = 0;
        g.gridy = fila;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        p.add(new JLabel(etiqueta), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        p.add(campo, g);
    }

    /** Carga los combos en segundo plano para no congelar la ventana. */
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
                    for (TipoCliente t : (List<TipoCliente>) r[0]) cboTipo.addItem(t);
                    for (MetodoPago m : (List<MetodoPago>) r[1]) cboMetodo.addItem(m);
                    btnGuardar.setEnabled(true);
                } catch (Exception ex) {
                    mostrarError("No se pudieron cargar los catálogos", ex);
                }
            }
        }.execute();
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String edadTxt = txtEdad.getText().trim();
        TipoCliente tipo = (TipoCliente) cboTipo.getSelectedItem();
        MetodoPago metodo = (MetodoPago) cboMetodo.getSelectedItem();

        // Validaciones
        if (nombre.isEmpty()) {
            avisar("Ingrese el nombre del cliente.");
            txtNombre.requestFocus();
            return;
        }
        Integer edad = null;
        if (!edadTxt.isEmpty()) {
            try {
                edad = Integer.valueOf(edadTxt);
                if (edad < 0 || edad > 120) {
                    avisar("La edad debe estar entre 0 y 120.");
                    txtEdad.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                avisar("La edad debe ser un número.");
                txtEdad.requestFocus();
                return;
            }
        }
        if (tipo == null || metodo == null) {
            avisar("Seleccione el tipo de cliente y el método de pago.\n"
                 + "Si las listas están vacías, primero inserte datos en esas tablas.");
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
        txtNombre.setText("");
        txtEdad.setText("");
        if (cboTipo.getItemCount() > 0) cboTipo.setSelectedIndex(0);
        if (cboMetodo.getItemCount() > 0) cboMetodo.setSelectedIndex(0);
        txtNombre.requestFocus();
    }

    private void avisar(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validación", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarError(String titulo, Exception ex) {
        Throwable causa = ex;
        while (causa.getCause() != null) causa = causa.getCause();
        JOptionPane.showMessageDialog(this, titulo + ":\n" + causa.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
