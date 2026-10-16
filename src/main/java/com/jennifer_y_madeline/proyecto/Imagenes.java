package com.jennifer_y_madeline.proyecto;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public final class Imagenes {

    private Imagenes() {
    }

    private static URL buscar(String archivo) {
        URL url = Imagenes.class.getResource(archivo);             
        if (url == null) {
            url = Imagenes.class.getResource("/imagenes/" + archivo); 
        }
        return url;
    }

    private static ImageIcon cargar(String archivo, int ancho) {
        URL url = buscar(archivo);
        if (url == null) {
            System.err.println("No se encontro " + archivo);
            return null;
        }
        ImageIcon original = new ImageIcon(url);
        int alto = Math.max(1, ancho * original.getIconHeight() / original.getIconWidth());
        Image escalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(escalada);
    }

    private static void poner(JLabel label, String archivo, int ancho) {
        ImageIcon icono = cargar(archivo, ancho);
        if (icono != null) {
            label.setText("");
            label.setIcon(icono);
        }
    }

    public static ImageIcon logo(int ancho) {
        return cargar("logo.png", ancho);
    }

    public static ImageIcon logo2(int ancho) {
        return cargar("logo2.png", ancho);
    }

    public static ImageIcon perroYGato(int ancho) {
        return cargar("perro_y_gato.png", ancho);
    }

    public static void ponerPerroYGato(JLabel label, int ancho) {
        poner(label, "perro_y_gato.png", ancho);
    }

    public static void ponerLogo(JLabel label, int ancho) {
        poner(label, "logo.png", ancho);
    }

    public static void ponerLogo2(JLabel label, int ancho) {
        poner(label, "logo2.png", ancho);
    }
}
