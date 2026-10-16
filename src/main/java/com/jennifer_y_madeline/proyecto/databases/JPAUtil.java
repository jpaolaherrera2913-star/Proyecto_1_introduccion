package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {

    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("ventasPU");

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public static void cerrar() {
        if (EMF.isOpen()) {
            EMF.close();
        }
    }
}
