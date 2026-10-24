package com.jennifer_y_madeline.proyecto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;


public final class EstadoCaja {

    private static final Preferences PREFS = Preferences.userNodeForPackage(EstadoCaja.class);
    private static final String CLAVE_FECHA = "fechaCierre";
    private static final String CLAVE_HORA = "horaCierre";

    private EstadoCaja() {
    }

    public static boolean estaCerrada() {
        return LocalDate.now().toString().equals(PREFS.get(CLAVE_FECHA, ""));
    }

    public static String horaCierre() {
        return estaCerrada() ? PREFS.get(CLAVE_HORA, "") : "";
    }

    public static void cerrar() {
        PREFS.put(CLAVE_FECHA, LocalDate.now().toString());
        PREFS.put(CLAVE_HORA, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    public static void reabrir() {
        PREFS.remove(CLAVE_FECHA);
        PREFS.remove(CLAVE_HORA);
    }
}
