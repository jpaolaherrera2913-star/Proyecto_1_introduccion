package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VentaDAO {
    public List<MetodoPago> listarMetodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT m FROM MetodoPago m ORDER BY m.nombre", MetodoPago.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }


    public List<Producto> listarProductos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Producto p ORDER BY p.nombre, p.id", Producto.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void guardar(List<Venta> ventas) {
        if (ventas.isEmpty()) throw new IllegalArgumentException("Agregue productos a la venta.");
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            for (Venta linea : ventas) {
                if (CierreCajaDAO.estaCerrada(em, linea.getFecha().toLocalDate())) {
                    throw new IllegalArgumentException(
                        "La caja de hoy ya fue cerrada. No se pueden registrar más ventas.");
                }
            }
            for (Venta linea : ventas) {
                Venta venta = new Venta();
                venta.setCliente(linea.getCliente());
                venta.setProducto(linea.getProducto());
                venta.setCantidad(linea.getCantidad());
                venta.setPrecioUnitario(linea.getPrecioUnitario());
                venta.setMetodoPago(linea.getMetodoPago());
                venta.setFecha(linea.getFecha());
                if (venta.getCantidad() == null || venta.getCantidad() <= 0
                        || venta.getPrecioUnitario() == null || venta.getPrecioUnitario().signum() < 0) {
                    throw new IllegalArgumentException("Cantidad o precio inv\u00e1lido.");
                }
                Producto producto = em.find(Producto.class, venta.getProducto().getId(), LockModeType.PESSIMISTIC_WRITE);
                if (producto == null || producto.getCantidad() < venta.getCantidad()) {
                    throw new IllegalArgumentException("Existencias insuficientes para " + venta.getProducto().getNombre());
                }
                producto.setCantidad(producto.getCantidad() - venta.getCantidad());
                venta.setProducto(producto);
                venta.setCliente(em.getReference(Cliente.class, venta.getCliente().getId()));
                venta.setMetodoPago(em.getReference(MetodoPago.class, venta.getMetodoPago().getId()));
                em.persist(venta);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<VentaResumen> listarResumenes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Venta> lineas = em.createQuery(
                    "SELECT v FROM Venta v ORDER BY v.fecha DESC, v.id ASC", Venta.class).getResultList();
            Map<String, VentaResumen> grupos = new LinkedHashMap<>();
            for (Venta v : lineas) {
                String clave = v.getCliente().getId() + "|" + v.getFecha() + "|" + v.getMetodoPago().getId();
                grupos.computeIfAbsent(clave, k -> new VentaResumen()).agregar(v);
            }
            return new ArrayList<>(grupos.values());
        } finally {
            em.close();
        }
    }

    public void eliminarVenta(List<Integer> idsLineas, boolean devolverStock) {
        if (idsLineas == null || idsLineas.isEmpty()) {
            throw new IllegalArgumentException("No hay venta para eliminar.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            for (Integer id : idsLineas) {
                Venta v = em.find(Venta.class, id);
                if (v == null) continue;
                if (CierreCajaDAO.estaCerrada(em, v.getFecha().toLocalDate())) {
                    throw new IllegalArgumentException(
                        "No se pueden eliminar ventas de un día con la caja cerrada.");
                }
                if (devolverStock) {
                    Producto p = em.find(Producto.class, v.getProducto().getId(), LockModeType.PESSIMISTIC_WRITE);
                    if (p != null) {
                        p.setCantidad(p.getCantidad() + v.getCantidad());
                    }
                }
                em.remove(v);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public Long contarVentasDelDia() {
        java.time.LocalDateTime inicio = java.time.LocalDate.now().atStartOfDay();
        java.time.LocalDateTime fin = inicio.plusDays(1);
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Venta> lineas = em.createQuery(
                    "SELECT v FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin", Venta.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getResultList();
            java.util.Set<String> ventas = new java.util.HashSet<>();
            for (Venta v : lineas) {
                ventas.add(v.getCliente().getId() + "|" + v.getFecha() + "|" + v.getMetodoPago().getId());
            }
            return (long) ventas.size();
        } finally {
            em.close();
        }
    }
}