package com.jennifer_y_madeline.proyecto;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Carga los logos de la tienda. Busca el archivo junto a las clases
 * (paquete com.jennifer_y_madeline.proyecto) o en /imagenes/.
 *
 *  - logo.png  : logo con eslogan (pantalla de login)
 *  - logo2.png : logo para el menú y la lista de productos
 */
public final class Imagenes {

    private Imagenes() {
    }

    private static URL buscar(String archivo) {
        URL url = Imagenes.class.getResource(archivo);              // mismo paquete
        if (url == null) {
            url = Imagenes.class.getResource("/imagenes/" + archivo); // carpeta imagenes
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

    /** Logo con eslogan, escalado al ancho indicado, o null si no se encuentra. */
    public static ImageIcon logo(int ancho) {
        return cargar("logo.png", ancho);
    }

    /** Logo 2, escalado al ancho indicado, o null si no se encuentra. */
    public static ImageIcon logo2(int ancho) {
        return cargar("logo2.png", ancho);
    }

    /** Imagen del perro y el gato, escalada al ancho indicado, o null si no se encuentra. */
    public static ImageIcon perroYGato(int ancho) {
        return cargar("perro_y_gato.png", ancho);
    }

    /** Reemplaza el texto "imagen perroygato" del label por perro_y_gato.png. Si falla, deja el texto. */
    public static void ponerPerroYGato(JLabel label, int ancho) {
        poner(label, "perro_y_gato.png", ancho);
    }

    /** Reemplaza el texto "logo" del label por logo.png. Si falla, deja el texto. */
    public static void ponerLogo(JLabel label, int ancho) {
        poner(label, "logo.png", ancho);
    }

    /** Reemplaza el texto "logo" del label por logo2.png. Si falla, deja el texto. */
    public static void ponerLogo2(JLabel label, int ancho) {
        poner(label, "logo2.png", ancho);
    }
}
