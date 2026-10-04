package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Crea una sola EntityManagerFactory para toda la aplicación.
 * Uso: EntityManager em = JPAUtil.getEntityManager(); ... em.close();
 */
public final class JPAUtil {

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("ventasPU");

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /** Llamar al cerrar la aplicación (por ejemplo, en windowClosing del JFrame principal). */
    public static void cerrar() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}
