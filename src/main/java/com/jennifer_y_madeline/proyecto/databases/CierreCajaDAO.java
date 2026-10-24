package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CierreCajaDAO {

    public record Cierre(LocalDate fecha, LocalDateTime horaCierre, int numVentas, int productosVendidos,
                         BigDecimal subtotal, BigDecimal descuentos, BigDecimal recargos, BigDecimal total,
                         int efectivoVentas, BigDecimal efectivoTotal,
                         int tarjetaVentas, BigDecimal tarjetaTotal,
                         int transferenciaVentas, BigDecimal transferenciaTotal,
                         BigDecimal ventaMayor, BigDecimal ventaMenor, BigDecimal promedio) { }

    /** Usado por el panel Inicio ("Estado de caja") y por Registrar Ventas. */
    public boolean estaCerrada(LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return estaCerrada(em, fecha);
        } finally {
            em.close();
        }
    }

    /** Versión que reutiliza el EntityManager de una transacción en curso (VentaDAO). */
    public static boolean estaCerrada(EntityManager em, LocalDate fecha) {
        Number n = (Number) em.createNativeQuery("SELECT COUNT(*) FROM cierre_caja WHERE fecha = ?1")
                .setParameter(1, java.sql.Date.valueOf(fecha))
                .getSingleResult();
        return n.intValue() > 0;
    }

    /** Cierra la caja del día. Lanza IllegalArgumentException con un mensaje listo para mostrar. */
    public Cierre cerrar(LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("CALL sp_cierre_caja(?1)")
              .setParameter(1, java.sql.Date.valueOf(fecha))
              .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalArgumentException(mensaje(e), e);
        } finally {
            em.close();
        }
        return buscar(fecha);
    }

    /** Reabre la caja de ese día: borra su registro de cierre para poder volver a vender. */
    public void reabrir(LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.createNativeQuery("DELETE FROM cierre_caja WHERE fecha = ?1")
              .setParameter(1, java.sql.Date.valueOf(fecha))
              .executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalArgumentException(mensaje(e), e);
        } finally {
            em.close();
        }
    }

    /** Devuelve el resumen del cierre de esa fecha, o null si no existe. */
    public Cierre buscar(LocalDate fecha) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            var filas = em.createNativeQuery(
                    "SELECT fecha, hora_cierre, num_ventas, productos_vendidos, subtotal, descuentos, recargos, total, "
                  + "efectivo_ventas, efectivo_total, tarjeta_ventas, tarjeta_total, "
                  + "transferencia_ventas, transferencia_total, venta_mayor, venta_menor, promedio "
                  + "FROM cierre_caja WHERE fecha = ?1")
                    .setParameter(1, java.sql.Date.valueOf(fecha))
                    .getResultList();
            if (filas.isEmpty()) {
                return null;
            }
            Object[] r = (Object[]) filas.get(0);
            return new Cierre(fecha, fechaHora(r[1]), ent(r[2]), ent(r[3]),
                    dec(r[4]), dec(r[5]), dec(r[6]), dec(r[7]),
                    ent(r[8]), dec(r[9]), ent(r[10]), dec(r[11]),
                    ent(r[12]), dec(r[13]), dec(r[14]), dec(r[15]), dec(r[16]));
        } finally {
            em.close();
        }
    }

    /** Extrae el mensaje del SIGNAL de MySQL (la causa más profunda). */
    public static String mensaje(Throwable t) {
        Throwable raiz = t;
        while (raiz.getCause() != null && raiz.getCause() != raiz) {
            raiz = raiz.getCause();
        }
        return raiz.getMessage() != null ? raiz.getMessage() : "Ocurrió un error inesperado.";
    }

    private static int ent(Object o) {
        return o == null ? 0 : ((Number) o).intValue();
    }

    private static BigDecimal dec(Object o) {
        return o == null ? BigDecimal.ZERO : new BigDecimal(o.toString()).setScale(2, RoundingMode.HALF_UP);
    }

    private static LocalDateTime fechaHora(Object o) {
        if (o instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        }
        return (LocalDateTime) o;
    }
}
