package com.jennifer_y_madeline.proyecto;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Fondo de la ventana de login: degradado crema con huellitas y huesitos
 * decorativos en lila. Solo dibuja el fondo; no tiene componentes propios.
 */
public class FondoLogin extends JPanel {

    private static final Color LILA = new Color(160, 135, 210);

    private JComponent tarjeta;   // opcional: para dibujarle una sombra suave

    public FondoLogin() {
        setOpaque(true);
    }

    public void setTarjeta(JComponent tarjeta) {
        this.tarjeta = tarjeta;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Degradado crema -> lavanda muy suave
        g2.setPaint(new GradientPaint(0, 0, new Color(255, 248, 238),
                w, h, new Color(243, 236, 250)));
        g2.fillRect(0, 0, w, h);

        // Decoraciones (x, y en proporción del tamaño, tamaño en px, rotación en grados)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.40f));
        g2.setColor(LILA);
        huella(g2, w * 0.045, h * 0.09, 30, -20);
        huella(g2, w * 0.47, h * 0.07, 24, 15);
        huella(g2, w * 0.965, h * 0.045, 30, 15);
        huella(g2, w * 0.06, h * 0.93, 26, 20);
        huella(g2, w * 0.53, h * 0.955, 26, -15);
        huella(g2, w * 0.975, h * 0.955, 30, -10);
        hueso(g2, w * 0.04, h * 0.52, 44, 55);
        hueso(g2, w * 0.30, h * 0.93, 46, -25);
        hueso(g2, w * 0.455, h * 0.50, 36, 35);

        // Sombra suave detrás de la tarjeta del formulario
        if (tarjeta != null && tarjeta.isShowing()) {
            int x = tarjeta.getX();
            int y = tarjeta.getY();
            int tw = tarjeta.getWidth();
            int th = tarjeta.getHeight();
            for (int i = 1; i <= 7; i++) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.035f));
                g2.setColor(new Color(90, 60, 140));
                g2.fill(new RoundRectangle2D.Double(x - i + 2, y - i + 5, tw + 2 * i, th + 2 * i, 14, 14));
            }
        }
        g2.dispose();
    }

    /** Huellita: una almohadilla grande + cuatro deditos. cx, cy = centro; s = tamaño. */
    private void huella(Graphics2D g2, double cx, double cy, double s, double grados) {
        AffineTransform viejo = g2.getTransform();
        g2.translate(cx, cy);
        g2.rotate(Math.toRadians(grados));
        g2.fill(new Ellipse2D.Double(-s * 0.32, -s * 0.05, s * 0.64, s * 0.52));          // almohadilla
        g2.fill(new Ellipse2D.Double(-s * 0.52, -s * 0.30, s * 0.24, s * 0.32));          // dedo izq.
        g2.fill(new Ellipse2D.Double(-s * 0.27, -s * 0.52, s * 0.24, s * 0.34));          // dedo centro izq.
        g2.fill(new Ellipse2D.Double(s * 0.03, -s * 0.52, s * 0.24, s * 0.34));           // dedo centro der.
        g2.fill(new Ellipse2D.Double(s * 0.28, -s * 0.30, s * 0.24, s * 0.32));           // dedo der.
        g2.setTransform(viejo);
    }

    /** Huesito: barra redonda con dos bolitas en cada punta. */
    private void hueso(Graphics2D g2, double cx, double cy, double largo, double grados) {
        AffineTransform viejo = g2.getTransform();
        g2.translate(cx, cy);
        g2.rotate(Math.toRadians(grados));
        double grosor = largo * 0.22;
        double r = largo * 0.14;
        g2.fill(new RoundRectangle2D.Double(-largo / 2, -grosor / 2, largo, grosor, grosor, grosor));
        for (int lado = -1; lado <= 1; lado += 2) {
            double px = lado * largo / 2;
            g2.fill(new Ellipse2D.Double(px - r, -r * 1.35, r * 2, r * 1.3 * 1.0));
            g2.fill(new Ellipse2D.Double(px - r, r * 0.05, r * 2, r * 1.3));
        }
        g2.setTransform(viejo);
    }
}
