package com.jennifer_y_madeline.proyecto;

import com.jennifer_y_madeline.proyecto.databases.JPAUtil;

public class Proyecto {
    static boolean sistemaActivo = true;
    
   public static void main(String[] args) {
    if (!iniciarSesion()) {
        JPAUtil.cerrar();
        return;
    }
    do {
        VentanaMenu menu = new VentanaMenu();
        menu.setVisible(true);

        while (menu.isVisible()) {
            try { Thread.sleep(50); } catch (Exception e) {}
        }

        if (menu.opcion == 1) {
            sistemaActivo = iniciarSesion();
        } else {
            VentanaMensaje vSalida = new VentanaMensaje("Cerrando el sistema...", true);
            vSalida.setVisible(true);
            long fin = System.currentTimeMillis() + 5000;
            while (vSalida.isVisible() && System.currentTimeMillis() < fin) {
                try { Thread.sleep(50); } catch (Exception e) {}
            }
            if (vSalida.cancelado) {
                continue;   // vuelve a abrir el menú
            }
            vSalida.dispose();
            sistemaActivo = false;
        }
    } while (sistemaActivo);

    JPAUtil.cerrar();
    System.exit(0);
}

    static boolean iniciarSesion() {
        final String usuarioCorrecto = "admin";
        final String claveCorrecta = "1234";
        
        final int MAX_INTENTOS = 3;
        int intentos = 0;
        
        while (intentos < MAX_INTENTOS) {
            
            VentanaLogin login = new VentanaLogin();
            login.setLocationRelativeTo(null);
            login.setIntentosRestantes(MAX_INTENTOS - intentos);
            
            if (intentos > 0) {
                login.setMensaje("Datos incorrectos");
            } else {
                login.setMensaje("");
            }
            
            login.setVisible(true);
            
            while (login.isVisible()) {
                try { Thread.sleep(50); } catch (Exception e) {}
            }
            
            if (!login.entro) {
                return false;
            }
            
            if (login.usuarioIngresado.equals(usuarioCorrecto) &&
                login.claveIngresada.equals(claveCorrecta)) {
                
                VentanaMensaje vBien = new VentanaMensaje("¡Acceso concedido!");
                vBien.setVisible(true);
                try { Thread.sleep(2000); } catch (Exception e) {}
                vBien.dispose();
                return true;
            }
            
            intentos++;
            
            if (intentos >= MAX_INTENTOS) {
                VentanaMensaje vAgotado = new VentanaMensaje("️Se agotaron los 3 intentos.\n Saliendo del sistema...");
                vAgotado.setVisible(true);
                try { Thread.sleep(2000); } catch (Exception e) {}
                vAgotado.dispose();
                System.exit(0);
            }
        }
        
        return false;
    }
}