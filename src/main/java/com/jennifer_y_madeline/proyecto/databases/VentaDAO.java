package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.List;

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
}
