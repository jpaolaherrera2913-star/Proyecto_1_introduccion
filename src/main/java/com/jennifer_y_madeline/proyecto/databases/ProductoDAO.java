package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Producto p ORDER BY p.nombre, p.id", Producto.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public void guardar(Producto p) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void actualizar(Integer id, String nombre, Integer cantidad, BigDecimal precio) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto p = em.find(Producto.class, id);
            if (p == null) {
                throw new IllegalArgumentException("Producto no encontrado.");
            }
            p.setNombre(nombre);
            p.setCantidad(cantidad);
            p.setPrecioUnitario(precio);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Suma unidades al stock de un producto y devuelve el nuevo total. */
    public int agregarStock(Integer id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser mayor a 0.");
        }
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto p = em.find(Producto.class, id, LockModeType.PESSIMISTIC_WRITE);
            if (p == null) {
                throw new IllegalArgumentException("Producto no encontrado.");
            }
            p.setCantidad(p.getCantidad() + cantidad);
            em.getTransaction().commit();
            return p.getCantidad();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminar(Integer id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Producto p = em.find(Producto.class, id);
            if (p != null) {
                Long ventas = em.createQuery("SELECT COUNT(v) FROM Venta v WHERE v.producto.id = :id", Long.class)
                                .setParameter("id", id).getSingleResult();
                if (ventas > 0) {
                    throw new IllegalArgumentException("No se puede eliminar un producto con ventas registradas.");
                }
                em.remove(p);
            }
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
