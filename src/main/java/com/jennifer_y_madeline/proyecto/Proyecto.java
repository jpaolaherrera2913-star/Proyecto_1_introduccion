package com.jennifer_y_madeline.proyecto;

public class Proyecto {
    static boolean sistemaActivo = true;
    
    public static void main(String[] args) {
        boolean acceso = iniciarSesion();
        
        if (!acceso) {
            return;
        }
        
        int opcionElegida;
        do {
            VentanaMenu menu = new VentanaMenu();
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
            
            while (menu.isVisible()) {
                try { Thread.sleep(50); } catch (Exception e) {}
            }
            
            opcionElegida = menu.opcion;
            
            if (opcionElegida == 1) {
                sistemaActivo = iniciarSesion();
            } else if (opcionElegida == 2) {
                System.out.println("Registrar clientes - En desarrollo");
            } else if (opcionElegida == 3 || opcionElegida == 4 || opcionElegida == 5) {
                System.out.println("Módulo disponible en próxima entrega");
            } else if (opcionElegida == 6) {
                System.out.println("Cierre de caja - En desarrollo");
            } else {
                sistemaActivo = false;
                VentanaMensaje vSalida = new VentanaMensaje("Cerrando el sistema...");
                vSalida.setVisible(true);
                try { Thread.sleep(1200); } catch (Exception e) {}
                vSalida.dispose();
                System.exit(0);
            }
        } while (sistemaActivo);
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
                try { Thread.sleep(1000); } catch (Exception e) {}
                vBien.dispose();
                return true;
            }
            
           
            intentos++;
            
            
            if (intentos >= MAX_INTENTOS) {
                VentanaMensaje vAgotado = new VentanaMensaje("Se agotaron los 3 intentos.\nSaliendo...");
                vAgotado.setVisible(true);
                try { Thread.sleep(1500); } catch (Exception e) {}
                vAgotado.dispose();
                System.exit(0);
            }
        }
        
        return false;
    }
}