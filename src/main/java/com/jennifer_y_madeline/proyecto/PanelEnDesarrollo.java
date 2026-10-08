package com.jennifer_y_madeline.proyecto;

public class PanelEnDesarrollo extends javax.swing.JPanel {
    public PanelEnDesarrollo(String titulo) {
        setBackground(new java.awt.Color(28, 28, 28));
        setLayout(new java.awt.GridBagLayout());
        javax.swing.JLabel l = new javax.swing.JLabel(titulo + " - en desarrollo");
        l.setForeground(java.awt.Color.WHITE);
        l.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        add(l);
    }
}
