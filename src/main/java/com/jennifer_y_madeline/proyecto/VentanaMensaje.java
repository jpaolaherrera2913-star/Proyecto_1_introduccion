package com.jennifer_y_madeline.proyecto;
import javax.swing.*;
import java.awt.*;

public class VentanaMensaje extends JFrame {
    public VentanaMensaje(String mensaje) {
        setTitle("Mensaje del Sistema");
        setSize(380, 180);  // Un poquito más ancho para que respire
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // === PANEL CON EL MISMO FONDO GRIS SUAVE ===
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(235, 235, 235)); // Igual que tu pantalla
        add(panel);

        // === TEXTO CENTRADO, MISMA FUENTE Y ESTILO ===
        JLabel lblMensaje = new JLabel(mensaje);
        lblMensaje.setFont(new Font("Arial", Font.PLAIN, 15));
        lblMensaje.setForeground(new Color(60, 60, 60)); // Gris oscuro suave
        lblMensaje.setBounds(25, 55, 330, 50); // Espacio para mensajes de 2 líneas
        lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblMensaje);
    }
}