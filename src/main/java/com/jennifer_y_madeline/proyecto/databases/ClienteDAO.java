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
                Long ventas = em.createQuery("SELECT COUNT(v) FROM Venta v WHERE v.cliente.id = :id", Long.class).setParameter("id", id).getSingleResult();
                if (ventas > 0) throw new IllegalArgumentException("No se puede eliminar un cliente con ventas registradas.");
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
    }    
    public void actualizarCompleto(Integer id, String nombre, Integer edad, TipoCliente tipo) {
       
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
