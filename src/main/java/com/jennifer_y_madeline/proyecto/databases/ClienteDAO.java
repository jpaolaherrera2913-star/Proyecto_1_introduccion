package com.jennifer_y_madeline.proyecto.databases;

import jakarta.persistence.EntityManager;
import java.util.List;

public class ClienteDAO {

    public List<TipoCliente> listarTipos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT t FROM TipoCliente t ORDER BY t.nombre", TipoCliente.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<MetodoPago> listarMetodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT m FROM MetodoPago m ORDER BY m.nombre", MetodoPago.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Cliente> listar() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cliente c ORDER BY c.nombre", Cliente.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public void guardar(Cliente c) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (c.getId() == null) {
                em.persist(c);
            } else {
                em.merge(c);
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

    public void eliminar(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, id);
            if (c != null) {
                em.remove(c);
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
    }    // === ACTUALIZAR CLIENTE usando JPA ===
    public void actualizarNombre(String nombreAntiguo, String nombreNuevo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Buscar el cliente por nombre
            Cliente cliente = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.nombre = :nom", Cliente.class)
                .setParameter("nom", nombreAntiguo)
                .setMaxResults(1)
                .getSingleResult();
            
            // Cambiar el nombre
            cliente.setNombre(nombreNuevo.trim());
            
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

    // === ELIMINAR CLIENTE usando JPA ===
    public void eliminarPorNombre(String nombre) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            
            // Buscar el cliente por nombre
            Cliente cliente = em.createQuery(
                "SELECT c FROM Cliente c WHERE c.nombre = :nom", Cliente.class)
                .setParameter("nom", nombre)
                .setMaxResults(1)
                .getSingleResult();
            
            // Eliminar
            if (cliente != null) {
                em.remove(cliente);
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
     // === ACTUALIZAR TODO EL CLIENTE ===
    public void actualizarCompleto(Integer id, String nombre, int edad, TipoCliente tipo, MetodoPago pago) {
        // ✅ Faltaba crear la variable "em"
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Cliente c = em.find(Cliente.class, id);
            if (c == null) {
                throw new RuntimeException("Cliente no encontrado");
            }
            
            c.setNombre(nombre);
            c.setEdad(edad);
            c.setTipoCliente(tipo);
            c.setMetodoPago(pago);
            
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close(); // ✅ Cerramos siempre
        }
    }
}
