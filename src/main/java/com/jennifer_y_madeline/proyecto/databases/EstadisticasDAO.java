package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasDAO {

    public record Resumen(long totalClientes, long totalVentas, long productosVendidos,
                          BigDecimal promedio, BigDecimal ventaMayor, BigDecimal ventaMenor,
                          BigDecimal totalVendido) { }

    public record PorTipoCliente(String tipo, long clientes, long ventas, BigDecimal total) { }

    public record PorMetodoPago(String metodo, long ventas, long productos, BigDecimal total) { }

    /** Total de clientes, total de ventas, productos vendidos, promedio, mayor y menor. */
    public Resumen resumen() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Object[] r = (Object[]) em.createNativeQuery(
                    "SELECT total_clientes, total_ventas, productos_vendidos, promedio_venta, "
                  + "venta_mayor, venta_menor, total_vendido FROM v_estadisticas_generales")
                    .getSingleResult();
            return new Resumen(lng(r[0]), lng(r[1]), lng(r[2]), dec(r[3]), dec(r[4]), dec(r[5]), dec(r[6]));
        } finally {
            em.close();
        }
    }

    /** Clasificación de clientes por tipo. */
    public List<PorTipoCliente> clasificacionClientes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<?> filas = em.createNativeQuery(
                    "SELECT tipo_cliente, clientes, ventas, total_vendido FROM v_clasificacion_clientes")
                    .getResultList();
            List<PorTipoCliente> lista = new ArrayList<>();
            for (Object f : filas) {
                Object[] r = (Object[]) f;
                lista.add(new PorTipoCliente((String) r[0], lng(r[1]), lng(r[2]), dec(r[3])));
            }
            return lista;
        } finally {
            em.close();
        }
    }

    /** Estadísticas por método de pago. */
    public List<PorMetodoPago> porMetodoPago() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<?> filas = em.createNativeQuery(
                    "SELECT metodo_pago, ventas, productos, total_vendido FROM v_estadisticas_metodo_pago")
                    .getResultList();
            List<PorMetodoPago> lista = new ArrayList<>();
            for (Object f : filas) {
                Object[] r = (Object[]) f;
                lista.add(new PorMetodoPago((String) r[0], lng(r[1]), lng(r[2]), dec(r[3])));
            }
            return lista;
        } finally {
            em.close();
        }
    }

    private static long lng(Object o) {
        return o == null ? 0 : ((Number) o).longValue();
    }

    private static BigDecimal dec(Object o) {
        return o == null ? BigDecimal.ZERO : new BigDecimal(o.toString()).setScale(2, RoundingMode.HALF_UP);
    }
}
